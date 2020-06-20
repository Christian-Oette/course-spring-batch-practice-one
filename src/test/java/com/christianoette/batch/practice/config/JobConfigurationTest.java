package com.christianoette.batch.practice.config;

import com.christianoette.batch.practice.FileHandlingJobExecutionListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.contentOf;

@SpringBootTest(classes = {JobConfigurationTest.TestConfig.class,
JobConfiguration.class
})
class JobConfigurationTest {

    @Autowired
    private Job job;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private FileHandlingJobExecutionListener fileHandlingJobExecutionListener;

    @Test
    void happyCaseTest() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(AnonymizeJobParameterKeys.INPUT_PATH, "classpath:unitTestData/persons.json")
                .addString(AnonymizeJobParameterKeys.OUTPUT_PATH, "output/unitTestOutput.json")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        String outputContent = contentOf(new File("output/unitTestOutput.json"));
        assertThat(outputContent).contains("Wei Lang");
        assertThat(outputContent).doesNotContain("Daliah Shah");
        Mockito.verify(fileHandlingJobExecutionListener).beforeJob(jobExecution);
        Mockito.verify(fileHandlingJobExecutionListener).afterJob(jobExecution);
    }

    @Test
    void testInvalidParametersThrowException() throws Exception {

        assertThatThrownBy(
                () -> jobLauncherTestUtils.launchJob(new JobParameters())
        ).isInstanceOf(JobParametersInvalidException.class)
        .hasMessageContaining("The JobParameters do not contain required keys");
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
