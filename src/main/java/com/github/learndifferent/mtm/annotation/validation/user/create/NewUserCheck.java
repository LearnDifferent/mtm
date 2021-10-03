package com.github.learndifferent.mtm.annotation.validation.user.create;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查用户名和密码，以及角色信息是否存在着
 *
 * @author zhou
 * @date 2021/09/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NewUserCheck {

    /**
     * 包含用户名和密码的属性的类
     *
     * @return {@code Class<? extends Serializable>} 包含用户名和密码的属性的类
     */
    Class<?> userClass();

    /**
     * 该类中，包含用户名的属性的名称
     *
     * @return {@code String} 该类中，包含用户名的属性的名称
     */
    String usernameFieldName();

    /**
     * 该类中，包含密码的属性的名称
     *
     * @return {@code String} 该类中，包含密码的属性的名称
     */
    String passwordFieldName();

    /**
     * 该类中，包含角色的属性的名称
     *
     * @return {@code String} 该类中，包含角色的属性的名称
     */
    String roleFieldName();
}
