package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.config.batch.BookmarkViewBatchConfig;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.BatchJobService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Batch Job Service
 *
 * @author zhou
 * @date 2024/3/4
 */
@Service
@Slf4j
public class BatchJobServiceImpl implements BatchJobService {

    private final JobLauncher jobLauncher;
    private final Job bookmarkViewBatchJob;

    public static final long TWELVE_HOURS = 43_200_000L;

    public BatchJobServiceImpl(JobLauncher jobLauncher,
                               @Qualifier(BookmarkViewBatchConfig.JOB_NAME) Job bookmarkViewBatchJob) {
        this.jobLauncher = jobLauncher;
        this.bookmarkViewBatchJob = bookmarkViewBatchJob;
    }

    @Override
    public BatchJobVO updateBookmarkView() {

        JobParameters parameters = getTimestampJobParameters();

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

    /**
     * A scheduled task to run {@link #updateBookmarkView()} every {@link #TWELVE_HOURS}
     */
    @Scheduled(fixedRate = TWELVE_HOURS)
    public void updateBookmarkViewScheduled() {
        log.info("[BatchJobService] Scheduled Job - updateBookmarkView() is started");
        BatchJobVO result = this.updateBookmarkView();
        log.info("[BatchJobService] Scheduled Job - updateBookmarkView() is finished. Result: {}", result);
    }

    private JobParameters getTimestampJobParameters() {
        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
    }
}