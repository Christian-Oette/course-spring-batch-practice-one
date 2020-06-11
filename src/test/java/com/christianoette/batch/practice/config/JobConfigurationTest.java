package com.christianoette.batch.practice.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

@SpringBootTest(classes = {JobConfigurationTest.TestConfig.class,
JobConfiguration.class
})
class JobConfigurationTest {

    @Autowired
    private Job job;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void happyCaseTest() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputPath", "classpath:unitTestData/persons.json")
                .addString("outputPath", "output/unitTestOutput.json")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        String outputContent = contentOf(new File("output/unitTestOutput.json"));
        assertThat(outputContent).contains("Wei Lang");
        assertThat(outputContent).doesNotContain("Daliah Shah");
    }

    @Configuration
    @EnableBatchProcessing
    static class TestConfig {
        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }
}
