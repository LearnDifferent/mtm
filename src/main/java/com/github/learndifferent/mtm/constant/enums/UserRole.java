package com.github.learndifferent.mtm.constant.enums;

/**
 * User role
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum UserRole implements ConvertByNames {

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

    UserRole(final String role) {
        this.role = role;
    }

    public String role() {
        return this.role;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{this.role, this.name()};
    }
}