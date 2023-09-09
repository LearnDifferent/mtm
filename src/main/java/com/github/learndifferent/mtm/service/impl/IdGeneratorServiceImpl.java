package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.IdGeneratorConstant;
import com.github.learndifferent.mtm.dto.id.Segment;
import com.github.learndifferent.mtm.dto.id.SegmentBuffer;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.IdGeneratorMapper;
import com.github.learndifferent.mtm.service.IdGeneratorService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.RedisKeyUtils;
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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;

    /**
     * Key is the business tag,
     * value is the buffer that stores the ID information
     */
    private final Map<String, SegmentBuffer> tagsAndIdsCache = new ConcurrentHashMap<>();

    /**
     * true if initialization is successful
     */
    private volatile boolean isInitialized = false;

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
        if (isInitialized) {
            log.info("ID Generator Service has already been initialized");
            return true;
        }
        log.info("Initializing ID Generator Service");

        // update cache to finish the initialization
        updateCacheFromDb();
        // initialization success if no exception
        isInitialized = true;

        // start daemon threads to update the cache frequently
        updateCacheFromDbEveryMinute();
        return isInitialized;
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

        // add the tags to the cache
        tagsToInsert.forEach(this::addTagToCache);

        // remove expired tags from the cache:
        // 1. update the 'tags to remove' set (tags in database should not be removed)
        dbTags.forEach(tagsToRemove::remove);
        // 2. remove the tags in cache because they are expired
        tagsToRemove.forEach(tagToRemove -> {
            tagsAndIdsCache.remove(tagToRemove);
            log.info("Remove tag {} from IdCache", tagToRemove);
        });
    }

    private void addTagToCache(String tag) {
        if (tagsAndIdsCache.containsKey(tag)) {
            log.info("Tag {} is already in IdCache", tag);
            return;
        }
        // create a new segment buffer
        SegmentBuffer buffer = new SegmentBuffer();
        // set the business tag
        buffer.setTag(tag);

        // get the current segment
        Segment currentSegment = buffer.getCurrentSegment();
        // init the current ID and max ID for the current segment
        currentSegment.setCurrentId(new AtomicLong(0L));
        currentSegment.setMaxId(0L);

        // put the 'tag' and its 'buffer' (buffer includes ID information for the tag) into cache
        tagsAndIdsCache.put(tag, buffer);
        log.info("Add tag {} from database to IdCache, SegmentBuffer: {}", tag, buffer);
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

        // update cache every 5 minutes
        service.scheduleAtFixedRate(
                // runnable task
                this::updateCacheFromDb,
                // initial delay 2 minutes
                10L,
                // update every 1 minute
                5L,
                TimeUnit.MINUTES);
    }

    @Override
    public long generateId(String tag) {
        if (!isInitialized) {
            log.warn("ID Generator Service has not been initialized");
            boolean initFail = !init();
            if (initFail) {
                throw new ServiceException("Failed to instantiate ID Generator Service");
            }
        }

        // when the tag is not in the cache,
        boolean hasNoCurrentTag = !tagsAndIdsCache.containsKey(tag);
        // create the record in database:
        if (hasNoCurrentTag) {
            // the first ID will be 1, so the max ID in database will be step + 1
            long maxId = IdGeneratorConstant.STEP + 1;
            boolean wasPreviouslyAbsent = idGeneratorMapper
                    .insertIfNotPresent(tag, maxId, IdGeneratorConstant.STEP, null);
            if (wasPreviouslyAbsent) {
                // if the record was previously absent and now exists,
                // generate the first ID (which is 1) for the newly added tag
                return generateFirstIdForNewlyAddedTag(tag, maxId);
            }

            // update the cache if the record was NOT previously absent in the database,
            // indicating that the tag is not in the cache only
            updateCacheFromDb();
        }

        // when the tag is already cached (when a tag is cached, it's also in the database)
        // get the buffer which contains segment that has ID information
        SegmentBuffer buffer = tagsAndIdsCache.get(tag);
        // check if initialized the buffer
        boolean isNotInitialized = buffer.isNotInitialized();
        // update the current segment and set the init status to 'true' if not initialized
        if (isNotInitialized) {
            // When the tag is not present in the cache,
            // it actually triggers the initial initialization process for that tag,
            // which also initializes the segment buffer.
            // Therefore, in most cases, this process will not be executed.
            Segment currentSegment = buffer.getCurrentSegment();
            updateSegmentFromDb(tag, currentSegment);
            log.info("Init buffer. Update tag {} and segment {} from db", tag, currentSegment);
            buffer.setInitialized(true);
        }
        //  get the ID from buffer if initialized
        long id = getIdFromSegmentBuffer(buffer);

        // save the current ID to cache
        String currentIdKey = RedisKeyUtils.getCurrentIdKey(tag);
        redisTemplate.opsForValue().set(currentIdKey, String.valueOf(id));

        return id;
    }

    private long generateFirstIdForNewlyAddedTag(String tag, long maxId) {
        // If the record was previously absent and now exists
        // add the tag to the cache (this will create a new segment buffer)
        addTagToCache(tag);
        // get the buffer
        SegmentBuffer buffer = tagsAndIdsCache.get(tag);
        // initialize the segment buffer
        initSegmentBufferForAbsentTag(tag, maxId, buffer);
        // return the value
        return 1L;
    }

    private void initSegmentBufferForAbsentTag(String tag, long maxId, SegmentBuffer buffer) {
        if (buffer.isInitialized()) {
            log.info("[Tag {} , Segment Buffer {}] has already been initialized", tag, buffer);
            return;
        }

        long firstId = 1;
        if (firstId + IdGeneratorConstant.STEP != maxId) {
            log.warn("The max ID is not correct when the current ID is 1. Max ID: {}, Step: {}", maxId,
                    IdGeneratorConstant.STEP);
            throw new ServiceException("The max ID is not correct");
        }

        // the current ID will be the second ID
        long currentId = firstId + 1;
        // set the max ID and current ID for the first segment
        Segment firstSegment = buffer.getCurrentSegment();
        setMaxIdAndCurrentIdForSegment(maxId, currentId, firstSegment);

        // set the inti status to 'true'
        buffer.setInitialized(true);
        log.info("Init buffer. Add tag {} and segment {} from db", tag, firstSegment);
    }

    private void updateSegmentFromDb(String tag, Segment segment) {

        SegmentBuffer buffer = segment.getBuffer();
        boolean isInitialized = buffer.isInitialized();

        // if the buffer has been initialized, get the max ID from the segment
        // if the buffer has NOT been initialized, update the max ID and get it from database
        long maxId = isInitialized ? segment.getMaxId() : updateAndGetMaxIdFromDatabase(tag);

        // current ID
        long currentId = getCurrentIdFromRedisCacheOrCalculateIt(tag, maxId);
        // set max ID and current iD
        setMaxIdAndCurrentIdForSegment(maxId, currentId, segment);
    }

    private long getCurrentIdFromRedisCacheOrCalculateIt(String tag, long maxId) {
        // if the current ID is in the Redis cache, get it from the cache
        String currentIdKey = RedisKeyUtils.getCurrentIdKey(tag);
        String currentIdString = redisTemplate.opsForValue().get(currentIdKey);
        boolean hasCurrentIdInCache = StringUtils.isNotBlank(currentIdString);
        if (hasCurrentIdInCache) {
            try {
                // get the current ID in the Redis cache
                long currentIdInCache = Long.parseLong(currentIdString);
                if (checkIfCurrentIdValid(currentIdInCache, maxId)) {
                    // if the current ID in cache is valid, return it
                    log.info("Retrieve current ID {} from Redis cache", currentIdInCache);
                    return currentIdInCache;
                }
            } catch (NumberFormatException e) {
                log.error("Failed to parse current ID string {} from Redis cache", currentIdString, e);
            }
        }
        // if the current ID is not in the Redis cache, or it's not valid,
        // calculate it from the max ID and step
        return maxId - IdGeneratorConstant.STEP;
    }

    private long updateAndGetMaxIdFromDatabase(String tag) {
        IdGeneratorServiceImpl bean = ApplicationContextUtils.getBean(IdGeneratorServiceImpl.class);
        return bean.updateOrInsertRecordAndGetMaxId(tag);
    }

    private void setMaxIdAndCurrentIdForSegment(long maxId, long currentId, Segment segment) {
        // set current ID
        AtomicLong currentIdInSegment = segment.getCurrentId();
        currentIdInSegment.set(currentId);
        // set max ID
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
                // get the current segment
                Segment currentSegment = buffer.getCurrentSegment();
                // check if
                boolean isNextNotReady = !buffer.isNextSegmentReady();
                // check if needed to update the next segment (Preload the IDs in advance)
                if (isNextNotReady
                        // remaining ID count is less than 90%
                        && (currentSegment.getRemainingIdCount() < 0.9 * IdGeneratorConstant.STEP)
                        // successfully to change the thread running status from 'false' to 'true'
                        && buffer.getThreadRunningAtomicBoolean().compareAndSet(false, true)) {

                    // use executor service to update the next segment
                    executorService.execute(() -> {
                        // get the next segment
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
                                // If the update is successful,
                                // set the status to 'ready' with the implementation of a 'write lock'
                                // to handle potential concurrent thread attempts to modify the status
                                Lock writeLock = buffer.getWriteLock();
                                writeLock.lock();
                                try {
                                    // the next segment is ready now after updating
                                    buffer.setNextSegmentReady(true);
                                    // remember to set the thread running status to 'false'
                                    buffer.getThreadRunningAtomicBoolean().set(false);
                                } finally {
                                    writeLock.unlock();
                                }
                            } else {
                                // If the update fails, regardless of the outcome,
                                // set the running status of the thread to 'false'
                                buffer.getThreadRunningAtomicBoolean().set(false);
                            }
                        }
                    });
                }

                // after checking if needed to update the next segment
                // get the current ID if valid (current ID < max ID):
                // get and increment the previous ID and get the current ID
                AtomicLong previousId = currentSegment.getCurrentId();
                long currentId = previousId.incrementAndGet();
                // check if the current ID is less than the max ID
                if (checkIfCurrentIdValid(currentId, currentSegment.getMaxId())) {
                    // then the current ID is valid, return it
                    return currentId;
                }
            } finally {
                readLock.unlock();
            }
            // ----- End: read lock -----

            // if the current ID is invalid (current ID >= max ID),
            // use a spin lock to wait, until no other thread
            // is currently executing the operation to modify the next segment
            waitAndSleep(buffer);

            // --- Start: write lock  ------
            Lock writeLock = buffer.getWriteLock();
            writeLock.lock();
            try {
                // After using the spin lock to wait,
                // the current ID may be updated and is valid now,
                // so check and return the current ID if it's a valid ID
                Segment currentSegment = buffer.getCurrentSegment();
                long currentId = currentSegment.getCurrentId().getAndIncrement();
                if (checkIfCurrentIdValid(currentId, currentSegment.getMaxId())) {
                    return currentId;
                }

                // the next segment is normally ready after updating
                boolean isNextReady = buffer.isNextSegmentReady();
                if (isNextReady) {
                    // switch the current segment to the next segment
                    buffer.switchCurrentSegmentIndex();
                    // set the next segment to 'not ready' to indicate
                    // that the next segment should be updated
                    buffer.setNextSegmentReady(false);
                } else {
                    log.warn("Both two segments in {} are not ready!", buffer);
                    throw new ServiceException("Both two segments in " + buffer + " are not ready!");
                }
            } finally {
                writeLock.unlock();
            }
            // --- End: write lock -----
            // Following the completion of the 'write lock' process,
            // the while loop will persist until a valid current ID is obtained.
        }
    }

    private boolean checkIfCurrentIdValid(long currentId, long maxId) {
        return currentId < maxId;
    }

    /**
     * This method checks whether another thread
     * is currently running to execute the segment modification operation.
     * If another thread is running, it enters a spin loop,
     * continuously checking if other threads are still running.
     * <p>
     * If the spin loop exceeds a certain threshold (in this case, 1000 times),
     * it pauses briefly and break the loop to avoid excessive CPU usage
     * </p>
     *
     * @param buffer current segment buffer
     */
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
