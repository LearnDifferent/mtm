package com.github.learndifferent.mtm.annotation.general.page;

import com.github.learndifferent.mtm.constant.enums.PageInfoMode;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 页面信息方法参数解析器。根据不同模式传入的信息，生成 PageInfoDTO。
 * <p>要通过自定义的 Web MVC 配置类的 addArgumentResolvers 方法加入进去才能生效。
 * 当前的配置类为 CustomWebConfig</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
public class PageInfoMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 在有 @PageInfo 注解的位置生效，也就是返回 true
        return parameter.hasParameterAnnotation(PageInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        PageInfo annotation = parameter.getParameterAnnotation(PageInfo.class);

        ThrowExceptionUtils.throwIfNull(annotation, ResultCode.FAILED);

        // 需要以 from 还是 current page 模式来获取页面信息
        PageInfoMode mode = annotation.pageInfoMode();
        // 页面的 size
        int size = annotation.size();

        // 获取传入的参数名
        String paramName = mode.paramName();
        // 获取传入的参数值（有可能为 null，也就是不存在）
        String paramValue = webRequest.getParameter(paramName);

        // 如果有值，就转化为数值（为空或 null 或无法转化为数字的时候，返回 0）
        int num = getNumberFromParamValue(paramValue);

        int from;
        switch (mode) {
            case FROM:
                // 此时，num 表示 from
                from = num;
                break;
            case CURRENT_PAGE:
            default:
                // 此时，num 表示 current page
                // 让 num 必须大于 0，然后将其设为变量 currentPage
                int currentPage = PageUtil.constrainGreaterThanZero(num);
                // 根据 currentPage 变量获取 from
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
                // 如果输入的字符串无法转化为数字，就打印日志，然后使用默认值
                e.printStackTrace();
                log.warn("Can't cast to number. Return 0 instead.");
            }
        }
        return num;
    }
}
