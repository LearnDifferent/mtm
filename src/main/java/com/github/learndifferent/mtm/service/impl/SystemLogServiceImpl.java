package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.manager.AsyncLogManager;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.vo.SysLog;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {

    private final AsyncLogManager asyncLogManager;

    @Autowired
    public SystemLogServiceImpl(AsyncLogManager asyncLogManager) {
        this.asyncLogManager = asyncLogManager;
    }

    @Override
    public List<SysLog> getLogs() {
        return asyncLogManager.getLogs();
    }

    @Override
    public void saveSysLogAsync(SysLog log) {
        asyncLogManager.saveSysLogAsync(log);
    }
}
