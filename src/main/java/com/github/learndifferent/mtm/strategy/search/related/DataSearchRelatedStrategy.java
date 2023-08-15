package com.github.learndifferent.mtm.strategy.search.related;

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
}