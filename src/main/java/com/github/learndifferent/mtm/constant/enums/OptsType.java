package com.github.learndifferent.mtm.constant.enums;

/**
 * 日志相关操作
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum OptsType {
    /**
     * 创建
     */
    CREATE("Create"),
    /**
     * 读
     */
    READ("Read"),
    /**
     * 更新
     */
    UPDATE("Update"),
    /**
     * 删除
     */
    DELETE("Delete"),
    /**
     * 其他
     */
    OTHERS("Others");

    private final String value;

    OptsType(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
