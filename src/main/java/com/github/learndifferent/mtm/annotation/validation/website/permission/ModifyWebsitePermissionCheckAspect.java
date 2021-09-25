package com.github.learndifferent.mtm.annotation.validation.website.permission;

import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取网页 ID 和用户名信息，并检查是否该网页是否属于该用户。
 * 只有该用户才有修改该网页的权限。
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
public class ModifyWebsitePermissionCheckAspect {

    private final WebsiteService websiteService;

    @Autowired
    public ModifyWebsitePermissionCheckAspect(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    @Before("@annotation(websitePermission)")
    public void check(JoinPoint joinPoint, ModifyWebsitePermissionCheck websitePermission) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        int webId = 0;
        String username = "";

        int count = -1;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof WebId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    webId = (int) args[i];
                    count++;
                    break;
                }
                if (annotation instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    count++;
                    break;
                }
            }

            if (count == 2) {
                break;
            }
        }

        checkPermission(webId, username);
    }

    /**
     * 查看该用户是否有修改该网页的权限
     *
     * @param userName 需要删除网页的用户
     * @param webId    需要被删除的网页 ID
     * @throws ServiceException 如果没有权限，就抛出异常。其他错误，也会抛出相应异常
     */
    private void checkPermission(int webId, String userName) {

        if (webId < 0) {
            throw new ServiceException(ResultCode.WEBSITE_DATA_NOT_EXISTS);
        }

        if ("".equals(userName)) {
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }

        WebsiteDTO web = websiteService.findWebsiteDataById(webId);

        if (ReverseUtils.hasNoPermissionToDelete(userName, web)) {
            // 没有权限就抛出异常
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
