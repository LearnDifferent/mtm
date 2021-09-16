package com.github.learndifferent.mtm.annotation.validation.register;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册验证码检查
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterCodeCheck {

    /**
     * 验证码参数的名称
     *
     * @return 名称
     */
    String codeParamName();

    /**
     * 验证码参数相关的 token 的名称
     *
     * @return 名称
     */
    String verifyTokenParamName();

    /**
     * 角色参数的名称
     *
     * @return 名称
     */
    String roleParamName();

    /**
     * 邀请码参数的名称
     *
     * @return 名称
     */
    String invitationCodeParamName();
}
