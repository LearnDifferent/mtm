package com.github.learndifferent.mtm.annotation.modify.webdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 清理 Website 数据中 title 和 desc 的长度，判断 url 格式是否正确，以及清理 url
 *
 * @author zhou
 * @date 2021/09/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebsiteDataClean {
}
