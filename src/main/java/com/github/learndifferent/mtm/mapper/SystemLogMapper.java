package com.github.learndifferent.mtm.mapper;


import com.github.learndifferent.mtm.entity.SysLog;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * SystemLogMapper
 *
 * @author zhou
 * @date 2021/10/13
 */
@Repository
public interface SystemLogMapper {

    /**
     * Save system log
     *
     * @param log system log
     */
    void saveSystemLog(SysLog log);

    /**
     * Get system logs
     *
     * @return {@link List}<{@link SysLog}> system logs
     */
    List<SysLog> getSystemLogs();
}