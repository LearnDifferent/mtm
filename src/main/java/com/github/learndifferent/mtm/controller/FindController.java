package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.manager.TrendsManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.vo.FindPageInitVO;
import com.github.learndifferent.mtm.vo.SearchResultsVO;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查找页面的 Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/find")
public class FindController {

    private final ElasticsearchManager elasticsearchManager;
    private final TrendsManager trendsManager;

    @Autowired
    public FindController(ElasticsearchManager elasticsearchManager,
                          TrendsManager trendsManager) {
        this.elasticsearchManager = elasticsearchManager;
        this.trendsManager = trendsManager;
    }

    /**
     * 载入的时候，获取热搜数据和是否存在可供搜索的数据
     *
     * @return 热搜数据和数据库是否存在
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping
    public ResultVO<FindPageInitVO> load() {

        // 热搜数据
        Set<String> trendingList = trendsManager.getTrends();
        // 是否存在可供搜索的数据
        boolean exist = elasticsearchManager.existsIndex();
        // 是否有新的更新
        boolean hasNewUpdate = elasticsearchManager.differentFromDatabase(exist);

        FindPageInitVO data = FindPageInitVO.builder()
                .trendingList(trendingList)
                .dataStatus(exist)
                .hasNewUpdate(hasNewUpdate)
                .build();

        return ResultCreator.okResult(data);
    }

    /**
     * 删除某个热搜词。
     * 非 Guest 账户才能删除热搜词，如果是 Guest 账户，
     * {@link NotGuest} 注解会抛出 {@link com.github.learndifferent.mtm.exception.ServiceException} 异常，
     * 异常的状态码为 {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *
     * @param word 被删除的热搜词
     * @return 是否删除成功
     * @throws com.github.learndifferent.mtm.exception.ServiceException 异常的状态码为 {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    @NotGuest
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/trends/{word}")
    public boolean deleteTrendsByWord(@PathVariable("word") String word) {
        return trendsManager.deleteTrendsByWord(word);
    }

    /**
     * 删除所有热搜词。
     * 非 Guest 账户才能删除热搜词，如果是 Guest 账户，
     * {@link NotGuest} 注解会抛出 {@link com.github.learndifferent.mtm.exception.ServiceException} 异常，
     * 异常的状态码为 {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *
     * @return 是否成功
     * @throws com.github.learndifferent.mtm.exception.ServiceException 异常的状态码为 {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    @NotGuest
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/trends")
    public boolean deleteAllTrends() {
        return trendsManager.deleteAllTrends();
    }

    /**
     * 搜索并返回查询到的网页数据
     *
     * @param pageInfo 分页数据
     * @param keyword  关键词（可以为空字符串或 null）
     * @return 查询到的网页结果
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping("/search")
    public ResultVO<SearchResultsVO> search(@PageInfo PageInfoDTO pageInfo,
                                            @RequestParam("keyword") String keyword) {

        SearchResultsDTO searchResultsDTO = elasticsearchManager.getSearchResult(keyword, pageInfo);
        SearchResultsVO results = DozerUtils.convert(searchResultsDTO, SearchResultsVO.class);

        return ResultCreator.okResult(results);
    }

    /**
     * 根据数据库中的数据重新生成 Elasticsearch 的数据库
     *
     * @return 是否成功
     */
    @SystemLog(optsType = OptsType.UPDATE)
    @GetMapping("/build")
    public boolean generateSearchDataBasedOnDatabase() {
        return elasticsearchManager.generateSearchData();
    }

    /**
     * 删除 Elasticsearch 中所有的数据
     *
     * @return 是否删除成功
     */
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/build")
    public boolean deleteSearch() {
        return elasticsearchManager.checkAndDeleteIndex();
    }

    @SystemLog(optsType = OptsType.CREATE)
    @GetMapping("/createIndex")
    public boolean hasIndexOrCreate() {
        // 初始化操作，生成用于搜索的 index（网络问题会抛出自定义的网络异常）
        return elasticsearchManager.hasIndexOrCreate();
    }
}
