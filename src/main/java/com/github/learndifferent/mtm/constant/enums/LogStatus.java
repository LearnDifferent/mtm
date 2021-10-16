package com.github.learndifferent.mtm.constant.enums;

/**
 * 日志：状态
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum LogStatus {

    /**
     * 正常
     */
    NORMAL("Normal"),
    /**
     * 错误
     */
    ERROR("Error");

    private final String status;

    LogStatus(final String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }
}
