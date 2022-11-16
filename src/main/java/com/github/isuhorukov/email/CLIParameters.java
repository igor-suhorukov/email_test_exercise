package com.github.isuhorukov.email;

import com.beust.jcommander.Parameter;

public class CLIParameters {
    @Parameter(
            names = "--source",
            description = "Source file path",
            required = true
    )
    private String sourcePath;
    @Parameter(
            names = "--type",
            description = "Source file type",
            required = true
    )
    private FileType type;
    @Parameter(
            names = "--output",
            description = "Result directory path",
            required = true
    )
    private String outputDir;

    public String getSourcePath() {
        return sourcePath;
    }

    public FileType getType() {
        return type;
    }

    public String getOutputDir() {
        return outputDir;
    }

    void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    void setType(FileType type) {
        this.type = type;
    }

    void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
