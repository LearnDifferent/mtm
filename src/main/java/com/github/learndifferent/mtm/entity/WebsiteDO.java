package com.github.learndifferent.mtm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 网页数据（为了方便，将 DTO 和 DO 合并在一起了）
 *
 * @author zhou
 * @date 2021/09/05
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WebsiteDO implements Serializable {

    /**
     * 网页 ID（就算是相同 URL 的网页，只要被不同的用户收藏就算独立存在的）
     */
    private Integer webId;
    /**
     * 收藏该网页的用户
     */
    private String userName;
    /**
     * 网页标题
     */
    private String title;
    /**
     * 网页链接
     */
    private String url;
    /**
     * 网页图片
     */
    private String img;
    /**
     * 网页简介
     */
    private String desc;
    /**
     * 网页数据的创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    /**
     * True if this is a public post
     */
    private Boolean isPublic;

    private static final long serialVersionUID = 1L;
}
