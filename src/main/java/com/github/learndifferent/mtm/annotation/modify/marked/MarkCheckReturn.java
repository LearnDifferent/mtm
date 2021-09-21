package com.github.learndifferent.mtm.annotation.modify.marked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根据用户名和 URL 查看网页数据是否已经被收藏。配合 {@link com.github.learndifferent.mtm.annotation.common.Url}
 * 和 {@link com.github.learndifferent.mtm.annotation.common.Username} 使用。
 * <p>
 * 如果已经收藏了，就抛出异常。
 * 如果还没收藏，且数据库中有，就返回数据库中的数据。
 * 如果还没收藏，而数据库中也没有，再继续运行。
 * </p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarkCheckReturn {}
