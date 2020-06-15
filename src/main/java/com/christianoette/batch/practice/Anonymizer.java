package com.christianoette.batch.practice;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class Anonymizer {

    private final Job job;
    private final JobLauncher jobLauncher;

    public Anonymizer(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    public void runAnonymizationJob() throws Exception {
        jobLauncher.run(job, new JobParameters());
    }

    public void runAnonymizationJob(File file) {

    }
}
