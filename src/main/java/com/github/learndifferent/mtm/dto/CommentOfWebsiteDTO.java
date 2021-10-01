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
 * Comment of the website
 *
 * @author zhou
 * @date 2021/9/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CommentOfWebsiteDTO implements Serializable {

    /**
     * Comment id
     */
    private Integer commentId;

    /**
     * content
     */
    private String comment;

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
