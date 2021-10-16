package com.github.learndifferent.mtm.constant.enums;

/**
 * 角色类型
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum RoleType {

    /**
     * user
     */
    USER("user"),
    /**
     * guest
     */
    GUEST("guest"),
    /**
     * admin
     */
    ADMIN("admin");

    private final String role;

    RoleType(final String role) {
        this.role = role;
    }

    public String role() {
        return this.role;
    }
}
