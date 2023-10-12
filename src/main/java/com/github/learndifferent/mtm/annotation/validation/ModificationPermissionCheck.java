package com.github.learndifferent.mtm.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data modification permission check
 *
 * @author zhou
 * @date 2023/10/12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModificationPermissionCheck {

    /**
     * Modification permission check type
     *
     * @return {@link CheckType}
     */
    CheckType type();

    /**
     * Modification permission check type
     *
     * @author zhou
     * @date 2023/10/12
     */
    enum CheckType {
        /**
         * Bookmark
         */
        BOOKMARK,
        /**
         * Comment
         */
        COMMENT
    }

    /**
     * When the {@link CheckType} is {@link CheckType#BOOKMARK}， it represents the ID of the bookmark.
     * When the {@link CheckType} is {@link CheckType#COMMENT}, it represents the ID of the comment.
     * etc.
     *
     * @author zhou
     * @date 2023/10/12
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Id {}

    /**
     * User ID
     *
     * @author zhou
     * @date 2023/10/12
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface UserId {}
}