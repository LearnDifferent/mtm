package com.github.learndifferent.mtm.annotation.modify.string;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Throw an exception or replace it with default value if the string is empty
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyStringCheck {

    /**
     * Replace the string with default value if it's empty
     *
     * @author zhou
     * @date 2021/09/05
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DefaultValueIfEmpty {

        /**
         * Default value if the string is empty
         *
         * @return default value if the string is empty
         */
        String value() default "";
    }

    /**
     * Throw an exception if the string is empty
     *
     * @author zhou
     * @date 2021/09/05
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ExceptionIfEmpty {

        /**
         * {@link ResultCode}
         *
         * @return {@link ResultCode}
         */
        ResultCode resultCode() default ResultCode.FAILED;

        /**
         * Error message
         *
         * @return error message
         */
        String errorMessage() default "";
    }
}
