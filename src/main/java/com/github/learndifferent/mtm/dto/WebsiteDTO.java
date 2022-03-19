package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Website data without privacy settings
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteDTO implements Serializable {

    /**
     * Web ID
     */
    private Integer webId;
    /**
     * Username
     */
    private String userName;
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
    /**
     * Creation date
     */
    private Instant createTime;

    private static final long serialVersionUID = 1L;
}
