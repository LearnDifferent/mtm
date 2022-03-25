package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.entity.WebDataViewDO;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
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
}