package com.github.learndifferent.mtm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Bookmark Data Object
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("bookmark")
public class BookmarkDO implements Serializable {

    /**
     * ID of the bookmark
     */
    @TableId("id")
    private Long id;
    /**
     * User ID of the owner of the bookmark
     */
    private Long userId;
    /**
     * title of the bookmarked website
     */
    private String title;
    /**
     * URL of the bookmarked website
     */
    private String url;
    /**
     * Image of the bookmarked website
     */
    private String img;
    /**
     * Description of the bookmarked website
     */
    private String desc;
    /**
     * Creation time
     */
    @TableField("creation_time")
    private Instant createTime;

    /**
     * True if this is a public bookmark
     */
    private Boolean isPublic;

    @TableLogic("is_deleted")
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}
