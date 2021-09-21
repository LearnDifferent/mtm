package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.vo.SysLog;
import java.util.List;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface SystemLogService {

    /**
     * Get System Logs
     *
     * @return {@link List}<{@link SysLog}>
     */
    List<SysLog> getLogs();

    /**
     * Save System Log Asynchronously
     *
     * @param log log
     */
    void saveSysLogAsync(SysLog log);
}
