package com.github.learndifferent.mtm.annotation.validation.comment;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 检查：
 * 1. 用户名是否为当前用户的用户名
 * 2. 该 Web ID 的网页是否存在
 * 3. 评论是否小于等于 140 字符且不为空
 * 4. 检查该用户是否已经对该网页进行了相同内容的评论
 *
 * @author zhou
 * @date 2021/9/28
 */
@Aspect
@Component
public class AddCommentCheckAspect {

    private final WebsiteService websiteService;

    private final CommentService commentService;

    @Autowired
    public AddCommentCheckAspect(WebsiteService websiteService,
                                 CommentService commentService) {
        this.websiteService = websiteService;
        this.commentService = commentService;
    }

    @Before("@annotation(addCommentCheck)")
    public void check(JoinPoint joinPoint, AddCommentCheck addCommentCheck) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        Object[] args = joinPoint.getArgs();

        String comment = "";
        int webId = -1;
        String username = "";

        int counter = 0;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Comment
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    comment = (String) args[i];
                    counter++;
                    break;
                }
                if (annotation instanceof WebId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    webId = (int) args[i];
                    counter++;
                    break;
                }
                if (annotation instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    counter++;
                    break;
                }

            }

            if (counter == 3) {
                break;
            }
        }

        // 评论是否小于等于 140 字符且不为空
        checkComment(comment);
        // 该 Web ID 的网页是否存在
        checkWebsiteExists(webId);
        // 用户名是否为当前用户的用户名
        checkUsername(username);
        // 检查该用户是否已经对该网页进行了相同内容的评论
        checkCommentExists(comment, webId, username);
    }

    private void checkComment(String comment) {
        if (StringUtils.isEmpty(comment)) {
            throw new ServiceException(ResultCode.COMMENT_EMPTY);
        }
        int maxLength = 140;
        if (comment.length() > maxLength) {
            throw new ServiceException(ResultCode.COMMENT_TOO_LONG);
        }
    }

    private void checkWebsiteExists(int webId) {
        WebsiteDTO web = websiteService.findWebsiteDataById(webId);
        if (web == null) {
            throw new ServiceException(ResultCode.WEBSITE_DATA_NOT_EXISTS);
        }
    }

    private void checkUsername(String username) {
        String currentUsername = (String) StpUtil.getLoginId();

        if (StringUtils.isEmpty(username)
                || ReverseUtils.stringNotEqualsIgnoreCase(username, currentUsername)) {
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
    }

    private void checkCommentExists(String comment, int webId, String username) {
        CommentDO exist = commentService.getCommentByWebIdAndUsernameAndComment(
                comment, webId, username);
        if (exist != null) {
            throw new ServiceException(ResultCode.COMMENT_EXISTS);
        }
    }
}
