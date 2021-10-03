package com.github.learndifferent.mtm.annotation.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解所修饰的参数为邀请码
 *
 * @author zhou
 * @date 2021/09/20
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvitationCode {

    /**
     * Whether invitation code is required.
     *
     * @return whether invitation code is required
     */
    boolean required() default true;
}
