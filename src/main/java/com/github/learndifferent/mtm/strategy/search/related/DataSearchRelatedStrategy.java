package com.github.learndifferent.mtm.strategy.search.related;

/**
 * Data search-related strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
public interface DataSearchRelatedStrategy {

    /**
     * Verify data existence
     *
     * @return true if exists
     */
    boolean verifyDataExistence();

    /**
     * Check if the index exists. If the index does not exist, return true.
     * If the index exists, delete it and return whether the deletion was successful.
     *
     * @return true if deleted
     */
    boolean checkAndDeleteIndex();

    /**
     * Data generation for Elasticsearch based on database
     *
     * @return true if success
     */
    boolean generateDataForSearch();
}