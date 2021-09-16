package com.github.learndifferent.mtm.annotation.modify.url;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * url 清理相关
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlClean {

    /**
     * 含有 url 的参数的名称
     *
     * @return {@code String}
     */
    String urlParamName() default "url";
}
