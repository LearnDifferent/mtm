package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * The result of saving website data
 *
 * @author zhou
 * @date 2021/10/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SaveWebDataResultDTO implements Serializable {

    /**
     * True if the data was successfully saved to Database
     */
    private Boolean hasSavedToDatabase;

    /**
     * True if Elasticsearch saved the data successfully.
     * Null if the data does not need to be saved to Elasticsearch.
     */
    private Boolean hasSavedToElasticsearch;

    private static final long serialVersionUID = 1L;
}
