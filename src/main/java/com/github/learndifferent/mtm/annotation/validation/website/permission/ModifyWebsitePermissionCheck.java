package com.github.learndifferent.mtm.annotation.validation.website.permission;

import com.github.learndifferent.mtm.annotation.common.BookmarkId;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verify whether the user has permission to modify the website data.
 * This annotation has to be used along with {@link BookmarkId}
 * and {@link com.github.learndifferent.mtm.annotation.common.Username}
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyWebsitePermissionCheck {}
