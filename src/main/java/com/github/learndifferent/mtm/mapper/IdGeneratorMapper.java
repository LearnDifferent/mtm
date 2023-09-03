package com.github.learndifferent.mtm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * ID Generator mapper
 *
 * @author zhou
 * @date 2023/9/2
 */
@Repository
public interface IdGeneratorMapper {

    /**
     * Update the max ID or insert a new record if the record does not exist
     *
     * @param bizTag      business tag
     * @param step        step
     * @param description description (allow null)
     */
    void updateMaxIdOrInsertIfNotPresent(@Param("bizTag") String bizTag,
                                         @Param("step") int step,
                                         @Param("description") String description);

    /**
     * Get the max ID
     *
     * @param bizTag business tag
     * @return max ID or null if there is no record
     */
    Long getMaxId(@Param("bizTag") String bizTag);

    /**
     * Get all business tags
     *
     * @return all business tags
     */
    List<String> getAllBizTags();
}




