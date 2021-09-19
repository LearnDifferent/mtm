package com.github.learndifferent.mtm.annotation.modify.string;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 空字符串检查。
 * <p>如果 String 类型的参数为空或 null，进行下一步处理</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyStringCheck {

    /**
     * 如果为空，转化为一个默认值
     *
     * @author zhou
     * @date 2021/09/05
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DefaultValueIfEmpty {

        /**
         * 如果为空字符串或 null 时，就转化为这个值
         *
         * @return 当字符串为空或 null 时的默认值
         */
        String value() default "";
    }

    /**
     * 如果为空或 null 时，抛出 ServiceException 异常
     *
     * @author zhou
     * @date 2021/09/05
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ExceptionIfEmpty {

        /**
         * ServiceException 需要的 ResultCode
         *
         * @return ResultCode
         */
        ResultCode resultCode() default ResultCode.FAILED;

        /**
         * 抛出异常时的信息
         *
         * @return 抛出异常时的信息
         */
        String errorMessage() default "";
    }
}
