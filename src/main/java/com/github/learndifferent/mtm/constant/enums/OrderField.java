package com.github.learndifferent.mtm.constant.enums;

/**
 * Order by the field
 *
 * @author zhou
 * @date 2022/4/10
 */
public enum OrderField implements ConvertByNames {

    /**
     * order by user_name
     */
    USER_NAME("userName", "user_name"),
    /**
     * order by creation_time
     */
    CREATION_TIME("creationTime", "creation_time");

    private final String camelCaseName;
    private final String snakeCaseName;

    OrderField(String camelCaseName, String snakeCaseName) {
        this.camelCaseName = camelCaseName;
        this.snakeCaseName = snakeCaseName;
    }

    public String getCamelCaseName() {
        return camelCaseName;
    }

    public String getSnakeCaseName() {
        return snakeCaseName;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{this.name(), this.camelCaseName, this.snakeCaseName};
    }
}