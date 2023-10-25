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
     * If the business tag is not present, insert the record and return true.
     * If the business tag already exists in the database, return false (which means 0 row affected).
     *
     * @param bizTag               business tag
     * @param maxId                max ID
     * @param step                 step
     * @param tableName            table name
     * @param primaryKeyColumnName primary key column name
     * @param description          description
     * @return Return true if the business tag is not present and insert successfully.
     * <p>Return false if the business tag already exists in the database,
     * which means the record should not be inserted.</p>
     */
    boolean insertIfNotPresent(@Param("bizTag") String bizTag,
                               @Param("maxId") long maxId,
                               @Param("step") int step,
                               @Param("tableName") String tableName,
                               @Param("primaryKeyColumnName") String primaryKeyColumnName,
                               @Param("description") String description);

    /**
     * Update the max ID or insert a new record if the record does not exist
     *
     * @param bizTag               business tag
     * @param step                 step
     * @param tableName            table name
     * @param primaryKeyColumnName primary key column name
     * @param description          description (allow null)
     */
    void updateMaxIdOrInsertIfNotPresent(@Param("bizTag") String bizTag,
                                         @Param("step") int step,
                                         @Param("tableName") String tableName,
                                         @Param("primaryKeyColumnName") String primaryKeyColumnName,
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




