package com.github.learndifferent.mtm.strategy.search.related;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Data search-related strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
public interface DataSearchRelatedStrategy {

    /**
     * Verify data existence in Elasticsearch
     *
     * @return Return true if it exists
     */
    boolean verifyDataExistenceInElasticsearch();

    /**
     * Check if the index exists in Elasticsearch.
     * If the index does not exist, return true.
     * If the index exists, delete it and return whether the deletion was successful.
     *
     * @return true if deleted
     */
    boolean checkAndDeleteIndexInElasticsearch();

    /**
     * Data generation for Elasticsearch based on database
     *
     * @return true if success
     */
    boolean generateDataForElasticsearchBasedOnDatabase();

    /**
     * Check if data in database is different from data in Elasticsearch
     *
     * @return true if detect a difference
     */
    boolean checkDatabaseElasticsearchDataDifference();

    /**
     * Get the count of Elasticsearch documents asynchronously and compare the difference
     *
     * @param countEsDocsResult {@link Future<Long>} Elasticsearch document count
     * @param databaseCount     database count
     * @return true if detect a difference
     */
    default boolean getEsCountAsyncAndCompareDifference(Future<Long> countEsDocsResult, long databaseCount) {
        Long elasticsearchDocCount = null;
        try {
            elasticsearchDocCount = countEsDocsResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long esCount = Optional.ofNullable(elasticsearchDocCount).orElse(0L);

        // 如果数量不相同，代表有变化；如果数量相同，代表没有变化
        return databaseCount != esCount;
    }
}