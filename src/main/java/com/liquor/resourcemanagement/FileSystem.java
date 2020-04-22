package com.liquor.resourcemanagement;

import com.liquor.resourcemanagement.registered.RegisteredResource;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileSystem {

    public static String CACHE_DIRECTORY = String.format("%s\\%s\\", System.getProperty("user.home"), ".liquor");

    public static void writeContent(RegisteredResource resource, boolean append, String... content) {
        String parentDirectories = CACHE_DIRECTORY + resource.getDirectoryStructure();
        Path parentDirectoriesPath = Paths.get(parentDirectories);
        if (!Files.exists(parentDirectoriesPath)) {
            boolean creationSuccessful = parentDirectoriesPath.toFile().mkdirs();
            log.info("Successful creation of directories " + resource.getName() + ": " + creationSuccessful);
        }
        Path resourcePath = parentDirectoriesPath.resolve(resource.getFileStructure());
        File resourceFile = resourcePath.toFile();
        if (!Files.exists(resourcePath)) {
            try {
                boolean creationSuccessful = resourceFile.createNewFile();
                log.info("Successful creation of file: " + resourceFile.getName() + ": " + creationSuccessful);
            } catch (IOException e) {
                log.error("Couldn't create file " + resourceFile.getName() + " (" + e.getMessage() + ")");
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resourcePath.toFile(), append))) {
            writer.write(String.join(" ", content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
