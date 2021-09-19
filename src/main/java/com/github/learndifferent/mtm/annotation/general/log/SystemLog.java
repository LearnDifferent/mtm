package com.github.learndifferent.mtm.annotation.general.log;

import com.github.learndifferent.mtm.constant.enums.OptsType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统日志
 *
 * @author zhou
 * @date 2021/09/05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemLog {

    /**
     * 标题
     *
     * @return {@code String}
     */
    String title() default "";

    /**
     * 操作类型
     *
     * @return {@code OptsType}
     */
    OptsType optsType() default OptsType.OTHERS;
}
