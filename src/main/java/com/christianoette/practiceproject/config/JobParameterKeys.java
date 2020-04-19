package com.christianoette.practiceproject.config;

public abstract class JobParameterKeys {

    public final static String INPUT_FILE = "inputFile";
    public final static String INPUT_FILE_PARAMETER = "#{jobParameters['"+INPUT_FILE+"']}";
    public static final String ERROR_DIRECTORY = "errorDirectory";
}
