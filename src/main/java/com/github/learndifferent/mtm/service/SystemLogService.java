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
     * Get System Logs.
     * The logs will be cached for 30 seconds.
     *
     * @param pageInfo pagination info
     * @return {@link List}<{@link SysLog}> system logs
     * @see com.github.learndifferent.mtm.config.RedisConfig
     */
    List<SysLog> getSystemLogs(PageInfoDTO pageInfo);

    /**
     * Save System Log Asynchronously
     *
     * @param log system log
     */
    void saveSystemLogAsync(SysLog log);
}
