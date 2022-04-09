package com.github.learndifferent.mtm.annotation.modify.webdata;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Clean Up {@link BasicWebDataDTO}'s fields:
 * 1. Check if the URLs are valid
 * 2. Clean up the format of the website URL
 * 3. If the title or description is empty, replace it with URL as content
 * 4. Shorten the title and description if necessary
 *
 * @author zhou
 * @date 2021/09/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebsiteDataClean {
}
