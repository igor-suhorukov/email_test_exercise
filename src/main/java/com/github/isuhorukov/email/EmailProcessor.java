package com.github.isuhorukov.email;

import com.beust.jcommander.JCommander;
import org.apache.commons.io.IOUtils;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * My email attachment example for IoT system https://github.com/igor-suhorukov/alarm-system
 * also referenced from https://camel.apache.org/community/articles/
 */
public class EmailProcessor {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    public static final String APPLICATION_X_ZIP_COMPRESSED = "application/x-zip-compressed";
    public static final String MESSAGE_RFC_822 = "message/rfc822";

    private final Session session = Session.getInstance(System.getProperties(), null);
    private final AtomicLong fileNameIncremental = new AtomicLong();

    public static void main(String[] args) throws Exception{
        final CLIParameters parameters = new CLIParameters();
        JCommander jCommander = JCommander.newBuilder().addObject(parameters).build();
        jCommander.parse(args);

        EmailProcessor emailProcessor = new EmailProcessor();
        emailProcessor.process(parameters);
    }

    void process(CLIParameters parameters) throws IOException, MessagingException {
        Objects.requireNonNull(parameters.getType(), "Expected source file type parameter");
        File sourceFile = FileUtils.getSourceFile(parameters);
        File outputDirectory = FileUtils.getOutputDirectory(parameters);
        switch (parameters.getType()){
            case ZIP:
                processZipArchive(sourceFile, outputDirectory);
                break;
            case EML:
                processMimeMessage(sourceFile, outputDirectory);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    void processZipArchive(File zipFile, File outputDirectory) throws IOException, MessagingException {
        if(!isZipStream(zipFile)){
            throw new IllegalArgumentException("ZIP stream magic sequence is not found");
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))){
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if(zipEntry.isDirectory()){
                    throw new IllegalArgumentException("Only root files in zip archive supported");
                }
                File payload = File.createTempFile("payload", "eml");
                try (FileOutputStream payloadStream = new FileOutputStream(payload)){
                    IOUtils.copy(zipInputStream, payloadStream);
                    processMimeMessage(payload, outputDirectory);
                } finally {
                    if(payload.exists() && !payload.delete()){
                        throw new RuntimeException("It is not possible to delete temporary entry copy from zip");
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    void processMimeMessage(File payload, File outputDirectory) throws IOException, MessagingException {
        if(isZipStream(payload)){
            throw new IllegalArgumentException("Expected text content but found ZIP stream magic sequence");
        }
        try (FileInputStream mimeStream = new FileInputStream(payload)){
            MimeMessage message = new MimeMessage(session, mimeStream);
            if(message.isMimeType(MULTIPART_MIXED)) {
                MimeMultipart content = (MimeMultipart) message.getContent();
                int contentCount = content.getCount();
                for(int contentIndex = 0; contentIndex < contentCount; contentIndex++){
                    BodyPart bodyPart = content.getBodyPart(contentIndex);
                    if(bodyPart.getSize()>0){
                        String[] contentType = bodyPart.getHeader(CONTENT_TYPE);
                        if(contentType.length>0 && contentType[0].contains(APPLICATION_X_ZIP_COMPRESSED)){
                            File zipPayload = File.createTempFile("payload", "zip");
                            try (InputStream attachmentInStream = bodyPart.getInputStream();
                                 FileOutputStream zipOutStream = new FileOutputStream(zipPayload)){
                                IOUtils.copy(attachmentInStream, zipOutStream);
                                processZipArchive(zipPayload, outputDirectory);
                            } finally {
                                if(zipPayload.exists() && !zipPayload.delete()){
                                    throw new RuntimeException("It is not possible to delete temporary zip file");
                                }
                            }
                        } else if(contentType.length>0 && contentType[0].contains(MESSAGE_RFC_822)){
                            String[] contentTypeParts = contentType[0].split("\\n");
                            String resultFileName = FileUtils.getResultFileName(contentTypeParts, fileNameIncremental);
                            File outputFile = new File(outputDirectory, resultFileName);
                            if(outputFile.exists()){
                                do{
                                    outputFile = new File(outputDirectory, FileUtils.getSyntheticIncrementalName(fileNameIncremental.getAndIncrement()));
                                } while (outputFile.exists());
                                //throw new IllegalArgumentException("File already exists "+outputFile.getAbsolutePath());
                            }
                            MimeMessage mimeMessage = (MimeMessage) bodyPart.getContent();
                            try (FileOutputStream emlOutputStream = new FileOutputStream(outputFile)){
                                mimeMessage.writeTo(emlOutputStream);
                            }
                        }
                    }
                }
            }
        }
    }

    boolean isZipStream(File payload) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(payload)){
            byte[] magicBytes = IOUtils.toByteArray(fileInputStream, 4);
            return Arrays.equals(magicBytes, new byte[]{80, 75, 3, 4});
        }
    }
}
