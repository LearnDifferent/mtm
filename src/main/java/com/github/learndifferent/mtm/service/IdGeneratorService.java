package com.github.learndifferent.mtm.service;

/**
 * ID Generator Service
 *
 * @author zhou
 * @date 2023/9/3
 */
public interface IdGeneratorService {

    /**
     * Generate ID
     *
     * @return ID
     */
    long generateId();

    /**
     * Initialize
     *
     * @return true if initialization is successful
     */
    boolean init();

    /**
     * Generate ID
     *
     * @param tag                  business tag
     * @param tableName            table name
     * @param primaryKeyColumnName primary key column name
     * @return ID
     */
    long generateId(String tag, String tableName, String primaryKeyColumnName);
}