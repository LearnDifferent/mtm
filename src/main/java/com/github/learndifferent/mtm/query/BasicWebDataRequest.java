package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body that contains title, URL, image and description
 *
 * @author zhou
 * @date 2021/9/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicWebDataRequest implements Serializable {

    /**
     * Title
     */
    private String title;
    /**
     * Url
     */
    private String url;
    /**
     * Image
     */
    private String img;
    /**
     * Description
     */
    private String desc;

    private static final long serialVersionUID = 1L;
}