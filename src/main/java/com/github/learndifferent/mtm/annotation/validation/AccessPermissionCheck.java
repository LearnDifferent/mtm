package com.github.learndifferent.mtm.annotation.validation;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data access permission check
 *
 * @author zhou
 * @date 2023/10/12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessPermissionCheck {

    /**
     * Indicate the data to access
     *
     * @return {@link DataType}
     */
    DataType dataType();

    /**
     * Indicate the data to access
     *
     * @author zhou
     * @date 2023/10/12
     */
    enum DataType {
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

        DataType(final String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return this.name;
        }
    }

    /**
     * Action type
     *
     * @return action type
     */
    ActionType actionType() default ActionType.MODIFICATION;

    /**
     * Action Type
     *
     * @author zhou
     * @date 2023/10/12
     */
    enum ActionType {
        /**
         * Modification
         */
        MODIFICATION,
        /**
         * Create
         */
        CREATE,
        /**
         * Read
         */
        READ,
        /**
         * Update
         */
        UPDATE,
        /**
         * Delete
         */
        DELETE
    }

    /**
     * When the {@link DataType} is {@link DataType#BOOKMARK}， it represents the ID of the bookmark.
     * When the {@link DataType} is {@link DataType#COMMENT} and the {@link ActionType} is {@link ActionType#CREATE}, it
     * represents the ID of the bookmark.
     * When the {@link DataType} is {@link DataType#TAG}, it represents the ID of the bookmark.
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

    /**
     * Comment
     *
     * @author zhou
     * @date 2023/10/16
     */
    @interface Comment {}

    /**
     * Reply to comment ID
     *
     * @author zhou
     * @date 2023/10/16
     */
    @interface ReplyToCommentId {}
}