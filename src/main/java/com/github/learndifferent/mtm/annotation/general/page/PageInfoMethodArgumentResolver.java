package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.config.mvc.CustomWebConfig;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.utils.PaginationUtils;
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
 * Generate {@link PageInfoDTO} according to {@link PageInfo}.
 *
 * @author zhou
 * @date 2021/09/05
 * @see CustomWebConfig#addArgumentResolvers(List)
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

        // page size
        int size = annotation.size();

        // get parameter name
        PageInfoParam param = annotation.paramName();
        String paramName = param.paramName();
        // get the value from request
        String paramValue = webRequest.getParameter(paramName);

        // get the number from value
        // use 0 as default value if the value is empty, null or not even a number
        int num = getNumberFromParamValue(paramValue);

        int from;
        switch (param) {
            // "num" stands for "from"
            case FROM:
                from = num;
                break;
            // "num" stands for "current page"
            case CURRENT_PAGE:
            default:
                // current page must be greater than 0
                int currentPage = PaginationUtils.constrainGreaterThanZero(num);
                // get "from" according to current page
                from = PaginationUtils.getFromIndex(currentPage, size);
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