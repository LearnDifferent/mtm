package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Basic website data that only contains title, url, image and description
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class BasicWebDataDTO implements Serializable {

    /**
     * Title
     */
    private String title;
    /**
     * URL
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
