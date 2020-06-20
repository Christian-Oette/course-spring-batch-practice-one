package com.christianoette.batch;

import com.christianoette.batch.practice.FileHandlingJobExecutionListener;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

@Component
public class FileHandlingJobExecutionListenerImpl implements FileHandlingJobExecutionListener {


    @Override
    public void beforeJob(JobExecution jobExecution) {
        // TODO Move uploaded files to processing
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
            // TODO Delete processing file
        } else {
            // TODO Move processing file to error directory
        }
    }
}
