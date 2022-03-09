package com.github.learndifferent.mtm.annotation.modify.url;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Clean up the URL when the URL is annotated
 * with {@link com.github.learndifferent.mtm.annotation.common.Url}
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlClean {}
