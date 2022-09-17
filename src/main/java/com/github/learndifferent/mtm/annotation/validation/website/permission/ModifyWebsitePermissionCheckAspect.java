package com.github.learndifferent.mtm.annotation.validation.website.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.CustomStringUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Verify whether the user has permission to modify the website data.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
public class ModifyWebsitePermissionCheckAspect {

    private final BookmarkMapper bookmarkMapper;

    @Autowired
    public ModifyWebsitePermissionCheckAspect(BookmarkMapper bookmarkMapper) {
        this.bookmarkMapper = bookmarkMapper;
    }

    @Before("@annotation(websitePermission)")
    public void check(JoinPoint joinPoint, ModifyWebsitePermissionCheck websitePermission) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        int webId = -1;
        String username = "";

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

        ThrowExceptionUtils.throwIfTrue(webId < 0, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        WebsiteDO bookmark = bookmarkMapper.getBookmarkById(webId);
        ThrowExceptionUtils.throwIfNull(bookmark, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        boolean emptyUsername = StringUtils.isEmpty(username);
        ThrowExceptionUtils.throwIfTrue(emptyUsername, ResultCode.USER_NOT_EXIST);

        boolean notTheOwner = CustomStringUtils.notEqualsIgnoreCase(username, bookmark.getUserName());
        ThrowExceptionUtils.throwIfTrue(notTheOwner, ResultCode.PERMISSION_DENIED);
    }
}