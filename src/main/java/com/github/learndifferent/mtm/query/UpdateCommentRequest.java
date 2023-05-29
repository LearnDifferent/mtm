package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

/**
 * Request body containing the comment information to update
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
     * ID of the comment
     */
    @NotNull(message = ErrorInfoConstant.COMMENT_NOT_FOUND)
    @Positive(message = ErrorInfoConstant.COMMENT_NOT_FOUND)
    private Integer id;

    /**
     * New comment
     */
    @NotBlank(message = ErrorInfoConstant.COMMENT_EMPTY)
    @Length(max = ConstraintConstant.COMMENT_MAX_LENGTH,
            message = "Comment should not be longer than {max} characters")
    private String comment;

    /**
     * ID of the bookmark
     */
    @NotNull(message = ErrorInfoConstant.COMMENT_NOT_FOUND)
    @Positive(message = ErrorInfoConstant.COMMENT_NOT_FOUND)
    private Integer bookmarkId;

    private static final long serialVersionUID = 1L;
}