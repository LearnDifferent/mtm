package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Paginated public website data and total pages belonging to the user
 *
 * @author zhou
 * @date 2021/10/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserPublicWebInfoDTO implements Serializable {

    /**
     * Paginated website data
     */
    private List<WebsiteDTO> websiteData;

    /**
     * Total pages
     */
    private Integer totalPages;

    private static final long serialVersionUID = 1L;
}
