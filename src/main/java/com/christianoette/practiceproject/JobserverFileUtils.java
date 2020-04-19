package com.christianoette.practiceproject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class JobserverFileUtils {

    public static String getFilenameForCopy(File file, String newDirectory) {
        return FilenameUtils.concat(newDirectory, file.getName());
    }

    public static File moveFileToDirectory(File file, String directory) {
        String copyFilePath = JobserverFileUtils.getFilenameForCopy(file, directory);
        File processingFile = new File(copyFilePath);
        try {
            FileUtils.moveFile(file, processingFile);
            return processingFile;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void deleteFile(String inputFile) {
        FileUtils.deleteQuietly(new File(inputFile));
    }
}
