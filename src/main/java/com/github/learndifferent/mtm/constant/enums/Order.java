package com.github.learndifferent.mtm.constant.enums;

/**
 * {@link Order#ASC} if ascending order, {@link Order#DESC} if descending order
 *
 * @author zhou
 * @date 2022/4/10
 */
public enum Order implements ConvertByNames {

    /**
     * Ascending order
     */
    ASC("asc", false),
    /**
     * Descending order
     */
    DESC("desc", true);

    private final String order;
    private final boolean isDesc;

    Order(final String order, final boolean isDesc) {
        this.order = order;
        this.isDesc = isDesc;
    }

    public String getOrder() {
        return order;
    }

    public boolean isDesc() {
        return isDesc;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{this.order, this.name(), String.valueOf(this.isDesc)};
    }
}