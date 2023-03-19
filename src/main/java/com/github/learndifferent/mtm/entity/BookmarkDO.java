package com.github.learndifferent.mtm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("bookmark")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BookmarkDO implements Serializable {

    /**
     * ID of the bookmark
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * name of the user who bookmarked the website
     */
    private String userName;
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
    @TableField("`desc`")
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

    /**
     * 逻辑删除字段
     * 逻辑删除 @TableLogic
     * select 的时候不要选这个字段 @TableField(select = false)
     */
    @TableLogic
    @TableField(select = false)
    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}