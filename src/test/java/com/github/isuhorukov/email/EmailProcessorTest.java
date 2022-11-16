package com.github.isuhorukov.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailProcessorTest {

    @Test
    void testMimeEmail1(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.processMimeMessage(new File(requireNonNull(EmailProcessor.class.
                getResource("/input/Email1.eml")).getFile()), tempDir);
        File[] files = tempDir.listFiles();
        assertThat(files.length).isEqualTo(1);
        assertThat(files[0]).hasSameTextualContentAs(new File(requireNonNull(EmailProcessorTest.class.
                getResource("/output/test2.eml")).getFile()));
    }

    @Test
    void testMimeEmail2(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.processMimeMessage(new File(requireNonNull(EmailProcessor.class.
                getResource("/input/Email2.eml")).getFile()), tempDir);
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
                getResource("/input/EmailDuplicate.eml")).getFile()), tempDir);
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
                getResource("/input/archive.zip")).getFile()), tempDir);
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
        parameters.setType(FileType.ZIP);

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
    void testProcessorNonValidTypeEml(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        CLIParameters parameters = new CLIParameters();
        parameters.setSourcePath(requireNonNull(EmailProcessor.class.
                                    getResource("/input/archive.zip")).getFile());
        parameters.setOutputDir(tempDir.getAbsolutePath());
        parameters.setType(FileType.EML);

        assertThrows(IllegalArgumentException.class, ()->emailProcessor.process(parameters));

    }

    @Test
    void testProcessorNonValidTypeZip(@TempDir File tempDir) throws Exception {
        EmailProcessor emailProcessor = new EmailProcessor();
        CLIParameters parameters = new CLIParameters();
        parameters.setSourcePath(requireNonNull(EmailProcessor.class.
                                    getResource("/input/Email1.eml")).getFile());
        parameters.setOutputDir(tempDir.getAbsolutePath());
        parameters.setType(FileType.ZIP);

        assertThrows(IllegalArgumentException.class, ()->emailProcessor.process(parameters));
    }

    @Test
    void testProcessorCliRun(@TempDir File tempDir) throws Exception {
        EmailProcessor.main(new String[]{"--source",requireNonNull(EmailProcessor.class.
                getResource("/input/archive.zip")).getFile(), "--output",tempDir.getAbsolutePath(), "--type", FileType.ZIP.toString()});

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
}