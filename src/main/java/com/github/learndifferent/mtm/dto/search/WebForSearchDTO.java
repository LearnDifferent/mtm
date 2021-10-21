package com.github.learndifferent.mtm.dto.search;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Elasticsearch 索引的数据结构（这里归为了 DTO，实际上也是 DO）
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebForSearchDTO implements Serializable, DataForSearch {

    /**
     * Title
     */
    private String title;
    /**
     * Url
     */
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
