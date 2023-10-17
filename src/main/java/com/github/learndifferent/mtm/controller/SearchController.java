package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
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
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/search")
@Validated
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search
     *
     * @param mode      Search for users in Elasticsearch if the search mode is {@link SearchMode#USER},
     *                  search for bookmarked websites in Elasticsearch if the search mode is {@link SearchMode#WEB},
     *                  search for tags in Elasticsearch if the search mode is {@link SearchMode#TAG},
     *                  search for bookmarks in MySQL if the search mode is {@link SearchMode#BOOKMARK_MYSQL},
     *                  search for tags in MySQL if the search mode is {@link SearchMode#TAG_MYSQL},
     *                  and search for users in MySQL if the search mode is {@link SearchMode#USER_MYSQL}.
     * @param keyword   keyword
     * @param pageInfo  pagination information
     * @param rangeFrom lower range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
     *                  unbounded.
     * @param rangeTo   upper range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
     *                  unbounded.
     * @return {@link ResultVO}<{@link SearchResultsDTO}> Search results
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} will be thrown if there are
     *                                                                  no results that match the keyword
     */
    @GetMapping
    public ResultVO<SearchResultsDTO> search(@RequestParam("mode") SearchMode mode,
                                             @RequestParam("keyword")
                                             @NotBlank(message = ErrorInfoConstant.NO_DATA)
                                                     String keyword,
                                             @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 10)
                                                     PageInfoDTO pageInfo,
                                             @RequestParam(required = false, value = "rangeFrom")
                                                     Integer rangeFrom,
                                             @RequestParam(required = false, value = "rangeTo")
                                                     Integer rangeTo) {

        SearchResultsDTO results = searchService.search(mode, keyword, pageInfo, rangeFrom, rangeTo);
        return ResultCreator.okResult(results);
    }

    /**
     * Check and delete data in Elasticsearch
     *
     * @param mode delete user data if {@link SearchMode#USER},
     *             bookmark data if {@link SearchMode#WEB}
     *             and tag data if {@link SearchMode#TAG}
     * @return true if deleted
     * @throws com.github.learndifferent.mtm.exception.ServiceException Only admin can delete all website data, and
     *                                                                  if the current user is not admin.
     *                                                                  This will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     */
    @DeleteMapping
    @AccessPermissionCheck(dataAccessType = DataAccessType.IS_ADMIN)
    @IdempotencyCheck
    public ResultVO<Boolean> deleteDataForSearch(@RequestParam("mode") SearchMode mode) {
        boolean isDeleted = searchService.checkAndDeleteIndexInElasticsearch(mode);
        return ResultCreator.okResult(isDeleted);
    }

    /**
     * Get trending searches, existent of bookmark data for search and the update information.
     *
     * @return {@link FindPageVO} trending searches, existent of bookmark data for search and update information
     * @throws com.github.learndifferent.mtm.exception.ServiceException in case unable to connect to Elasticsearch
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping("/load")
    public FindPageVO load() {

        // trending searches
        Set<String> trendingList = searchService.getTop20Trending();
        // existent of bookmark data for search
        boolean hasDataInElasticsearch = searchService.verifyDataExistenceInElasticsearch(SearchMode.WEB);
        // update information
        boolean hasNewUpdate = searchService.checkDatabaseElasticsearchDataDifference(SearchMode.WEB,
                hasDataInElasticsearch);

        return FindPageVO.builder()
                .trendingList(trendingList)
                .dataStatus(hasDataInElasticsearch)
                .hasNewUpdate(hasNewUpdate)
                .build();
    }

    /**
     * Check the existent and changes of data
     *
     * @param mode Check user data if {@link SearchMode#USER},
     *             bookmark data if {@link SearchMode#WEB}
     *             and tag data if {@link SearchMode#TAG}
     * @return data status
     * @throws com.github.learndifferent.mtm.exception.ServiceException in case unable to connect to Elasticsearch
     */
    @GetMapping("/status")
    public SearchDataStatusVO checkDataStatus(@RequestParam("mode") SearchMode mode) {
        boolean hasDataInElasticsearch = searchService.verifyDataExistenceInElasticsearch(mode);
        boolean hasChanges = searchService.checkDatabaseElasticsearchDataDifference(mode, hasDataInElasticsearch);
        return SearchDataStatusVO.builder().exists(hasDataInElasticsearch).hasChanges(hasChanges).build();
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
    @IdempotencyCheck
    public ResultVO<Boolean> generateDataForElasticsearchBasedOnDatabase(@RequestParam("mode") SearchMode mode) {
        boolean success = searchService.generateDataForElasticsearchBasedOnDatabase(mode);
        return ResultCreator.okResult(success);
    }

    /**
     * Delete a specific trending keyword (Guest does not have the permission)
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
    @DeleteMapping("/trending/{word}")
    @IdempotencyCheck
    public ResultVO<Boolean> deleteTrendingWord(@PathVariable("word") String word) {
        boolean success = searchService.deleteTrendingWord(word);
        return ResultCreator.okResult(success);
    }

    /**
     * Delete all trending keywords (Guest does not have the permission)
     *
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotGuest} will throw exception if the
     *                                                                  user is a guest with the result code of {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     */
    @NotGuest
    @SystemLog(optsType = OptsType.DELETE)
    @DeleteMapping("/trending")
    @IdempotencyCheck
    public ResultVO<Boolean> deleteTrending() {
        boolean success = searchService.deleteTrending();
        return ResultCreator.okResult(success);
    }
}