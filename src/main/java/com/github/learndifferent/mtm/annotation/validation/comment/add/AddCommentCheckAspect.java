package com.github.learndifferent.mtm.annotation.validation.comment.add;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithPrivacyDTO;
import com.github.learndifferent.mtm.service.CommentService;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Throw an exception with the result code of {@link ResultCode#PERMISSION_DENIED}
 * if the username is not the current user's name or the user has no permissions to comment on this website.
 * <p>
 * Throw an exception with the result code of {@link ResultCode#COMMENT_EXISTS}
 * if the comment existed .
 * <p>
 * Throw an exception with the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
 * if the website does not exist.
 * <p>
 * Throw an exception with the result code of {@link ResultCode#COMMENT_EMPTY}
 * if the comment is empty.
 * <p>
 * Throw an exception with the result code of {@link ResultCode#COMMENT_TOO_LONG}
 * if the comment is too long.
 * <p>
 * Throw an exception with the result code of {@link ResultCode#COMMENT_NOT_EXISTS}
 * if the comment is a reply to another comment and the "another comment" does not exist
 *
 * @author zhou
 * @date 2021/9/28
 */
@Aspect
@Order(2)
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
        Integer replyToCommentId = null;

        AnnotationHelper helper = new AnnotationHelper(4);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundIndex(0)
                        && annotation instanceof Comment
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    comment = (String) args[i];
                    helper.findIndex(0);
                    break;
                }
                if (helper.hasNotFoundIndex(1)
                        && annotation instanceof WebId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    webId = (int) args[i];
                    helper.findIndex(1);
                    break;
                }
                if (helper.hasNotFoundIndex(2)
                        && annotation instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    helper.findIndex(2);
                    break;
                }
                if (helper.hasNotFoundIndex(3)
                        && annotation instanceof ReplyToCommentId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    replyToCommentId = (Integer) args[i];
                    helper.findIndex(3);
                    break;
                }
            }

            if (helper.hasFoundAll()) {
                break;
            }
        }

        // 评论是否小于等于 140 字符且不为空
        checkComment(comment);
        // 用户名是否为当前用户的用户名（并判断是否空）
        checkUsername(username);
        // 该 Web ID 的网页是否存在，以及该用户是否有权限评论该网页
        // 公开的网页，及该用户所有的网页才有权限评论
        checkWebsiteExistsAndPermission(webId, username);
        // 检查该用户是否已经对该网页进行了相同内容的评论
        checkCommentContentExists(comment, webId, username);
        // 如果是回复评论，需要检查回复的评论的 Comment ID 是否存在
        checkReplyToCommentId(replyToCommentId);
    }

    private void checkComment(String comment) {
        boolean commentIsEmpty = comment == null || "".equals(comment.trim());
        ThrowExceptionUtils.throwIfTrue(commentIsEmpty, ResultCode.COMMENT_EMPTY);

        boolean tooLong = comment.length() > 140;
        ThrowExceptionUtils.throwIfTrue(tooLong, ResultCode.COMMENT_TOO_LONG);
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

    private void checkCommentContentExists(String comment, int webId, String username) {
        CommentDTO exist = commentService.getCommentByWebIdAndUsernameAndComment(
                comment, webId, username);
        ThrowExceptionUtils.throwIfNotNull(exist, ResultCode.COMMENT_EXISTS);
    }

    private void checkReplyToCommentId(Integer replyToCommentId) {
        if (replyToCommentId == null) {
            // null means it's a comment, not a reply
            return;
        }
        CommentDTO comment = commentService.getCommentById(replyToCommentId);
        ThrowExceptionUtils.throwIfNull(comment, ResultCode.COMMENT_NOT_EXISTS);
    }
}
