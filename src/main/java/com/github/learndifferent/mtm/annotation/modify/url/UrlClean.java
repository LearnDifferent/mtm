package com.github.learndifferent.mtm.annotation.modify.url;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * url 清理相关。需要配合 {@link com.github.learndifferent.mtm.annotation.common.Url} 注解使用
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlClean {}
