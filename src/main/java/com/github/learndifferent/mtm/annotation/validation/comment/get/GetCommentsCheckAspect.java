package com.github.learndifferent.mtm.annotation.validation.comment.get;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.service.WebsiteService;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public GetCommentsCheckAspect(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

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

        websiteService.checkBookmarkExistsAndUserPermission(webId, username);
    }
}