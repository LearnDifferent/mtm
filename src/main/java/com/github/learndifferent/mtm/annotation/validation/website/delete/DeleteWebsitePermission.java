package com.github.learndifferent.mtm.annotation.validation.website.delete;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除网页数据的权限验证
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteWebsitePermission {

    String webIdParamName();

    String usernameParamName();
}
