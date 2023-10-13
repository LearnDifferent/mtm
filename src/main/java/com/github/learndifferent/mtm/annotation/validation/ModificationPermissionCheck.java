package com.github.learndifferent.mtm.annotation.validation;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
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
        BOOKMARK(PermissionCheckConstant.BOOKMARK),
        /**
         * Tag
         */
        TAG(PermissionCheckConstant.TAG),
        /**
         * Comment
         */
        COMMENT(PermissionCheckConstant.COMMENT);

        CheckType(final String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return this.name;
        }
    }

    /**
     * When the {@link CheckType} is {@link CheckType#BOOKMARK}， it represents the ID of the bookmark.
     * When the {@link CheckType} is {@link CheckType#COMMENT}, it represents the ID of the comment.
     * When the {@link CheckType} is {@link CheckType#TAG}, it represents the ID of the bookmark.
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

    /**
     * Tag
     *
     * @author zhou
     * @date 2023/10/13
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Tag {}
}