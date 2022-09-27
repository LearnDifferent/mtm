package com.github.learndifferent.mtm.annotation.validation.comment.get;

import com.github.learndifferent.mtm.annotation.common.BookmarkId;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Check before getting comments.
 * This annotation has to be used along with {@link BookmarkId}
 * and {@link com.github.learndifferent.mtm.annotation.common.Username}
 *
 * @author zhou
 * @date 2021/9/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetCommentsCheck {}
