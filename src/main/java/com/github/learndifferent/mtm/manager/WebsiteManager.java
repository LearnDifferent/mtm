package com.github.learndifferent.mtm.manager;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.constant.enums.ShowPattern;
import com.github.learndifferent.mtm.dto.*;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.vo.NewWebVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 操作网页数据的一些特殊操作
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class WebsiteManager {

    private final WebsiteService websiteService;
    private final ElasticsearchManager elasticsearchManager;

    @Autowired
    public WebsiteManager(WebsiteService websiteService,
                          ElasticsearchManager elasticsearchManager) {
        this.websiteService = websiteService;
        this.elasticsearchManager = elasticsearchManager;
    }

    /**
     * 根据 URL 和用户名，收藏新的网页数据
     *
     * @param newWebVO URL、用户名，以及是否同步数据到 Elasticsearch
     * @return {@code boolean[]} boolean 数组 index 为 0 的位置表示是否存放到数据库中，
     * boolean 数组 index 为 1 的位置表示是否存放到 Elasticsearch 中。
     */
    public boolean[] saveNewWebsiteData(NewWebVO newWebVO) {

        String url = newWebVO.getUrl();
        String username = newWebVO.getUsername();
        Boolean syncToElasticsearch = newWebVO.getSyncToElasticsearch();

        WebWithNoIdentityDTO rawWebsite = websiteService.scrapeWebsiteDataFromUrl(url, username);

        // 如果选择同步到 Elasticsearch 中，就异步执行保存网页数据的方法并返回结果
        // 如果选择不同步，也就是 syncToEs 为 false 或 null 的情况：直接返回 true 作为结果，表示无需异步存放
        Future<Boolean> futureResult = elasticsearchManager
                .saveDocAsync(rawWebsite, syncToElasticsearch);

        // 将网页数据放入数据库，返回是否成功放入
        boolean toDatabase = websiteService.saveWebsiteData(rawWebsite, username);

        // true 表示存放成功或无需存放到 Elasticsearch；false 表示存放失败
        boolean toElasticsearch = true;
        try {
            // 获取异步存放数据到 Elasticsearch 的结果
            toElasticsearch = futureResult.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return new boolean[]{toDatabase, toElasticsearch};
    }

    /**
     * 获取该 pattern 下，分页后需要的网页数据和总页数
     *
     * @param pattern  默认模式为查询所有，如果有指定的模式，就按照指定模式获取
     * @param pageInfo 分页相关信息
     * @param username 如果需要用户名，就传入用户名；如果不需要，就传入空字符串；
     * @return 该 pattern 下，分页后需要的网页数据和总页数
     */
    @EmptyStringCheck
    public WebsitePatternDTO getWebsiteByPattern(
            @EmptyStringCheck.DefaultValueIfEmpty String pattern,
            PageInfoDTO pageInfo,
            @EmptyStringCheck.DefaultValueIfEmpty String username) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        WebsitePatternDTO.WebsitePatternDTOBuilder builder = WebsitePatternDTO.builder();

        ShowPattern showPattern = castPatternStringToPatternEnum(pattern);

        switch (showPattern) {
            case MARKED:
                // 最多收藏的情况
                List<WebsiteWithCountDTO> markedWebs = websiteService.showMostMarked(from, size);
                int markedTotalCount = websiteService.countDistinctUrl();
                int markedTotalPage = PageUtil.getAllPages(markedTotalCount, size);
                builder.webs(markedWebs).totalPage(markedTotalPage);
                break;
            case USER_PAGE:
                // 查看某个用户所有收藏的情况
                List<WebsiteDTO> userPageWebs = websiteService.findWebsitesDataByUser(username, from, size);
                int userPageTotalCount = websiteService.countUserPost(username);
                int userPageTotalPage = PageUtil.getAllPages(userPageTotalCount, size);
                builder.webs(userPageWebs).totalPage(userPageTotalPage);
                break;
            case WITHOUT_USER_PAGE:
                // 查看除去某个用户的所有收藏的情况
                List<WebsiteDTO> withoutUserPageWebs = websiteService.findWebsitesDataExcludeUser(username, from, size);
                int withoutUserPageTotalCount = websiteService.countExcludeUserPost(username);
                int withoutUserPageTotalPage = PageUtil.getAllPages(withoutUserPageTotalCount, size);
                builder.webs(withoutUserPageWebs).totalPage(withoutUserPageTotalPage);
                break;
            case DEFAULT:
            default:
                // 默认查看全部的情况（如果 pattern 不是以上的情况，也是按照默认情况处理）
                List<WebsiteDTO> webs = websiteService.showAllWebsiteDataDesc(from, size);
                int totalCount = websiteService.countAll();
                int totalPage = PageUtil.getAllPages(totalCount, size);
                builder.webs(webs).totalPage(totalPage);
        }

        return builder.build();
    }

    private ShowPattern castPatternStringToPatternEnum(String pattern) {

        pattern = camelToSnake(pattern).toUpperCase();

        try {
            return ShowPattern.valueOf(pattern);
        } catch (IllegalArgumentException | NullPointerException e) {
            // 找不到的时候，返回默认值
            return ShowPattern.DEFAULT;
        }
    }

    private String camelToSnake(String value) {
        return new PropertyNamingStrategy.SnakeCaseStrategy().translate(value);
    }
}
