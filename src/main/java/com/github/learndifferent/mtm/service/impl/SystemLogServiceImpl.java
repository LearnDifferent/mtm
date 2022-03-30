package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.mapper.SystemLogMapper;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogMapper systemLogMapper;

    @Autowired
    public SystemLogServiceImpl(SystemLogMapper systemLogMapper) {
        this.systemLogMapper = systemLogMapper;
    }

    @Override
    public List<SysLog> getSystemLogs(PageInfoDTO pageInfo, Boolean isReadFromDb) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        SystemLogServiceImpl systemLogService =
                ApplicationContextUtils.getBean(SystemLogServiceImpl.class);

        if (Boolean.TRUE.equals(isReadFromDb)) {
            return systemLogService.getSystemLogsFromDatabaseDirectly(from, size);
        }

        return systemLogService.getSystemLogs(from, size);
    }

    @Cacheable(value = "system-log")
    public List<SysLog> getSystemLogs(int from, int size) {
        return systemLogMapper.getSystemLogs(from, size);
    }

    @CachePut(value = "system-log")
    public List<SysLog> getSystemLogsFromDatabaseDirectly(int from, int size) {
        return systemLogMapper.getSystemLogs(from, size);
    }

    @Async("asyncTaskExecutor")
    @Override
    public void saveSystemLogAsync(SysLog log) {
        systemLogMapper.saveSystemLog(log);
    }
}
