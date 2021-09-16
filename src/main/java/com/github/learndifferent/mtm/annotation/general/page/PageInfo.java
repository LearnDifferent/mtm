package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.constant.enums.PageInfoMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 页面信息。根据不同模式传入的信息，生成 PageInfoDTO。
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageInfo {

    /**
     * 选择以什么模式来获取 Page 信息。
     * <p>PageInfoMode.CURRENT_PAGE_MODE 表示传入的参数是 currentPage</p>
     * <p>PageInfoMode.FROM_MODE 表示传入的参数是 from</p>
     *
     * @return 选择的模式
     */
    PageInfoMode pageInfoMode() default PageInfoMode.CURRENT_PAGE;

    /**
     * size 属性的值，默认为 10
     *
     * @return size 属性的值
     */
    int size() default 10;
}
