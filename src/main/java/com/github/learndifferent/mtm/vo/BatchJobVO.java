package com.github.learndifferent.mtm.vo;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.JobParameter;

/**
 * Batch Job result
 *
 * @author zhou
 * @date 2024/2/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchJobVO {

    /**
     * Job ID
     */
    private Long jobId;

    /**
     * Instance ID
     */
    private Long jobInstanceId;

    /**
     * Job Parameters
     */
    private Map<String, JobParameter> jobParameters;

    /**
     * Exist Code
     */
    private String exitCode;

    /**
     * Exit Description
     */
    private String exitDescription;
}