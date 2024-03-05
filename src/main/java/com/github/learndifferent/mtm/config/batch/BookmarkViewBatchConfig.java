package com.github.learndifferent.mtm.config.batch;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.entity.ViewDataDO;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Bookmark View Batch Configuration
 *
 * @author zhou
 * @date 2023/12/15
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookmarkViewBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final StringRedisTemplate redisTemplate;
    private final SqlSessionFactory sqlSessionFactory;

    public static final String JOB_NAME = "updateBookmarkViewJob";
    public static final String STEP_NAME = "updateBookmarkViewStep";

    /**
     * the length of {@link RedisConstant#WEB_VIEW_COUNT_PREFIX}
     */
    private final static int LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX = RedisConstant.WEB_VIEW_COUNT_PREFIX.length();

    @Bean
    public Job updateBookmarkViewJob() {
        log.info("[BookmarkViewBatch] Update bookmark views batch job is started");
        return jobBuilderFactory
                .get(JOB_NAME)
                .start(updateBookmarkViewStep())
                .build();
    }

    @Bean
    public Step updateBookmarkViewStep() {
        log.info("[BookmarkViewBatch] Update bookmark views step is started");
        return stepBuilderFactory
                .get(STEP_NAME)
                .allowStartIfComplete(true)
                .<ViewDataDO, ViewDataDO>chunk(10)
                .reader(updateBookmarkViewReader())
                .writer(updateBookmarkViewWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<ViewDataDO> updateBookmarkViewWriter() {
        log.info("[BookmarkViewBatch - BatchItemWriter] Updating bookmark views");
        MyBatisBatchItemWriterBuilder<ViewDataDO> writerBuilder = new MyBatisBatchItemWriterBuilder<>();

        return writerBuilder
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.github.learndifferent.mtm.mapper.BookmarkViewMapper.upsertBookmarkView")
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<ViewDataDO> updateBookmarkViewReader() {
        log.info("[BookmarkViewBatch - BatchItemReader] Update bookmark views reader is started");
        log.info("[BookmarkViewBatch - BatchItemReader] Getting views data from Redis for updating bookmark views");
        // all views data keys
        Set<String> keys = this.redisTemplate.opsForSet().members(RedisConstant.VIEW_KEY_SET);
        boolean hasNoKeys = CollectionUtils.isEmpty(keys);
        log.info("[BookmarkViewBatch - BatchItemReader] Keys for views data {}", hasNoKeys ? "is empty" : keys);

        ThrowExceptionUtils.throwIfTrue(hasNoKeys, "No keys for views data");

        List<ViewDataDO> viewDataList = new ArrayList<>();

        List<String> failKeys = new ArrayList<>();

        for (String key : keys) {
            log.info("[BookmarkViewBatch - BatchItemReader] Getting views data for key: {}", key);
            String val = this.redisTemplate.opsForValue().get(key);
            if (Objects.isNull(val)) {
                log.warn("[BookmarkViewBatch - BatchItemReader] Can't get views data for key: {}", key);
                continue;
            }

            try {
                // get views
                int views = Integer.parseInt(val);
                // get bookmark id
                String bookmarkIdStr = key.substring(LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX);
                long bookmarkId = Long.parseLong(bookmarkIdStr);
                // create data
                ViewDataDO data = ViewDataDO.builder().views(views).bookmarkId(bookmarkId).build();
                // add data to set
                viewDataList.add(data);
                log.info(
                        "[BookmarkViewBatch - BatchItemReader] Get views data for key: {}, views: {}, bookmark Id: {}",
                        key, views, bookmarkId);
            } catch (Exception e) {
                log.error("[BookmarkViewBatch - BatchItemReader] Can't get views for key: {}", key, e);
                failKeys.add(key);
            }
        }

        if (CollectionUtils.isNotEmpty(failKeys)) {
            log.warn("[BookmarkViewBatch - BatchItemReader] Fail Views Data Keys: {}", failKeys);
        }

        log.info("[BookmarkViewBatch - BatchItemReader] Get views data: {}", viewDataList);
        return new ListItemReader<>(viewDataList);
    }

}