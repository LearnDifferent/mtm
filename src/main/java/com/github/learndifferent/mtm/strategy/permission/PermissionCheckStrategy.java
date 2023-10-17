package com.github.learndifferent.mtm.strategy.permission;

import java.lang.annotation.Annotation;

/**
 * Data access permission check strategy
 *
 * @author zhou
 * @date 2023/10/12
 */
public interface PermissionCheckStrategy {

    /**
     * Check data access permission
     *
     * @param parameterAnnotations parameter annotations
     * @param args                 args
     */
    void checkPermission(Annotation[][] parameterAnnotations, Object[] args);
}