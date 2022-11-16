package com.github.isuhorukov.email;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FileUtils {

    public FileUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static File getSourceFile(CLIParameters parameters) {
        Objects.requireNonNull(parameters.getSourcePath(), "Expected source file parameter");
        File sourceFile = new File(parameters.getSourcePath());
        if(!sourceFile.exists()){
            throw new IllegalArgumentException("Source file is not exist");
        }
        if(sourceFile.isDirectory()){
            throw new IllegalArgumentException("Source file shouldn't be directory");
        }
        return sourceFile;
    }

    public static File getOutputDirectory(CLIParameters parameters) {
        Objects.requireNonNull(parameters.getOutputDir(), "Expected output directory parameter");
        File outputDirectory = new File(parameters.getOutputDir());
        if(!outputDirectory.exists()){
            throw new IllegalArgumentException("Output directory should exists");
        }
        if(!outputDirectory.isDirectory()){
            throw new IllegalArgumentException("Output path should be directory");
        }
        return outputDirectory;
    }

    public static String getResultFileName(String[] contentTypeParts, AtomicLong fileNameIncremental) {
        Optional<String> name = Arrays.stream(contentTypeParts).filter(s -> s.contains("name=")).findFirst();
        if(name.isPresent()){
            String nameString = name.get();
            String nameParam = "name=\"";
            int nameParamIdx = nameString.indexOf(nameParam);
            if(nameParamIdx!=-1) {
                return nameString.substring(nameParamIdx + nameParam.length(), nameString.length() - 1);
            } else {
                return getSyntheticIncrementalName(fileNameIncremental.getAndIncrement());
            }
        } else {
            return getSyntheticIncrementalName(fileNameIncremental.getAndIncrement());
        }
    }

    public static String getSyntheticIncrementalName(long fileNumber){
        return "filename_collision_"+fileNumber+".eml";
    }
}
