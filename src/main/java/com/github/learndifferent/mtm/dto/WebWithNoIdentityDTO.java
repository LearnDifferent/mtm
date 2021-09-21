package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Website data that has no web id, username and creation time
 * , which only contains title, url, image and description.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebWithNoIdentityDTO implements Serializable {

    private String title;
    private String url;
    private String img;
    private String desc;

    private static final long serialVersionUID = 1L;
}
