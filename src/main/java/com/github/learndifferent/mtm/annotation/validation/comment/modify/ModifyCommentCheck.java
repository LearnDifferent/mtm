package com.github.learndifferent.mtm.annotation.validation.comment.modify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Check before comment modification
 * This annotation has to be used along with {@link com.github.learndifferent.mtm.annotation.common.CommentId}
 * and {@link com.github.learndifferent.mtm.annotation.common.Username}
 *
 * @author zhou
 * @date 2021/9/29
 * @see ModifyCommentCheckAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyCommentCheck {}
