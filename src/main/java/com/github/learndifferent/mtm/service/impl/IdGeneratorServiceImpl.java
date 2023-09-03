package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.IdGeneratorConstant;
import com.github.learndifferent.mtm.dto.id.Segment;
import com.github.learndifferent.mtm.dto.id.SegmentBuffer;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.IdGeneratorMapper;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ID Generator Service
 *
 * @author zhou
 * @date 2023/9/3
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IdGeneratorServiceImpl implements IdGeneratorService {

    private final IdGeneratorMapper idGeneratorMapper;

    /**
     * Key is the business tag,
     * value is the buffer that stores the ID information
     */
    private final Map<String, SegmentBuffer> tagsAndIdsCache = new ConcurrentHashMap<>();

    /**
     * true if initialization is successful
     */
    private volatile boolean initSuccess = false;

    /**
     * Executor
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(3,
            8,
            60L,
            TimeUnit.SECONDS,
            // SynchronousQueue<Runnable>():
            // Only one thread execute a task for the tag.
            // The task is executed immediately and once completed, it is immediately retrieved.
            // All others can only wait in a blocking manner
            new SynchronousQueue<>(),
            new UpdateSegmentThreadFactory()
    );

    public static class UpdateSegmentThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }
    }

    @Override
    public boolean init() {
        log.info("Initializing ID Generator Service");
        // update cache to finish the initialization
        updateCacheFromDb();
        initSuccess = true;
        // start daemon threads to update the cache frequently
        updateCacheFromDbEveryMinute();
        return initSuccess;
    }

    private void updateCacheFromDb() {
        log.info("updating cache from db");
        List<String> dbTags = idGeneratorMapper.getAllBizTags();

        if (CollectionUtils.isEmpty(dbTags)) {
            return;
        }

        // get all business tags in the cache
        List<String> cacheTags = new ArrayList<>(tagsAndIdsCache.keySet());
        // store all the business tags for now
        Set<String> tagsToRemove = new HashSet<>(cacheTags);
        // store all current tags in database
        Set<String> tagsToInsert = new HashSet<>(dbTags);

        // filter out the tags in cache from the tags in database,
        // so the remaining tags are the tags to insert
        tagsToInsert.removeAll(tagsToRemove);

        for (String tag : tagsToInsert) {
            // create a new segment buffer
            SegmentBuffer buffer = new SegmentBuffer();
            // set the business tag
            buffer.setTag(tag);

            // get the current segment
            Segment currentSegment = buffer.getCurrentSegment();
            // settings
            currentSegment.setCurrentId(new AtomicLong(0L));
            currentSegment.setMaxId(0L);

            // put the 'tag' and its 'buffer' into cache
            tagsAndIdsCache.put(tag, buffer);

            log.info("Add tag {} from database to IdCache, SegmentBuffer: {}", tag, buffer);
        }

        // remove expired tags from the cache:
        // 1. update the 'tags to remove' set (tags in database should not be removed)
        dbTags.forEach(tagsToRemove::remove);
        // 2. remove the tags in cache
        for (String tagToRemove : tagsToRemove) {
            tagsAndIdsCache.remove(tagToRemove);
            log.info("Remove tag {} from IdCache", tagToRemove);
        }
    }

    private void updateCacheFromDbEveryMinute() {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(
                // core pool size
                1,
                // thread factory
                runnable -> {
                    Thread thread = new Thread(runnable, "check-idCache-thread");
                    thread.setDaemon(true);
                    return thread;
                });

        // update cache every minute
        service.scheduleAtFixedRate(
                // runnable task
                this::updateCacheFromDb,
                // initial delay 2 minutes
                2L,
                // update every 1 minute
                1L,
                TimeUnit.MINUTES);
    }

    @Override
    public long generateId(String tag) {
        if (!initSuccess) {
            log.warn("ID Generator Service is not initialized for now");
            return 0L;
        }

        boolean hasCurrentTag = tagsAndIdsCache.containsKey(tag);
        if (hasCurrentTag) {
            SegmentBuffer buffer = tagsAndIdsCache.get(tag);
            // check if initialized the buffer
            boolean hasNotInit = buffer.hasNotInit();
            // if not initialized, initialize the buffer
            if (hasNotInit) {
                Segment currentSegment = buffer.getCurrentSegment();
                updateSegmentFromDb(tag, currentSegment);
                log.info("Init buffer. Update tag {} and segment {} from db", tag, currentSegment);
                buffer.setInit(true);
            }
            //  get the ID from buffer if initialized
            return getIdFromSegmentBuffer(buffer);
        }

        // when the tag is not in the cache, create the record in database
        idGeneratorMapper.updateMaxIdOrInsertIfNotPresent(tag, IdGeneratorConstant.STEP, null);
        updateCacheFromDb();
        return 0L;
    }

    private void updateSegmentFromDb(String tag, Segment segment) {
        IdGeneratorServiceImpl bean = ApplicationContextUtils.getBean(IdGeneratorServiceImpl.class);

        SegmentBuffer buffer = segment.getBuffer();
        boolean hasNotInit = buffer.hasNotInit();

        Long maximumId = null;

        if (hasNotInit) {
            // if the buffer has not been initialized, initialize it
            // get the max ID
            maximumId = bean.updateOrInsertRecordAndGetMaxId(tag);
        }

        long maxId = Optional.ofNullable(maximumId).orElseThrow(() -> new ServiceException("No max ID"));

        // set the current ID
        long currentId = maxId - IdGeneratorConstant.STEP;
        AtomicLong currentIdInCurrentSegment = segment.getCurrentId();
        currentIdInCurrentSegment.set(currentId);
        // set the max ID
        segment.setMaxId(maxId);
    }

    /**
     * Update the max ID or insert the record if the record doesn't exist,
     * and get the current max ID
     *
     * @param tag business tag
     * @return max ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public long updateOrInsertRecordAndGetMaxId(String tag) {
        idGeneratorMapper.updateMaxIdOrInsertIfNotPresent(tag, IdGeneratorConstant.STEP, null);
        Long maxId = idGeneratorMapper.getMaxId(tag);
        return Optional.ofNullable(maxId).orElseThrow(() -> new ServiceException("Can't get the max ID"));
    }

    private long getIdFromSegmentBuffer(SegmentBuffer buffer) {
        while (true) {
            // ----- Start: read lock  ------
            Lock readLock = buffer.getReadLock();
            readLock.lock();
            try {
                Segment currentSegment = buffer.getCurrentSegment();
                boolean isNextNotReady = !buffer.isNextSegmentReady();
                // check if needed to update
                if (isNextNotReady
                        // remaining ID count is less than 90%
                        && (currentSegment.getRemainingIdCount() < 0.9 * IdGeneratorConstant.STEP)
                        // successfully to change the thread running status from 'false' to 'true'
                        && buffer.getThreadRunningAtomicBoolean().compareAndSet(false, true)) {

                    executorService.execute(() -> {
                        Segment[] segments = buffer.getSegments();
                        int nexSegmentIndex = buffer.getNexSegmentIndex();
                        Segment nextSegment = segments[nexSegmentIndex];

                        boolean isUpdated = false;

                        try {
                            // update the next segment
                            String tag = buffer.getTag();
                            updateSegmentFromDb(tag, nextSegment);
                            isUpdated = true;
                            log.info("update segment [tag: {}] from database {}", tag, nextSegment);
                        } catch (ServiceException e) {
                            log.error("update segment [tag: {}] from database failed", buffer.getTag(), e);
                        } finally {
                            if (isUpdated) {
                                Lock writeLock = buffer.getWriteLock();
                                writeLock.lock();
                                try {
                                    buffer.setNextSegmentReady(true);
                                    buffer.getThreadRunningAtomicBoolean().set(false);
                                } finally {
                                    writeLock.unlock();
                                }
                            } else {
                                // is not update
                                buffer.getThreadRunningAtomicBoolean().set(false);
                            }
                        }
                    });
                }

                // after checking if needed to update
                // get the current ID
                AtomicLong previousId = currentSegment.getCurrentId();
                long currentId = previousId.getAndIncrement();
                if (currentId < currentSegment.getMaxId()) {
                    // successfully get the ID
                    return currentId;
                }
            } finally {
                readLock.unlock();
            }
            // ----- End: read lock -----

            // wait and sleep
            waitAndSleep(buffer);

            // --- Start: write lock  ------
            Lock writeLock = buffer.getWriteLock();
            writeLock.lock();
            try {
                Segment currentSegment = buffer.getCurrentSegment();
                long currentId = currentSegment.getCurrentId().getAndIncrement();
                if (currentId < currentSegment.getMaxId()) {
                    return currentId;
                }

                boolean isNextReady = buffer.isNextSegmentReady();
                if (isNextReady) {
                    buffer.switchCurrentSegmentIndex();
                    buffer.setNextSegmentReady(false);
                } else {
                    log.warn("Both two segments in {} are not ready!", buffer);
                    throw new ServiceException("Both two segments in " + buffer + " are not ready!");
                }
            } finally {
                writeLock.unlock();
            }
            // --- End: write lock -----
        }
    }

    private void waitAndSleep(SegmentBuffer buffer) {
        AtomicBoolean isThreadRunning = buffer.getThreadRunningAtomicBoolean();

        int roll = 0;
        // spin lock:
        // when the thread is running,
        while (isThreadRunning.get()) {
            // roll 1000 times
            roll++;
            if (roll > 1000) {
                try {
                    // and sleep
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    log.error("Thread {} Interrupted", Thread.currentThread().getName(), e);
                    break;
                }
            }
        }
    }
}
