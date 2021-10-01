package com.github.learndifferent.mtm.annotation.validation.comment.add;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查 {@link com.github.learndifferent.mtm.annotation.common.WebId} 注释对应的参数的网页 ID 是否存在，
 * {@link com.github.learndifferent.mtm.annotation.common.Username} 注解所对应的参数的用户是否为当前用户，
 * {@link com.github.learndifferent.mtm.annotation.common.Comment} 注解所对应的评论是否不为空，且小于等于 140 个字符，
 * 以及检查该用户是否已经对该网页进行了相同内容的评论，还有该用户是否有评论该网页的权限。
 * 如果是回复评论，还要检查回复的 {@link com.github.learndifferent.mtm.annotation.common.ReplyToCommentId} 所属的评论是否存在。
 *
 * @author zhou
 * @date 2021/9/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AddCommentCheck {}
