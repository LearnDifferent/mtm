package com.github.learndifferent.mtm.annotation.modify.marked;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Check if the website is bookmarked by the user and the website data is stored in database
 * according to {@link com.github.learndifferent.mtm.annotation.common.Username}
 * and {@link com.github.learndifferent.mtm.annotation.common.Url}.
 * If the user has already bookmarked the website, then throw an exception.
 * If the user didn't bookmark the website and the website data is stored in database,
 * then return the data in database.
 * If the user didn't bookmark the website and website data is not stored in database, then do nothing.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarkCheckReturn {}
