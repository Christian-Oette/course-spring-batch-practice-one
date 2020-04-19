package com.christianoette.practiceproject.config;

import com.christianoette.practiceproject.JobserverFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;

import java.io.File;

public class FileHandlingExecutionListener implements JobExecutionListener {

    private static final Logger LOGGER =
            LogManager.getLogger(FileHandlingExecutionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String inputFile = jobParameters.getString(JobParameterKeys.INPUT_FILE);
        String errorDirectory = jobParameters.getString(JobParameterKeys.ERROR_DIRECTORY);

        BatchStatus status = jobExecution.getStatus();
        if (status.equals(BatchStatus.COMPLETED)) {
            LOGGER.info("Batch job completed. Delete input file.");
            JobserverFileUtils.deleteFile(inputFile);
        }
        else {
            LOGGER.info("Batch job failed. Move input file to error directory.");
            JobserverFileUtils.moveFileToDirectory(new File(inputFile), errorDirectory);
        }
    }
}
