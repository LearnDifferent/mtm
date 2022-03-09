package com.github.learndifferent.mtm.annotation.general.log;

import com.github.learndifferent.mtm.constant.enums.OptsType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemLog {

    /**
     * Title
     *
     * @return {@code String} title
     */
    String title() default "";

    /**
     * Operation's type
     *
     * @return {@code OptsType}
     */
    OptsType optsType() default OptsType.OTHERS;
}
