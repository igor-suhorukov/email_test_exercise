package com.github.isuhorukov.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailProcessorTest {

    @Test
    void testMimeEmail1(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.processMimeMessage(new File(requireNonNull(EmailProcessor.class.
                getResource("/input/Email1.eml")).getFile()), tempDir, new ArrayList<>(),
                new HashSet<>(prepareOpSequenceList("1/unzip/0/1")), "Email1.eml");
        File[] files = tempDir.listFiles();
        assertThat(files.length).isEqualTo(1);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
    }

    @Test
    void testMimeEmail2(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.processMimeMessage(new File(requireNonNull(EmailProcessor.class.
                getResource("/input/Email2.eml")).getFile()), tempDir, new ArrayList<>(),
                new HashSet<>(prepareOpSequenceList("1/unzip/0/1,1/unzip/1/1")),
                "Email2.eml");
        File[] files = tempDir.listFiles();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(2);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test1.eml")).getFile()));
        assertThat(files[1]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test3.eml")).getFile()));
    }

    @Test
    void testNameCollisionSaveAll(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.processMimeMessage(new File(requireNonNull(EmailProcessor.class.
                getResource("/input/EmailDuplicate.eml")).getFile()), tempDir, new ArrayList<>(),
                new HashSet<>(prepareOpSequenceList("0/unzip/0/1,1/unzip/0/1,2/unzip/0/1")),
                "EmailDuplicate.eml");
        File[] files = tempDir.listFiles();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(3);
        assertThat(files[0].getName()).isEqualTo("Test 2.eml");
        assertThat(files[1].getName()).isEqualTo("filename_collision_0.eml");
        assertThat(files[2].getName()).isEqualTo("filename_collision_1.eml");
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
        assertThat(files[1]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
    }

    @Test
    void testZipFile(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.processZipArchive(new File(requireNonNull(EmailProcessor.class.
                getResource("/input/archive.zip")).getFile()), tempDir, new ArrayList<>(),
                new HashSet<>(prepareOpSequenceList(
                        "unzip/0/1/unzip/0/1,unzip/1/1/unzip/0/1,unzip/1/1/unzip/1/1")));
        File[] files = tempDir.listFiles();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(3);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test1.eml")).getFile()));
        assertThat(files[1]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
        assertThat(files[2]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test3.eml")).getFile()));
    }

    @Test
    void testProcessor(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        CLIParameters parameters = new CLIParameters();
        parameters.setSourcePath(requireNonNull(EmailProcessor.class.
                                    getResource("/input/archive.zip")).getFile());
        parameters.setOutputDir(tempDir.getAbsolutePath());
        parameters.setOpsequence(prepareOpSequenceList(
                "unzip/0/1/unzip/0/1,unzip/1/1/unzip/0/1,unzip/1/1/unzip/1/1"));

        emailProcessor.process(parameters);

        File[] files = tempDir.listFiles();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(3);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test1.eml")).getFile()));
        assertThat(files[1]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
        assertThat(files[2]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test3.eml")).getFile()));
    }

    @Test
    void testProcessorSaveEml1Also(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        CLIParameters parameters = new CLIParameters();
        parameters.setSourcePath(requireNonNull(EmailProcessor.class.
                getResource("/input/archive.zip")).getFile());
        parameters.setOutputDir(tempDir.getAbsolutePath());
        parameters.setOpsequence(prepareOpSequenceList(
                "unzip/1,unzip/0/1/unzip/0/1,unzip/1/1/unzip/0/1,unzip/1/1/unzip/1/1"));

        emailProcessor.process(parameters);

        File[] files = tempDir.listFiles();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(4);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/input/Email2.eml")).getFile()));
        assertThat(files[1]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test1.eml")).getFile()));
        assertThat(files[2]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
        assertThat(files[3]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test3.eml")).getFile()));
    }

    @Test
    void testNonFoundOperationProcessor(@TempDir File tempDir) {
        EmailProcessor emailProcessor = new EmailProcessor();
        CLIParameters parameters = new CLIParameters();
        parameters.setSourcePath(requireNonNull(EmailProcessor.class.
                getResource("/input/archive.zip")).getFile());
        parameters.setOutputDir(tempDir.getAbsolutePath());
        parameters.setOpsequence(prepareOpSequenceList(
                "unzip/124,unzip/0/1/unzip/0/1,unzip/1/1/unzip/0/1,unzip/1/1/unzip/1/1"));

        assertThrows(RuntimeException.class, ()->emailProcessor.process(parameters));
    }

    @Test
    void testProcessorCliRun(@TempDir File tempDir) throws Exception {
        EmailProcessor.main(new String[]{"--source",requireNonNull(EmailProcessor.class.
                getResource("/input/archive.zip")).getFile(), "--output",tempDir.getAbsolutePath(),
                "--opsequence", "unzip/0/1/unzip/0/1,unzip/1/1/unzip/0/1,unzip/1/1/unzip/1/1"});

        File[] files = tempDir.listFiles();
        Arrays.sort(files);
        assertThat(files.length).isEqualTo(3);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test1.eml")).getFile()));
        assertThat(files[1]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
        assertThat(files[2]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test3.eml")).getFile()));
    }

    private static List<String> prepareOpSequenceList(String opSequenceString) {
        return Arrays.asList(opSequenceString.split(","));
    }
}