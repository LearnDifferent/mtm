package com.github.learndifferent.mtm.annotation.validation.website.marked;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查用户是否已经收藏。如果已经收藏了、或是没有收藏的权限，就抛出异常。
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarkCheck {

    /**
     * 包含用户名信息的参数的名称
     *
     * @return 包含用户名信息的参数的名称
     */
    String usernameParamName();

    /**
     * 包含 url 信息的参数的 Class
     *
     * @return 包含 url 信息的参数的 Class
     */
    Class<? extends Serializable> paramClassContainsUrl();

    /**
     * 在包含 URL 的 Class 中，URL 属性的名称
     *
     * @return URL 属性的名称
     */
    String urlFieldNameInParamClass();
}
