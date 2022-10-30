package com.github.learndifferent.mtm.annotation.validation.tag;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Tag;
import com.github.learndifferent.mtm.annotation.common.BookmarkId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.TagDO;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Verify a tag
 *
 * @author zhou
 * @date 2022/3/31
 * @see TagCheck
 */
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class TagCheckAspect {

    private final TagMapper tagMapper;

    @Autowired
    public TagCheckAspect(TagMapper tagMapper) {this.tagMapper = tagMapper;}

    @Before("@annotation(annotation)")
    public void check(JoinPoint joinPoint, TagCheck annotation) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        AnnotationHelper helper = new AnnotationHelper(2);

        String tagText = "";
        int bookmarkId = -1;

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation an : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && an instanceof Tag
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    tagText = (String) args[i];
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && an instanceof BookmarkId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    bookmarkId = (int) args[i];
                    helper.findIndex(1);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        // Check the existence of the bookmark should be done by @ModifyBookmarkPermissionCheck
        // This is just a simple check
        ThrowExceptionUtils.throwIfTrue(bookmarkId < 0, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        checkTagBasic(tagText, annotation.maxLength());
        checkTagExists(tagText, bookmarkId);
    }

    private void checkTagExists(String tagText, int bookmarkId) {
        TagDO tag = tagMapper.getSpecificTagByTagTextAndBookmarkId(tagText, bookmarkId);
        ThrowExceptionUtils.throwIfNotNull(tag, ResultCode.TAG_EXISTS);
    }

    private void checkTagBasic(String tag, int maxLength) {
        boolean isEmpty = StringUtils.isEmpty(tag);
        ThrowExceptionUtils.throwIfTrue(isEmpty, ResultCode.TAG_NOT_EXISTS);

        boolean hasExceeded = tag.length() > maxLength;
        ThrowExceptionUtils.throwIfTrue(hasExceeded, ResultCode.TAG_TOO_LONG);
    }
}
