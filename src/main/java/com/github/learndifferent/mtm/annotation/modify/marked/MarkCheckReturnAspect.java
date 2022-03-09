package com.github.learndifferent.mtm.annotation.modify.marked;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Url;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Check if the website is bookmarked by the user
 * and the website data is stored in database according to {@link Username} and {@link Url}.
 * If the user has already bookmarked the website, then throw an exception
 * with the result code of {@link ResultCode#ALREADY_MARKED}.
 * If the user didn't bookmark the website and the website data is stored in database, then return the data in database.
 * If the user didn't bookmark the website and website data is not stored in database, then do nothing.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@Order(2)
public class MarkCheckReturnAspect {

    private final WebsiteService websiteService;

    @Autowired
    public MarkCheckReturnAspect(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * Check if the website is bookmarked by the user
     * and the website data is stored in database according to username and url.
     *
     * @param pjp             Proceeding Join Point
     * @param markCheckReturn IfMarkedThenReturn annotation
     * @return {@code Object}
     * @throws Throwable Throw an exception with the result code of {@link ResultCode#ALREADY_MARKED}
     *                   if the user has already bookmarked the website
     */
    @Around("@annotation(markCheckReturn)")
    public Object around(ProceedingJoinPoint pjp, MarkCheckReturn markCheckReturn) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = pjp.getArgs();

        String url = "";
        String username = "";

        AnnotationHelper helper = new AnnotationHelper(2);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && annotation instanceof Url
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    url = (String) args[i];
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

        // Find the website data in database by URL
        List<WebsiteDTO> websInDb = websiteService.findWebsitesDataByUrl(url);

        // If not found, do nothing
        if (websInDb.isEmpty()) {
            return pjp.proceed();
        }

        // If found, check whether the user has already bookmarked the website
        checkIfMarked(username, websInDb);
        // If not bookmarked yet, return the website data in database
        return DozerUtils.convert(websInDb.get(0), WebWithNoIdentityDTO.class);
    }

    /**
     * Check if the website is bookmarked by the user
     *
     * @param username Username
     * @param websInDb Website data in database
     * @throws ServiceException Throw an exception with the result code of {@link ResultCode#ALREADY_MARKED}
     *                          if the user has already bookmarked the website
     */
    private void checkIfMarked(String username, List<WebsiteDTO> websInDb) {
        WebsiteDTO userMarkedWeb = websInDb.stream()
                .filter(w -> w.getUserName().equals(username))
                .findFirst().orElse(null);

        ThrowExceptionUtils.throwIfNotNull(userMarkedWeb, ResultCode.ALREADY_MARKED);
    }

}
