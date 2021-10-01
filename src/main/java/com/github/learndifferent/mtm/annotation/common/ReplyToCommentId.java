package com.github.learndifferent.mtm.annotation.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Reply to a comment
 *
 * @author zhou
 * @date 2021/10/1
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplyToCommentId {}
