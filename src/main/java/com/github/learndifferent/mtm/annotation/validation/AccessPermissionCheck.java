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
     * Data Access Type
     *
     * @return {@link DataAccessType}
     */
    DataAccessType dataAccessType();

    /**
     * Indicate the data to access
     *
     * @author zhou
     * @date 2023/10/12
     */
    enum DataAccessType {
        /**
         * Bookmark
         */
        BOOKMARK(PermissionCheckConstant.BOOKMARK),
        /**
         * Create user
         */
        USER_CREATE(PermissionCheckConstant.USER_CREATE),
        /**
         * Create tag
         */
        TAG_CREATE(PermissionCheckConstant.TAG_CREATE),
        /**
         * Delete tag
         */
        TAG_DELETE(PermissionCheckConstant.TAG_DELETE),
        /**
         * Read comment
         */
        COMMENT_READ(PermissionCheckConstant.COMMENT_READ),
        /**
         * Create Comment
         */
        COMMENT_CREATE(PermissionCheckConstant.COMMENT_CREATE),
        /**
         * Update Comment
         */
        COMMENT_UPDATE(PermissionCheckConstant.COMMENT_UPDATE),
        /**
         * Delete Comment
         */
        COMMENT_DELETE(PermissionCheckConstant.COMMENT_DELETE),
        /**
         * Is admin
         */
        IS_ADMIN(PermissionCheckConstant.IS_ADMIN);

        DataAccessType(final String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return this.name;
        }
    }

    /**
     * Bookmark ID
     *
     * @author zhou
     * @date 2023/10/12
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface BookmarkId {}

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
     * Comment ID
     *
     * @author zhou
     * @date 2023/10/16
     */
    @interface CommentId {}

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

    /**
     * Username
     *
     * @author zhou
     * @date 2023/10/17
     */
    @interface Username {}

    /**
     * Password
     *
     * @author zhou
     * @date 2023/10/17
     */
    @interface Password {}
}