package com.github.learndifferent.mtm.annotation.validation.website.delete;

import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 获取网页 ID 和用户名信息，并检查是否有删除的权限
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
public class DeleteWebsitePermissionAspect {

    private final WebsiteService websiteService;

    @Autowired
    public DeleteWebsitePermissionAspect(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @Before("@annotation(websitePermission)")
    public void check(JoinPoint joinPoint, DeleteWebsitePermission websitePermission) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        String[] parameterNames = signature.getParameterNames();

        HttpServletRequest request = getRequest();
        int webId = 0;
        String username = "";

        int count = 0;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof WebId) {
                    webId = getParamValue(request, parameterNames[i], Integer.class);
                    count++;
                    break;
                }
                if (annotation instanceof Username) {
                    username = getParamValue(request, parameterNames[i], String.class);
                    count++;
                    break;
                }
            }

            if (count == 2) {
                break;
            }
        }

        checkDeletePermission(webId, username);
    }

    @NotNull
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ServiceException("No attributes available.");
        }

        return attributes.getRequest();
    }

    private <T> T getParamValue(@NotNull HttpServletRequest request,
                                String paramName,
                                Class<T> clazz) {

        String paramValue = request.getParameter(paramName);
        if (StringUtils.isEmpty(paramValue)) {
            throw new ServiceException("Can't get parameter: " + paramName);
        }

        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(paramValue);
        } else if (Integer.class.isAssignableFrom(clazz)) {
            int result = castStringToInt(paramValue);
            return clazz.cast(result);
        } else {
            throw new ServiceException("不支持类型：" + clazz.getName());
        }
    }

    private int castStringToInt(String num) {

        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            log.info("传入的值 " + num + " 不是数字，自动转化为 0");
            return 0;
        }
    }

    /**
     * 查看该用户是否有删除该网页的权限
     *
     * @param userName 需要删除网页的用户
     * @param webId    需要被删除的网页 ID
     * @throws ServiceException 如果没有权限，就抛出异常
     */
    private void checkDeletePermission(int webId, String userName) {

        WebsiteDTO web = websiteService.findWebsiteDataById(webId);

        if (ReverseUtils.hasNoPermissionToDelete(userName, web)) {
            // 没有权限就抛出异常
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
