package com.christianoette.batch.dontchangeit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CourseTestDataCreator {

    private static final int LIST_SIZE = 500;
    private static final int NR_OF_FILES = 10;
    private static List<String> FIRST_NAMES = List.of("Abigail", "Emily", "Harper", "Daliah", "Amelia", "Zane", "Evelyn", "Elizabeth", "Sofia", "Madison", "Avery", "Ella", "Scarlett", "Grace", "Chloe", "Ahmet", "Berat", "Yildrim", "Kaya", "Jìng", "Li", "Wei", "Qiang,", "Minh");
    private static List<String> LAST_NAMES = List.of("Singh", "Lee", "Smith", "Khan", "Nguyen", "Shah", "Chen", "Fox", "Gill", "Burke", "Potter", "Carvalho", "Sanchez", "Liu", "Aziz", "Mueller", "Lang", "Wolf", "Small", "Michael", "Knox", "Winters", "Sommers", "Spring");

    @SuppressWarnings("FieldCanBeLocal")
    private static String OUTPUT_DIRECTORY = "public/upload";
    //private static String OUTPUT_DIRECTORY = "src/main/resources/testData";


    public static void main(String[] args) throws IOException {
        for (int i=0; i<NR_OF_FILES; i++) {
            writeTestDataToFile();
        }
    }

    private static void writeTestDataToFile() throws IOException {
        List<TestData> items = new ArrayList<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            items.add(createRandomItem());
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(items);
        String fileName = String.format("persons_%s.json", UUID.randomUUID().toString());

        FileUtils.writeStringToFile(
                new File(FilenameUtils.concat(OUTPUT_DIRECTORY, fileName)),
                jsonInString,
                Charset.defaultCharset());

    }

    private static TestData createRandomItem() {
        TestData testData = new TestData();

        Random random = new Random();
        String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        testData.email = firstName + "." + lastName + "@domain.xyz";
        testData.name = firstName + " " + lastName;
        testData.isCustomer = random.nextInt(10) >= 2;
        BigDecimal randomAge = new BigDecimal(Math.random() * 20 + 18);
        testData.birthday = LocalDate.now().minusYears(randomAge.longValue()).toString();
        if (testData.isCustomer) {
            BigDecimal randomValue = new BigDecimal(Math.random() * Math.random() * 1000);
            testData.revenue = randomValue.setScale(2, RoundingMode.UP);
        }
        return testData;
    }

    @SuppressWarnings("WeakerAccess")
    static class TestData {
        public String name;
        public String birthday;
        public String email;
        public BigDecimal revenue = BigDecimal.ZERO;
        public boolean isCustomer;
    }


}
