package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.SearchService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.vo.FindPageVO;
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
 * Search Page Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/find")
public class FindController {

    private final SearchService searchService;

    @Autowired
    public FindController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Get trending searches, existent of data for search and the update information.
     *
     * @return {@link ResultVO}<{@link FindPageVO}> trending searches, existent of data for search and update information
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping
    public ResultVO<FindPageVO> load() {

        // trending searches
        Set<String> trendingList = searchService.getTrends();
        // existent of website data for search
        boolean exist = searchService.existsIndex(EsConstant.INDEX_WEB);
        // update information
        boolean hasNewUpdate = searchService.websiteDataDiffFromDatabase(exist);

        FindPageVO data = FindPageVO.builder()
                .trendingList(trendingList)
                .dataStatus(exist)
                .hasNewUpdate(hasNewUpdate)
                .build();

        return ResultCreator.okResult(data);
    }

    /**
     * Delete specific trending keyword (Guest does not have the permission)
     *
     * @param word keyword to delete
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotGuest} will throw exception if the
     *                                                                  user is a guest with the result code of {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    @NotGuest
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/trends/{word}")
    public boolean deleteTrendsByWord(@PathVariable("word") String word) {
        return searchService.deleteTrendsByWord(word);
    }

    /**
     * Delete all trending keyword (Guest does not have the permission)
     *
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotGuest} will throw exception if the
     *                                                                  user is a guest with the result code of {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    @NotGuest
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/trends")
    public boolean deleteAllTrends() {
        return searchService.deleteAllTrends();
    }

    /**
     * Search
     *
     * @param pageInfo pagination info
     * @param keyword  keyword (accept empty string and null)
     * @return {@link ResultVO}<{@link SearchResultsVO}> Search results
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping("/search")
    public ResultVO<SearchResultsVO> search(@PageInfo PageInfoDTO pageInfo,
                                            @RequestParam("keyword") String keyword) {

        SearchResultsDTO searchResultsDTO = searchService.searchWebsiteData(keyword, pageInfo);
        SearchResultsVO results = DozerUtils.convert(searchResultsDTO, SearchResultsVO.class);

        return ResultCreator.okResult(results);
    }

    /**
     * Website Data generation for Elasticsearch based on database
     *
     * @return success or failure
     */
    @SystemLog(optsType = OptsType.UPDATE)
    @GetMapping("/build")
    public boolean generateSearchDataBasedOnDatabase() {
        return searchService.generateWebsiteDataForSearch();
    }

    /**
     * Check and delete all website data in Elasticsearch
     *
     * @return success or failure.
     */
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/build")
    public boolean deleteWebsiteDataSearch() {
        return searchService.checkAndDeleteIndex(EsConstant.INDEX_WEB);
    }

    /**
     * Initialize Elasticsearch: Check whether the index exists. If not, create the index.
     *
     * @param indexName name of the index
     * @return success or failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link SearchService#hasIndexOrCreate(String)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#CONNECTION_ERROR}
     *                                                                  if there is an error occurred while creating
     *                                                                  the index.
     */
    @SystemLog(optsType = OptsType.CREATE)
    @GetMapping("/createIndex")
    public boolean hasIndexOrCreate(@RequestParam("indexName") String indexName) {
        return searchService.hasIndexOrCreate(indexName);
    }
}
