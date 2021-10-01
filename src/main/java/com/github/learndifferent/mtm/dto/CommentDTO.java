package com.github.learndifferent.mtm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Comment DTO
 *
 * @author zhou
 * @date 2021/9/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CommentDTO implements Serializable {

    /**
     * Comment id
     */
    private Integer commentId;

    /**
     * content
     */
    private String comment;

    /**
     * Web id
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

    /**
     * Reply to the comment (null if it's not a reply)
     */
    private Integer replyToCommentId;

    private static final long serialVersionUID = 1L;
}
