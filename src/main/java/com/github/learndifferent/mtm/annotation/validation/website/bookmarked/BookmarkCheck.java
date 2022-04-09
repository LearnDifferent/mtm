package com.github.learndifferent.mtm.annotation.validation.website.bookmarked;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verify whether the user has already bookmarked the website by username and URL
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BookmarkCheck {

    /**
     * Name of the parameter that contains username
     *
     * @return name of the parameter that contains username
     */
    String usernameParamName();

    /**
     * Class name of the parameter that has a field that contains URL
     *
     * @return name of the class that has a field that contains URL
     */
    Class<? extends Serializable> classContainsUrlParamName();

    /**
     * Name of the field in the {@link #classContainsUrlParamName() class} that contains URL
     *
     * @return name of the field in the {@link #classContainsUrlParamName() class} that contains URL
     */
    String urlFieldNameInParamClass();
}
