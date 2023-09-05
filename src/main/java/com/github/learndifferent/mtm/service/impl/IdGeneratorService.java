package com.github.learndifferent.mtm.service.impl;

/**
 * ID Generator Service
 *
 * @author zhou
 * @date 2023/9/3
 */
public interface IdGeneratorService {

    /**
     * Initialize
     *
     * @return true if initialization is successful
     */
    boolean init();

    /**
     * Generate ID
     *
     * @param tag business tag
     * @return ID
     */
    long generateId(String tag);
}