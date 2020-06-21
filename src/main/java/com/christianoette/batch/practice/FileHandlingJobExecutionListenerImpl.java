package com.christianoette.batch.practice;

import com.christianoette.batch.dontchangeit.utils.CourseUtils;
import com.christianoette.batch.practice.FileHandlingJobExecutionListener;
import com.christianoette.batch.practice.config.AnonymizeJobParameterKeys;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileHandlingJobExecutionListenerImpl implements FileHandlingJobExecutionListener {


    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String uploadedFile = jobParameters.getString(AnonymizeJobParameterKeys.UPLOAD_PATH);
        String inputFile = jobParameters.getString(AnonymizeJobParameterKeys.INPUT_PATH);
        CourseUtils.moveFileToDirectory(new File(uploadedFile), new File(inputFile).getParent());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobParameters jobParameters = jobExecution.getJobParameters();
        String inputFile = jobParameters.getString(AnonymizeJobParameterKeys.INPUT_PATH);

        if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
            CourseUtils.deleteFile(inputFile);
        } else {
            String errorFile = jobParameters.getString(AnonymizeJobParameterKeys.ERROR_PATH);
            CourseUtils.moveFileToDirectory(new File(inputFile), new File(errorFile).getParent());
        }
    }
}
