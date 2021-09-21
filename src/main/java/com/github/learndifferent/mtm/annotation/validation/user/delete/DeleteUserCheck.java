package com.github.learndifferent.mtm.annotation.validation.user.delete;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除用户前查看是否可以删除。配合注解 {@link com.github.learndifferent.mtm.annotation.common.Username}
 * 和 {@link com.github.learndifferent.mtm.annotation.common.Password}
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteUserCheck {}
