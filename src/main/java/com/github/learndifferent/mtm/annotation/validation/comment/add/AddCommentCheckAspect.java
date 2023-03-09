package com.github.learndifferent.mtm.annotation.validation.comment.add;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.BookmarkId;
import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.service.BookmarkService;
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
 * Throw an exception with the result code of {@link ResultCode#PERMISSION_DENIED}
 * if the user has no permissions to comment on this bookmark.
 * <p>
 * Throw an exception with the result code of {@link ResultCode#COMMENT_EXISTS}
 * if the comment existed .
 * <p>
 * Throw an exception with the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
 * if the bookmark does not exist.
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

    private final BookmarkService bookmarkService;

    private final CommentMapper commentMapper;

    @Autowired
    public AddCommentCheckAspect(BookmarkService bookmarkService,
                                 CommentMapper commentMapper) {
        this.bookmarkService = bookmarkService;
        this.commentMapper = commentMapper;
    }

    @Before("@annotation(addCommentCheck)")
    public void check(JoinPoint joinPoint, AddCommentCheck addCommentCheck) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        Object[] args = joinPoint.getArgs();

        String comment = "";
        int bookmarkId = -1;
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
                        && annotation instanceof BookmarkId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    bookmarkId = (int) args[i];
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
        // check if the bookmark exists
        // check whether the user has permission to comment (public or the user owns the bookmark)
        bookmarkService.checkBookmarkExistsAndUserPermission(bookmarkId, username);
        // 检查该用户是否已经对该网页进行了相同内容的评论
        checkCommentContentExists(comment, bookmarkId, username);
        // 如果是回复评论，需要检查回复的评论的 Comment ID 是否存在
        checkReplyToCommentId(replyToCommentId);
    }

    private void checkComment(String comment) {
        boolean commentIsEmpty = comment == null || "".equals(comment.trim());
        ThrowExceptionUtils.throwIfTrue(commentIsEmpty, ResultCode.COMMENT_EMPTY);

        boolean tooLong = comment.length() > 140;
        ThrowExceptionUtils.throwIfTrue(tooLong, ResultCode.COMMENT_TOO_LONG);
    }

    private void checkCommentContentExists(String comment, int bookmarkId, String username) {
        boolean isExists = commentMapper.checkIfCommentExists(comment, bookmarkId, username);
        ThrowExceptionUtils.throwIfTrue(isExists, ResultCode.COMMENT_EXISTS);
    }

    private void checkReplyToCommentId(Integer replyToCommentId) {
        if (replyToCommentId == null) {
            // null means it's a comment, not a reply
            return;
        }
        CommentDO comment = commentMapper.getCommentById(replyToCommentId);
        ThrowExceptionUtils.throwIfNull(comment, ResultCode.COMMENT_NOT_EXISTS);
    }
}