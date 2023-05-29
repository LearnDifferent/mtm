package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which helps to generate
 * {@link com.github.learndifferent.mtm.dto.PageInfoDTO PageInfoDTO}
 *
 * @author zhou
 * @date 2021/09/05
 * @see com.github.learndifferent.mtm.dto.PageInfoDTO
 * @see PageInfoMethodArgumentResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageInfo {

    /**
     * If the parameter is {@link PageInfoParam#CURRENT_PAGE currentPage}, then the string
     * value will be recognized as current page and will be calculated to be the "from"
     * field of {@link com.github.learndifferent.mtm.dto.PageInfoDTO PageInfoDTO}.
     * <p>
     * If the parameter is {@link PageInfoParam#FROM from}, it will simply be set to
     * the "from" field of {@link com.github.learndifferent.mtm.dto.PageInfoDTO PageInfoDTO}.
     * </p>
     *
     * @return parameter name
     */
    PageInfoParam paramName();

    /**
     * The value of will be set to the "size" field of
     * {@link com.github.learndifferent.mtm.dto.PageInfoDTO PageInfoDTO}
     *
     * @return size
     */
    int size();
}
