package com.christianoette.batch.practice.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("anonymizeJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return new Step() {
            @Override
            public String getName() {
                return "test-Step";
            }

            @Override
            public boolean isAllowStartIfComplete() {
                return false;
            }

            @Override
            public int getStartLimit() {
                return 0;
            }

            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {

            }
        };
    }
}
