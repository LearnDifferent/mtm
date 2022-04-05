package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.SearchService;
import com.github.learndifferent.mtm.vo.FindPageVO;
import com.github.learndifferent.mtm.vo.SearchDataStatusVO;
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
     * Get trending searches, existent of bookmark data for search and the update information.
     *
     * @return {@link FindPageVO} trending searches, existent of bookmark data for search and update information
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping("/load")
    public FindPageVO load() {

        // trending searches
        Set<String> trendingList = searchService.getTrends();
        // existent of bookmark data for search
        boolean exist = searchService.existsData(SearchMode.WEB);
        // update information
        boolean hasNewUpdate = searchService.dataInDatabaseDiffFromElasticsearch(SearchMode.WEB, exist);

        return FindPageVO.builder()
                .trendingList(trendingList)
                .dataStatus(exist)
                .hasNewUpdate(hasNewUpdate)
                .build();
    }

    /**
     * Delete specific trending keyword (Guest does not have the permission)
     *
     * @param word keyword to delete
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotGuest} will throw exception if the
     *                                                                  user is a guest with the result code of {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
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
     * @return true if success
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
     * @param mode     Search users if the search mode is {@link SearchMode#USER},
     *                 search bookmarks if the search mode is {@link SearchMode#WEB}
     *                 and search tags if the search mode is {@link SearchMode#TAG}
     * @param keyword  keyword (accept empty string and null)
     * @param pageInfo pagination information
     * @return {@link ResultVO}<{@link SearchResultsDTO}> Search results
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} will be thrown if there are no results that
     *                                                                  match the keyword
     */
    @GetMapping("/search")
    public ResultVO<SearchResultsDTO> search(@RequestParam("mode") SearchMode mode,
                                             @RequestParam("keyword") String keyword,
                                             @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 10)
                                                     PageInfoDTO pageInfo) {

        SearchResultsDTO results = searchService.search(mode, keyword, pageInfo);
        return ResultCreator.okResult(results);
    }

    /**
     * Check the existent and changes of data
     *
     * @param mode Check user data if {@link SearchMode#USER},
     *             bookmark data if {@link SearchMode#WEB}
     *             and tag data if {@link SearchMode#TAG}
     * @return data status
     */
    @GetMapping("/status")
    public SearchDataStatusVO checkDataStatus(@RequestParam("mode") SearchMode mode) {
        boolean exists = searchService.existsData(mode);
        boolean hasChanges = searchService.dataInDatabaseDiffFromElasticsearch(mode, exists);
        return SearchDataStatusVO.builder().exists(exists).hasChanges(hasChanges).build();
    }

    /**
     * Data generation for Elasticsearch based on database
     *
     * @param mode Generate user data if {@link SearchMode#USER},
     *             generate tag data if {@link SearchMode#TAG}
     *             and generate bookmark data if {@link SearchMode#WEB}.
     * @return true if success
     */
    @GetMapping("/build")
    public boolean generateSearchDataBasedOnDatabase(@RequestParam("mode") SearchMode mode) {
        return searchService.generateDataForSearch(mode);
    }

    /**
     * Check and delete data in Elasticsearch
     *
     * @param mode delete user data if {@link SearchMode#USER},
     *             bookmark data if {@link SearchMode#WEB}
     *             and tag data if {@link SearchMode#TAG}
     * @return true if deleted
     * @throws com.github.learndifferent.mtm.exception.ServiceException Only admin can delete all website data, and
     *                                                                  if the current user is not admin,
     *                                                                  {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     */
    @DeleteMapping("/delete")
    @AdminValidation
    public boolean deleteDataForSearch(@RequestParam("mode") SearchMode mode) {
        return searchService.checkAndDeleteIndex(mode);
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