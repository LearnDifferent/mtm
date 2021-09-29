package com.github.learndifferent.mtm.annotation.validation.comment.delete;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.CommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Check before comment deletion
 *
 * @author zhou
 * @date 2021/9/29
 * @see com.github.learndifferent.mtm.annotation.common.CommentId
 * @see com.github.learndifferent.mtm.annotation.common.Username
 */
@Aspect
@Component
public class DeleteCommentCheckAspect {

    private final CommentService commentService;

    @Autowired
    public DeleteCommentCheckAspect(CommentService commentService) {this.commentService = commentService;}

    @Before("@annotation(deleteCommentCheck)")
    public void checkBeforeDeleting(JoinPoint jp, DeleteCommentCheck deleteCommentCheck) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        Object[] args = jp.getArgs();

        String username = "";
        int commentId = -1;

        int count = 0;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof CommentId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    commentId = (int) args[i];
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

        check(username, commentId);
    }

    private void check(String username, int commentId) {
        CommentDO comment = commentService.getCommentById(commentId);
        if (comment == null) {
            // 评论不存在的情况
            throw new ServiceException(ResultCode.COMMENT_NOT_EXISTS);
        }

        String currentUsername = (String) StpUtil.getLoginId();
        String user = comment.getUsername();
        if (ReverseUtils.stringNotEqualsIgnoreCase(currentUsername, username)
                || ReverseUtils.stringNotEqualsIgnoreCase(username, user)) {
            // 用户名不是当前用户名，或者不是该评论的所有者的情况
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }
}
