package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.VisitedBookmarksDTO;
import com.github.learndifferent.mtm.entity.WebDataViewDO;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Website Data View Counter Mapper
 *
 * @author zhou
 * @date 2022/3/24
 */
@Repository
public interface WebDataViewMapper {

    /**
     * Add all data to database
     *
     * @param allWebDataViews all data
     */
    void addAll(@Param("set") Set<WebDataViewDO> allWebDataViews);

    /**
     * Clear all data
     *
     * @return success or not
     */
    boolean clearAll();

    /**
     * Get all visited bookmarks from database
     *
     * @return all visited bookmarks
     */
    List<VisitedBookmarksDTO> getAllVisitedWebData();
}