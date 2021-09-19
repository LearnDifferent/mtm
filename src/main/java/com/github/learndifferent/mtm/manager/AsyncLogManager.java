package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import com.github.learndifferent.mtm.vo.SysLog;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 系统记录相关，存放在 Redis 内
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class AsyncLogManager {

    private final StringRedisTemplate template;

    @Autowired
    public AsyncLogManager(StringRedisTemplate template) {
        this.template = template;
    }

    /**
     * 获取日志
     *
     * @return {@code List<SysLog>}
     */
    public List<SysLog> getLogs() {
        List<SysLog> sysLogs = new ArrayList<>();
        List<String> data = template.opsForList().range(KeyConstant.SYSTEM_LOG, 0, -1);
        if (ReverseUtils.collectionNotEmpty(data)) {
            for (String d : data) {
                sysLogs.add(JsonUtils.toObject(d, SysLog.class));
            }
        }
        return sysLogs;
    }

    /**
     * 异步保存操作记录
     *
     * @param log 操作记录
     */
    @Async
    public void saveSysLogAsync(SysLog log) {
        template.opsForList().leftPush(KeyConstant.SYSTEM_LOG, JsonUtils.toJson(log));
    }
}
