package com.github.learndifferent.mtm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
