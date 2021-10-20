package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebsitePatternDTO;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Home 页面需要展示的数据
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class HomePageVO implements Serializable {

    /**
     * Current user
     */
    private String currentUser;
    /**
     * Paginated website data and total pages
     */
    private WebsitePatternDTO websiteDataInfo;
    /**
     * Username for further operation
     */
    private String optUsername;

    private static final long serialVersionUID = 1L;
}