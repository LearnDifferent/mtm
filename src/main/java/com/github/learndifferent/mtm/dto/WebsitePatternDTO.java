package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * WebsitePattern 的 dto
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebsitePatternDTO implements Serializable {

    /**
     * 分页后展示出来的网页数据
     */
    private List<? extends WebsiteDTO> webs;
    /**
     * 分页后的总页数
     */
    Integer totalPage;

    private static final long serialVersionUID = 1L;
}
