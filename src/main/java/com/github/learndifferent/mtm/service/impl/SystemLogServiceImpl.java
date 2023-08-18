package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.mapper.SystemLogMapper;
import com.github.learndifferent.mtm.service.SystemLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogMapper systemLogMapper;

    @Override
    @Cacheable(value = "system:log")
    public List<SysLog> getSystemLogs(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        return systemLogMapper.getSystemLogs(from, size);
    }

    @Override
    @CachePut(value = "system:log")
    public List<SysLog> getSystemLogsFromDatabaseDirectly(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        return systemLogMapper.getSystemLogs(from, size);
    }

    @Async("asyncTaskExecutor")
    @Override
    public void saveSystemLogAsync(SysLog log) {
        systemLogMapper.saveSystemLog(log);
    }
}