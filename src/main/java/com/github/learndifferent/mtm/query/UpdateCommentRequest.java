package com.github.learndifferent.mtm.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 更新评论的 Request Body
 *
 * @author zhou
 * @date 2021/9/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class UpdateCommentRequest implements Serializable {

    /**
     * Comment ID
     */
    private Integer commentId;

    /**
     * New comment
     */
    private String comment;

    /**
     * Website ID
     */
    private Integer webId;

    /**
     * Username
     */
    private String username;

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    private static final long serialVersionUID = 1L;
}