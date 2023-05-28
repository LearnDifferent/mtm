package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Request body that contains title, URL, image and description
 *
 * @author zhou
 * @date 2021/9/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicWebDataRequest implements Serializable {

    /**
     * Title
     */
    private String title;
    /**
     * Url
     */
    @URL(message = ErrorInfoConstant.URL_INVALID)
    @NotBlank(message = ErrorInfoConstant.URL_INVALID)
    private String url;
    /**
     * Image
     */
    private String img;
    /**
     * Description
     */
    private String desc;

    private static final long serialVersionUID = 1L;
}