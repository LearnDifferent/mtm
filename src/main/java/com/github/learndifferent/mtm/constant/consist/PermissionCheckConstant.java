package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to data modification permission check
 *
 * @author zhou
 * @date 2023/10/12
 */
public final class PermissionCheckConstant {

    public static final String BOOKMARK = "bookmark-permission-check";
    public static final String USER_CREATE = "user-create-permission-check";
    public static final String COMMENT_CREATE = "comment-create-permission-check";
    public static final String COMMENT_READ = "comment-read-permission-check";
    public static final String COMMENT_UPDATE = "comment-update-permission-check";
    public static final String COMMENT_DELETE = "comment-delete-permission-check";
    public static final String TAG_CREATE = "tag-create-permission-check";
    public static final String TAG_DELETE = "tag-delete-permission-check";
    public static final String IS_ADMIN = "is-admin-permission-check";

    private PermissionCheckConstant() {
    }
}