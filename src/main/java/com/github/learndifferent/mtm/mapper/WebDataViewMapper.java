package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.VisitedBookmarksDTO;
import com.github.learndifferent.mtm.entity.ViewDataDO;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * View Counter Mapper
 *
 * @author zhou
 * @date 2022/3/24
 */
@Repository
public interface WebDataViewMapper {

    /**
     * Add all view data to database
     *
     * @param allWebDataViews all data
     */
    void addAll(@Param("set") Set<ViewDataDO> allWebDataViews);

    /**
     * Clear all view data
     *
     * @return success or not
     */
    boolean clearAll();

    /**
     * Get all view data
     *
     * @return all view data
     */
    List<ViewDataDO> getAllViewData();

    /**
     * Get visited bookmarks from database
     *
     * @param from from
     * @param size size
     * @return all visited bookmarks
     */
    List<VisitedBookmarksDTO> getVisitedWebData(@Param("from") int from, @Param("size") int size);

    /**
     * Delete view data by {@code webId}
     *
     * @param webId ID
     * @return true if success
     */
    boolean deleteViewData(int webId);
}