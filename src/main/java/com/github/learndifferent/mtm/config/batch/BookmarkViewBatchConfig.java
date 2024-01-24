package com.github.learndifferent.mtm.config.batch;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.entity.ViewDataDO;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
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
    private final DataSource dataSource;

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
                .<ViewDataDO, ViewDataDO>chunk(10)
                .reader(updateBookmarkViewReader())
                .writer(updateBookmarkViewWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super ViewDataDO> updateBookmarkViewWriter() {
        log.info("[BookmarkViewBatch - JdbcBatchItemWriter] Update bookmark views writer is started");
        log.info("[BookmarkViewBatch - JdbcBatchItemWriter] Updating bookmark views to database");

        JdbcBatchItemWriter<Object> writer = new JdbcBatchItemWriter<>();
        log.info("[BookmarkViewBatch - JdbcBatchItemWriter] JdbcBatchItemWriter is generated");

        writer.setDataSource(dataSource);
        log.info("[BookmarkViewBatch - JdbcBatchItemWriter] DataSource is set: {}", dataSource);

        // set parameters
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        log.info("[BookmarkViewBatch - JdbcBatchItemWriter] ItemSqlParameterSourceProvider is set");

        // set SQL
        writer.setSql("delete from bookmark_view where bookmark_id = :bookmarkId");
        writer.setSql("insert into bookmark_view (bookmark_id, views) values (:bookmarkId, :views)");
        log.info("[BookmarkViewBatch - JdbcBatchItemWriter] SQL is set");
        return writer;
    }

    @Bean
    public ItemReader<? extends ViewDataDO> updateBookmarkViewReader() {
        log.info("[BookmarkViewBatch - JdbcBatchItemReader] Update bookmark views reader is started");
        log.info("[BookmarkViewBatch - JdbcBatchItemReader] Getting views data from Redis for updating bookmark views");
        // all views data keys
        Set<String> keys = this.redisTemplate.opsForSet().members(RedisConstant.VIEW_KEY_SET);
        boolean hasNoKeys = CollectionUtils.isEmpty(keys);
        log.info("[BookmarkViewBatch - JdbcBatchItemReader] Keys for views data {}", hasNoKeys ? "is empty" : keys);

        ThrowExceptionUtils.throwIfTrue(hasNoKeys, "No keys for views data");

        List<ViewDataDO> viewDataList = new ArrayList<>();

        List<String> failKeys = new ArrayList<>();

        for (String key : keys) {
            log.info("[BookmarkViewBatch - JdbcBatchItemReader] Getting views data for key: {}", key);
            String val = this.redisTemplate.opsForValue().get(key);
            if (Objects.isNull(val)) {
                log.warn("[BookmarkViewBatch - JdbcBatchItemReader] Can't get views data for key: {}", key);
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
                        "[BookmarkViewBatch - JdbcBatchItemReader] Get views data for key: {}, views: {}, bookmark Id: {}",
                        key, views, bookmarkId);
            } catch (Exception e) {
                log.error("[BookmarkViewBatch - JdbcBatchItemReader] Can't get views for key: {}", key, e);
                failKeys.add(key);
            }
        }

        if (CollectionUtils.isNotEmpty(failKeys)) {
            log.warn("[BookmarkViewBatch - JdbcBatchItemReader] Fail Views Data Keys: {}", failKeys);
        }

        log.info("[BookmarkViewBatch - JdbcBatchItemReader] Get views data: {}", viewDataList);
        return new ListItemReader<>(viewDataList);
    }


}