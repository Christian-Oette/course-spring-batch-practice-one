package com.christianoette.batch.practice.config;

import com.christianoette.batch.dontchangeit.utils.CourseUtils;
import com.christianoette.batch.practice.FileHandlingJobExecutionListener;
import com.christianoette.batch.practice.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;


@Configuration
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job job(FileHandlingJobExecutionListener listener) {
        return jobBuilderFactory.get("anonymizeJob")
                .start(step())
                .listener(listener)
                .validator(new AnonymizeJobParameterValidator())
                .build();
    }

    @Bean
    public Step step() {
        SimpleStepBuilder<Person, Person> simpleStepBuilder = stepBuilderFactory.get("anonymizeStep")
                .chunk(1);
        return simpleStepBuilder.reader(reader(null))
                .processor(processor())
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<Person> reader(
            @Value(AnonymizeJobParameterKeys.INPUT_PATH_REFERENCE) String inputPath) {
        FileSystemResource resource = CourseUtils.getFileResource(inputPath);

        return new JsonItemReaderBuilder<Person>()
                .name("jsonItemReader")
                .resource(resource)
                .jsonObjectReader(new JacksonJsonObjectReader<>(Person.class))
                .build();
    }

    @Bean
    public ItemProcessor<Person, Person> processor() {
        return input -> {
            if (!input.isCustomer) {
                return null;
            }

            Person output = new Person();
            output.birthday = input.birthday;
            output.email = input.email;
            output.isCustomer = input.isCustomer;
            output.name = input.name;
            output.revenue = input.revenue;
            return output;
        };
    }


    @Bean
    @StepScope
    public JsonFileItemWriter<Person> writer(@Value(AnonymizeJobParameterKeys.OUTPUT_PATH_REFERENCE) String outputPath) {
        FileSystemResource resource = CourseUtils.getFileResource(outputPath);

        return new JsonFileItemWriterBuilder<Person>()
                .name("jsonItemWriter")
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(resource)
                .build();
    }
}
