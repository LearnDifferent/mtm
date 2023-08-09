package com.github.learndifferent.mtm.strategy;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Verify the existence of tag data in Elasticsearch
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(EsConstant.INDEX_TAG)
@RequiredArgsConstructor
public class TagDataExistenceVerification implements DataExistenceVerificationStrategy {

    private final ElasticsearchManager elasticsearchManager;

    @Override
    public boolean verifyDataExistence() {
        return elasticsearchManager.existsIndex(EsConstant.INDEX_TAG);
    }
}