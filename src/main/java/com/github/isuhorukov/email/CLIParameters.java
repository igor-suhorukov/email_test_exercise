package com.github.isuhorukov.email;

import com.beust.jcommander.Parameter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CLIParameters {
    @Parameter(
            names = "--source",
            description = "Source file path",
            required = true
    )
    private String sourcePath;

    @Parameter(
            names = "--output",
            description = "Result directory path",
            required = true
    )
    private String outputDir;

    @Parameter(
            names = "--opsequence",
            description = "Sequence of operations",
            required = true
    )
    private List<String> opSequence;

    public String getSourcePath() {
        return sourcePath;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public Set<String> getOpSequence() {
        return new HashSet<>(opSequence);
    }

    void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    void setOpsequence(List<String> opSequence) {
        this.opSequence = opSequence;
    }
}
