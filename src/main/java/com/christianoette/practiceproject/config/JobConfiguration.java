package com.christianoette.practiceproject.config;

import com.christianoette.practiceproject.model.Person;
import com.christianoette.utils.CourseUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
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

import static com.christianoette.practiceproject.config.JobParameterKeys.INPUT_FILE;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class JobConfiguration implements JobParametersValidator {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobConfiguration(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("myJob")
                .start(step())
                .validator(this)
                .listener(new FileHandlingExecutionListener())
                .build();
    }

    @Bean
    public Step step() {
        SimpleStepBuilder<Person, Person> chunk = stepBuilderFactory.get("jsonItemReader")
                .chunk(1);

        return chunk.reader(reader(null))
                .faultTolerant()
                .skipLimit(200)
                .skip(NotACustomerException.class)
                .processor(processor())
                .writer(writer(null))
                .build();
    }

    @Bean
    public ItemProcessor<Person, Person> processor() {
        return item -> {
            if (!item.isCustomer) {
                throw new NotACustomerException();
            }

            Person person = new Person();
            person.birthday = item.birthday;
            person.name = "John Doe";
            person.revenue = item.revenue;
            person.email = "";
            person.isCustomer = true;
            return person;
        };
    }

    @Bean
    @StepScope
    public JsonItemReader<Person> reader(@Value(JobParameterKeys.INPUT_FILE_PARAMETER)
                                                                String inputPath) {

        FileSystemResource inputResource = CourseUtils.getFileResource(inputPath);
        return new JsonItemReaderBuilder<Person>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Person.class))
                .resource(inputResource)
                .name("tradeJsonItemReader")
                .build();
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<Person> writer(@Value("#{jobParameters['outputPath']}")
                                                         String outputPath) {

        FileSystemResource outputResource = CourseUtils.getFileResource(outputPath);
        return new JsonFileItemWriterBuilder<Person>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(outputResource)
                .name("tradeJsonItemReader")
                .build();
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String inputFile = parameters.getString(INPUT_FILE);
        String extension = FilenameUtils.getExtension(inputFile);
        if (extension == null || !extension.toLowerCase().equals("json")) {
            throw new JobParametersInvalidException(
                    "Input file must be in JSON format. ");
        }
    }
}
