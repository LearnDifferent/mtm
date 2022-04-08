package com.github.learndifferent.mtm.annotation.validation.website.bookmarked;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Verify whether the user has already bookmarked the website by username and URL,
 * and verify whether the user has permission to bookmark the website.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
@Order(4)
public class BookmarkCheckAspect {

    private final WebsiteMapper websiteMapper;

    @Autowired
    public BookmarkCheckAspect(WebsiteMapper websiteMapper) {
        this.websiteMapper = websiteMapper;
    }

    @Before("@annotation(annotation)")
    public void check(JoinPoint joinPoint, BookmarkCheck annotation) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        Object[] args = joinPoint.getArgs();

        Class<? extends Serializable> classContainsUrl = annotation.paramClassContainsUrl();
        String urlFieldName = annotation.urlFieldNameInParamClass();

        String usernameParamName = annotation.usernameParamName();

        String url = "";
        String username = "";

        AnnotationHelper helper = new AnnotationHelper(2);

        for (int i = 0; i < parameterNames.length; i++) {
            if (helper.hasNotFoundIndex(0)
                    && classContainsUrl.isAssignableFrom(parameterTypes[i])
                    && args[i] != null) {

                url = getUrl(urlFieldName, args[i]);
                helper.findIndex(0);
            }

            if (helper.hasNotFoundIndex(1)
                    && usernameParamName.equals(parameterNames[i])
                    && String.class.isAssignableFrom(parameterTypes[i])
                    && ObjectUtils.isNotEmpty(args[i])) {
                username = (String) args[i];
                helper.findIndex(1);
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        testUserPermission(username);
        testIfUserAlreadyBookmarked(username, url);
    }

    private String getUrl(String urlFieldName, Object arg) {

        Class<?> clazz = arg.getClass();
        try {
            Field urlField = clazz.getDeclaredField(urlFieldName);
            urlField.setAccessible(true);
            return (String) urlField.get(arg);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            log.warn("Can't get the URL: Use empty string instead");
            return "";
        }
    }


    /**
     * Verify whether the user has permission to bookmark the website.
     *
     * @param username username
     * @throws ServiceException if the user is not the current user,
     *                          throw an exception with the result code of {@link ResultCode#PERMISSION_DENIED}
     */
    private void testUserPermission(String username) {
        String currentUsername = (String) StpUtil.getLoginIdDefaultNull();
        boolean notCurrentUser = CompareStringUtil.notEqualsIgnoreCase(username, currentUsername);
        ThrowExceptionUtils.throwIfTrue(notCurrentUser, ResultCode.PERMISSION_DENIED);
    }

    private void testIfUserAlreadyBookmarked(String userName, String url) {

        List<WebsiteDO> list = websiteMapper.findWebsitesDataByUrl(url);
        WebsiteDO bookmark = list.stream()
                .filter(w -> w.getUserName().equals(userName))
                .findFirst().orElse(null);

        ThrowExceptionUtils.throwIfNotNull(bookmark, ResultCode.ALREADY_SAVED);
    }
}
