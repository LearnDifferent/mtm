package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import java.util.List;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface SystemLogService {

    /**
     * Get system logs from cache and database.
     * <p>
     * The result will be stored in cache.
     * </p>
     *
     * @param pageInfo Pagination information
     * @return {@link List}<{@link SysLog}> system logs
     */
    List<SysLog> getSystemLogs(PageInfoDTO pageInfo);

    /**
     * Get system logs from database directly.
     * <p>
     * The result will be put in cache.
     * </p>
     *
     * @param pageInfo Pagination information
     * @return {@link List}<{@link SysLog}> system logs
     */
    List<SysLog> getSystemLogsFromDatabaseDirectly(PageInfoDTO pageInfo);

    /**
     * Save System Log Asynchronously
     *
     * @param log system log
     */
    void saveSystemLogAsync(SysLog log);
}