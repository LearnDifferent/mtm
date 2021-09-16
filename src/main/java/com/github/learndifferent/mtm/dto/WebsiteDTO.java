package com.github.learndifferent.mtm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 网页数据 dto
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteDTO implements Serializable {

    private Integer webId;
    private String userName;
    private String title;
    private String url;
    private String img;
    private String desc;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
