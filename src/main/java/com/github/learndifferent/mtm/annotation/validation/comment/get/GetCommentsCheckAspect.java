package com.github.learndifferent.mtm.annotation.validation.comment.get;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebsiteWithPrivacyDTO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
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

        AnnotationHelper helper = new AnnotationHelper(2);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && annotation instanceof WebId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    webId = (int) args[i];
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && annotation instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    helper.findIndex(1);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        // 用户名是否为当前用户的用户名（并判断是否空）
        checkUsername(username);
        checkWebsiteExistsAndPermission(webId, username);
    }

    private void checkUsername(String username) {
        String currentUsername = StpUtil.getLoginIdAsString();

        boolean hasNoPermission = StringUtils.isEmpty(username)
                || CompareStringUtil.notEqualsIgnoreCase(username, currentUsername);

        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }

    private void checkWebsiteExistsAndPermission(int webId, String username) {
        WebsiteWithPrivacyDTO web = websiteService.findWebsiteDataWithPrivacyById(webId);

        ThrowExceptionUtils.throwIfNull(web, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        Boolean isPublic = web.getIsPublic();
        boolean isPrivate = BooleanUtils.isFalse(isPublic);
        String user = web.getUserName();

        boolean hasNoPermission = isPrivate && CompareStringUtil.notEqualsIgnoreCase(username, user);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }
}
