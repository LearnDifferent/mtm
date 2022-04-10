package com.github.learndifferent.mtm.constant.enums;

/**
 * Data access privilege
 *
 * @author zhou
 * @date 2022/4/10
 */
public enum AccessPrivilege {

    /**
     * Access all data
     */
    ALL(true),
    /**
     * Access only public data
     */
    LIMITED(false);

    /**
     * True if private data can be accessed.
     * False if only public data can be accessed.
     */
    private final boolean canAccessPrivateData;

    AccessPrivilege(final boolean canAccessPrivateData) {
        this.canAccessPrivateData = canAccessPrivateData;
    }

    public boolean canAccessPrivateData() {
        return this.canAccessPrivateData;
    }
}