package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Request body of existing website data that contains title, url, image and description.
 *
 * @author zhou
 * @date 2021/9/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SaveWebDataRequest implements Serializable {

    private String title;
    private String url;
    private String img;
    private String desc;

    private static final long serialVersionUID = 1L;
}