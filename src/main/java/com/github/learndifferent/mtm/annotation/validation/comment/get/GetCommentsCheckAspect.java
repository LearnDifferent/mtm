package com.github.learndifferent.mtm.annotation.validation.comment.get;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebsiteWithPrivacyDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Check before getting comments.
 *
 * @author zhou
 * @date 2021/9/29
 * @see com.github.learndifferent.mtm.annotation.common.WebId
 * @see com.github.learndifferent.mtm.annotation.common.Username
 */
@Aspect
@Component
public class GetCommentsCheckAspect {

    private final WebsiteService websiteService;

    @Autowired
    public GetCommentsCheckAspect(WebsiteService websiteService) {this.websiteService = websiteService;}

    @Before("@annotation(getCommentsCheck)")
    public void check(JoinPoint jp, GetCommentsCheck getCommentsCheck) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        Object[] args = jp.getArgs();

        String username = "";
        int webId = -1;

        int count = 0;
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

        // 用户名是否为当前用户的用户名（并判断是否空）
        checkUsername(username);
        checkWebsiteExistsAndPermission(webId, username);
    }

    private void checkUsername(String username) {
        String currentUsername = (String) StpUtil.getLoginId();

        if (StringUtils.isEmpty(username)
                || ReverseUtils.stringNotEqualsIgnoreCase(username, currentUsername)) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }

    private void checkWebsiteExistsAndPermission(int webId, String username) {
        WebsiteWithPrivacyDTO web = websiteService.findWebsiteDataWithPrivacyById(webId);
        if (web == null) {
            throw new ServiceException(ResultCode.WEBSITE_DATA_NOT_EXISTS);
        }

        Boolean isPublic = web.getIsPublic();
        boolean isPrivate = BooleanUtils.isFalse(isPublic);
        String user = web.getUserName();

        if (isPrivate && ReverseUtils.stringNotEqualsIgnoreCase(username, user)) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
