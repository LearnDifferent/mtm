package com.github.learndifferent.mtm.strategy;

/**
 * Data existence verification strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
public interface DataExistenceVerificationStrategy {

    /**
     * Verify data existence
     *
     * @return true if exists
     */
    boolean verifyDataExistence();
}