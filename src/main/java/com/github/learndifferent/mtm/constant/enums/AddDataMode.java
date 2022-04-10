package com.github.learndifferent.mtm.constant.enums;

/**
 * Add the data to database if {@link #ADD_TO_DATABASE}.
 * Add the data to database and Elasticsearch if {@link #ADD_TO_DATABASE_AND_ELASTICSEARCH}.
 *
 * @author zhou
 * @date 2022/4/10
 */
public enum AddDataMode implements ConvertByNames {
    /**
     * Add the data to database
     */
    ADD_TO_DATABASE(0, "addToDatabase", "add_to_database"),
    /**
     * Add the data to database and Elasticsearch
     */
    ADD_TO_DATABASE_AND_ELASTICSEARCH(1, "addToDatabaseAndElasticsearch", "add_to_database_and_elasticsearch");

    private final int number;
    private final String camelCaseName;
    private final String snakeCaseName;

    AddDataMode(final int number, final String camelCaseName, final String snakeCaseName) {
        this.number = number;
        this.camelCaseName = camelCaseName;
        this.snakeCaseName = snakeCaseName;
    }

    public String camelCaseName() {
        return this.camelCaseName;
    }

    public String snakeCaseName() {
        return this.snakeCaseName;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{String.valueOf(this.number), this.name(), this.camelCaseName, this.snakeCaseName};
    }
}