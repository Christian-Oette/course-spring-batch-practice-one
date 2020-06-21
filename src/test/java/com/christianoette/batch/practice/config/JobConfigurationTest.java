package com.christianoette.batch.practice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {JobConfigurationTest.TestConfig.class})
class JobConfigurationTest {

    @Test
    void happyCaseTest() {

    }

    @Configuration
    static class TestConfig {

    }
}
