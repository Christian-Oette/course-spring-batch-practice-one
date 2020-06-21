package com.christianoette.batch.practice;

import com.christianoette.batch.dontchangeit.utils.CourseUtils;
import com.christianoette.batch.practice.config.AnonymizeJobParameterKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class Anonymizer {

    private final Logger LOGGER = LoggerFactory.getLogger(Anonymizer.class);

    private final Job job;
    private final JobLauncher jobLauncher;

    public Anonymizer(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    public void runAnonymizationJob(File uploadedFile) {
        String uploadedFilePath = uploadedFile.getAbsolutePath();

        String completedDirectory = CourseUtils.getWorkDirSubDirectory("public/completed");
        String errorDirectory = CourseUtils.getWorkDirSubDirectory("public/error");
        String processingDirectory = CourseUtils.getWorkDirSubDirectory("private/processing");

        String outputPath = CourseUtils.getFilePathForDifferentDirectory(uploadedFile, completedDirectory);
        String errorPath = CourseUtils.getFilePathForDifferentDirectory(uploadedFile, errorDirectory);
        String processingPath = CourseUtils.getFilePathForDifferentDirectory(uploadedFile, processingDirectory);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(AnonymizeJobParameterKeys.INPUT_PATH, processingPath)
                .addString(AnonymizeJobParameterKeys.OUTPUT_PATH, outputPath)
                .addString(AnonymizeJobParameterKeys.ERROR_PATH, errorPath)
                .addString(AnonymizeJobParameterKeys.UPLOAD_PATH, uploadedFilePath)
                .addString(AnonymizeJobParameterKeys.ANONYMIZE_DATA, "true")
                .toJobParameters();

        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            LOGGER.error("Job is already running", e);
        } catch (JobRestartException e) {
            LOGGER.error("Job can not be restarted", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            LOGGER.warn("Job is already completed", e);
        } catch (JobParametersInvalidException e) {
            LOGGER.error("Job parameters invalid", e);
        }
    }
}
