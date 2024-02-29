package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.config.batch.BookmarkViewBatchConfig;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.vo.BatchJobVO;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Batch Job Controller
 *
 * @author zhou
 * @date 2024/2/23
 */
@RequestMapping("/batch-job")
@RestController
@Slf4j
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job bookmarkViewBatchJob;

    public BatchJobController(JobLauncher jobLauncher,
                              @Qualifier(BookmarkViewBatchConfig.JOB_NAME) Job bookmarkViewBatchJob) {
        this.jobLauncher = jobLauncher;
        this.bookmarkViewBatchJob = bookmarkViewBatchJob;
    }

    @GetMapping("/update-bookmark-view")
    public BatchJobVO updateBookmarkView() {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution execution = jobLauncher.run(bookmarkViewBatchJob, parameters);
            Long jobId = execution.getJobId();

            ExitStatus status = execution.getExitStatus();
            String exitCode = status.getExitCode();
            String exitDescription = status.getExitDescription();
            long instanceId = execution.getJobInstance().getInstanceId();
            JobParameters jobParameters = execution.getJobParameters();
            Map<String, JobParameter> jobParametersMap = jobParameters.getParameters();

            return BatchJobVO.builder()
                    .jobId(jobId)
                    .jobInstanceId(instanceId)
                    .jobParameters(jobParametersMap)
                    .exitCode(exitCode)
                    .exitDescription(exitDescription)
                    .build();

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Batch Job Error", e);
            throw new ServiceException(ResultCode.BATCH_JOB_ERROR);
        }
    }
}