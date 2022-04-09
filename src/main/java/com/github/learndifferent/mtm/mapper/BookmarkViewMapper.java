package com.github.learndifferent.mtm.mapper;

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
public interface BookmarkViewMapper {

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
     * Delete view data by {@code webId}
     *
     * @param webId ID
     * @return true if success
     */
    boolean deleteViewData(int webId);
}