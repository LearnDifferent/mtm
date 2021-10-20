package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebWithPrivacyCommentCountDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * My website data
 *
 * @author zhou
 * @date 2021/10/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class MyWebsiteDataVO implements Serializable {

    /**
     * My paginated website data
     */
    private List<WebWithPrivacyCommentCountDTO> myWebsiteData;

    /**
     * Total pages
     */
    private Integer totalPages;

    private static final long serialVersionUID = 1L;
}
