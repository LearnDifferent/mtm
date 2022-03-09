package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.constant.enums.PageInfoMode;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Generate {@link PageInfoDTO} according to {@link PageInfoMode}.
 *
 * @author zhou
 * @date 2021/09/05
 * @see com.github.learndifferent.mtm.config.CustomWebConfig#addArgumentResolvers(List)
 */
@Slf4j
public class PageInfoMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Only when annotated with @PageInfo
        return parameter.hasParameterAnnotation(PageInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        PageInfo annotation = parameter.getParameterAnnotation(PageInfo.class);

        ThrowExceptionUtils.throwIfNull(annotation, ResultCode.FAILED);

        // "from" mode or "current page" mode
        PageInfoMode mode = annotation.pageInfoMode();
        // page size
        int size = annotation.size();

        // use the name of mode as parameter name
        String paramName = mode.paramName();
        // Get value from request
        String paramValue = webRequest.getParameter(paramName);

        // Get the number from value.
        // If the value is empty, null or not even a number,
        // use 0 as default value
        int num = getNumberFromParamValue(paramValue);

        int from;
        switch (mode) {
            case FROM:
                // "num" stands for "from" on "from" mode
                from = num;
                break;
            case CURRENT_PAGE:
            default:
                // "num" stands for "current page" on "current page" mode
                // num must be greater than 0
                int currentPage = PageUtil.constrainGreaterThanZero(num);
                // get "from" according to current page
                from = PageUtil.getFromIndex(currentPage, size);
        }

        return PageInfoDTO.builder().from(from).size(size).build();
    }

    private int getNumberFromParamValue(String paramValue) {

        int num = 0;

        if (StringUtils.isNotEmpty(paramValue)) {
            try {
                num = Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                log.warn("Can't cast to number. Return 0 instead.");
            }
        }
        return num;
    }
}
