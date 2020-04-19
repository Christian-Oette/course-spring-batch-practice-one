package com.christianoette.practiceproject;

import com.christianoette.practiceproject.config.JobParameterKeys;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("FieldCanBeLocal")
@Service
public class Anonymizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Anonymizer.class);
    private final String PROCESSING_DIRECTORY = "C:\\development\\course-spring-batch-practice-one\\private\\processing";
    private final String ERROR_DIRECTORY = "C:\\development\\course-spring-batch-practice-one\\public\\error";
    private final String OUTPUT_DIRECTORY = "C:\\development\\course-spring-batch-practice-one\\public\\completed";
    private final JobLauncher jobLauncher;
    private final Job job;

    public Anonymizer(JobLauncher jobLauncher, Job anonymizationJob) {
        this.jobLauncher = jobLauncher;
        this.job = anonymizationJob;
    }

    public void runAnonymizationJob(File file) {
        File processingFile = JobserverFileUtils.moveFileToDirectory(file, PROCESSING_DIRECTORY);
        String outputFile = FilenameUtils.getBaseName(file.getAbsolutePath()) + "_anonymized.json";
        String outputPath = FilenameUtils.concat(OUTPUT_DIRECTORY, outputFile);

        JobParameters jobParameters = new JobParametersBuilder()
                .addParameter(JobParameterKeys.INPUT_FILE, new JobParameter(processingFile.getAbsolutePath()))
                .addParameter("outputPath", new JobParameter(outputPath))
                .addParameter(JobParameterKeys.ERROR_DIRECTORY, new JobParameter(ERROR_DIRECTORY))
                .toJobParameters();

        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            LOGGER.error("Job already running.", e);
        } catch (JobRestartException e) {
            LOGGER.error("Job restart failed.", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            LOGGER.warn("Already completed.", e);
        } catch (JobParametersInvalidException e) {
            LOGGER.error("Job parameters invalid.", e);
            JobserverFileUtils.moveFileToDirectory(processingFile, ERROR_DIRECTORY);
        }
    }


}
