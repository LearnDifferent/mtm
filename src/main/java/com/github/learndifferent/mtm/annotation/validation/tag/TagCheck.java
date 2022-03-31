package com.github.learndifferent.mtm.annotation.validation.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verify the length and the existence of a tag, and verify whether the tag has
 * already existed.
 * <p>
 * This annotation has to be used along with
 * {@link com.github.learndifferent.mtm.annotation.common.Tag Tag} annotation
 * and {@link com.github.learndifferent.mtm.annotation.common.WebId} annotation.
 * </p>
 *
 * @author zhou
 * @date 2022/3/31
 * @see TagCheckAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TagCheck {

    /**
     * Max length of the tag
     *
     * @return Max length of the tag
     */
    int maxLength() default 16;
}
