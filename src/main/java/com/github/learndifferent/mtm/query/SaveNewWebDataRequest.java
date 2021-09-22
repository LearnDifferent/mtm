package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于接收 URL、用户名以及是否同步数据到 Elasticsearch
 *
 * @author zhou
 * @date 2021/09/13
 */
@Data
@NoArgsConstructor
public class SaveNewWebDataRequest implements Serializable {

    /**
     * Url
     */
    private String url;

    /**
     * Username
     */
    private String username;

    /**
     * True if the data should be synchronized to Elasticsearch
     */
    private Boolean syncToElasticsearch;

    private static final long serialVersionUID = 1L;
}
