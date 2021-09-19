package com.github.learndifferent.mtm.vo;

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
public class NewWebVO implements Serializable {

    /**
     * url
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 是否同步数据到 Elasticsearch
     */
    private Boolean syncToElasticsearch;

    private static final long serialVersionUID = 1L;
}
