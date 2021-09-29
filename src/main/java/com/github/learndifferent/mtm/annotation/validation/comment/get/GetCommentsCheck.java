package com.github.learndifferent.mtm.annotation.validation.comment.get;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Check before getting comments.
 *
 * @author zhou
 * @date 2021/9/29
 * @see com.github.learndifferent.mtm.annotation.common.WebId
 * @see com.github.learndifferent.mtm.annotation.common.Username
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetCommentsCheck {}
