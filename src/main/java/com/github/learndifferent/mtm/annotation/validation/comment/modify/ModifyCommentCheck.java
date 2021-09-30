package com.github.learndifferent.mtm.annotation.validation.comment.modify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Check before comment modification
 *
 * @author zhou
 * @date 2021/9/29
 * @see ModifyCommentCheckAspect
 * @see com.github.learndifferent.mtm.annotation.common.CommentId
 * @see com.github.learndifferent.mtm.annotation.common.Username
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyCommentCheck {}
