package com.github.learndifferent.mtm.annotation.validation.website.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查该 {@link com.github.learndifferent.mtm.annotation.common.WebId} 注解的 web id 的数据是否属于
 * {@link com.github.learndifferent.mtm.annotation.common.Username} 注解的用户名的用户
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyWebsitePermissionCheck {}
