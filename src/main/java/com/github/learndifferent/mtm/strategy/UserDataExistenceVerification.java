package com.github.learndifferent.mtm.strategy;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Verify the existence of user data in Elasticsearch
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(EsConstant.INDEX_USER)
@RequiredArgsConstructor
public class UserDataExistenceVerification implements DataExistenceVerificationStrategy {

    private ElasticsearchManager elasticsearchManager;

    @Autowired
    public void setElasticsearchManager(ElasticsearchManager elasticsearchManager) {
        this.elasticsearchManager = elasticsearchManager;
    }

    @Override
    public boolean verifyDataExistence() {
        return elasticsearchManager.existsIndex(EsConstant.INDEX_USER);
    }
}
