package com.github.learndifferent.mtm.annotation.validation.comment.modify;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.CommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.utils.CustomStringUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Check before comment modification
 *
 * @author zhou
 * @date 2021/9/29
 */
@Order(1)
@Aspect
@Component
public class ModifyCommentCheckAspect {

    private final CommentMapper commentMapper;

    @Autowired
    public ModifyCommentCheckAspect(CommentMapper commentMapper) {this.commentMapper = commentMapper;}

    @Before("@annotation(modifyCommentCheck)")
    public void checkBeforeModification(JoinPoint jp, ModifyCommentCheck modifyCommentCheck) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        Object[] args = jp.getArgs();

        String username = "";
        int commentId = -1;

        AnnotationHelper helper = new AnnotationHelper(2);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && annotation instanceof CommentId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    commentId = (int) args[i];
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

        check(username, commentId);
    }

    private void check(String username, int commentId) {
        CommentDO comment = commentMapper.getCommentById(commentId);
        // comment does not exist
        ThrowExceptionUtils.throwIfNull(comment, ResultCode.COMMENT_NOT_EXISTS);

        String owner = comment.getUsername();
        boolean hasNoPermission = CustomStringUtils.notEqualsIgnoreCase(username, owner);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }
}