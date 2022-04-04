package com.github.learndifferent.mtm.annotation.validation.comment.add;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation has to be used along with {@link com.github.learndifferent.mtm.annotation.common.WebId},
 * {@link com.github.learndifferent.mtm.annotation.common.Username},
 * {@link com.github.learndifferent.mtm.annotation.common.Comment},
 * and {@link com.github.learndifferent.mtm.annotation.common.ReplyToCommentId}.
 * <p>
 * Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
 * if the user has no permissions to comment on this bookmark.
 * <p>
 * Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}
 * if the comment existed .
 * <p>
 * Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}
 * if the bookmark does not exist.
 * <p>
 * Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
 * if the comment is empty.
 * <p>
 * Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}
 * if the comment is too long.
 * <p>
 * Throw an exception with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
 * if the comment is a reply to another comment and the "another comment" does not exist
 *
 * @author zhou
 * @date 2021/9/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AddCommentCheck {}
