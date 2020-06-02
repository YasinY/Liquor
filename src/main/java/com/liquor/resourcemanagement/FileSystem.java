package com.liquor.resourcemanagement;

import com.liquor.resourcemanagement.registered.RegisteredResource;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FileSystem {

    public static String CACHE_DIRECTORY = String.format("%s\\%s\\", System.getProperty("user.home"), ".liquor");

    public static void writeContent(RegisteredResource resource, boolean append, boolean open, String... content) {
        Path parentDirectoriesPath = Paths.get(CACHE_DIRECTORY, resource.getDirectoryStructure());
        ensureDirectories(resource, parentDirectoriesPath);
        Path resourcePath = parentDirectoriesPath.resolve(resource.getFileStructure());
        File resourceFile = resourcePath.toFile();
        ensureFileExistence(resourcePath, resourceFile);
        writeContentToFile(append, resourcePath, content);
        if (open) {
            try {
                Desktop.getDesktop().open(resourceFile.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void writeContentToFile(boolean append, Path resourcePath, String[] content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resourcePath.toFile(), append))) {
            writer.write(String.join(" ", content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureFileExistence(Path resourcePath, File resourceFile) {
        if (!Files.exists(resourcePath)) {
            try {
                boolean creationSuccessful = resourceFile.createNewFile();
                log.info("Successful creation of file: " + resourceFile.getName() + ": " + creationSuccessful);
            } catch (IOException e) {
                log.error("Couldn't create file " + resourceFile.getName() + " (" + e.getMessage() + ")");
            }
        }
    }

    public static void ensureDirectories(RegisteredResource resource, Path parentDirectoriesPath) {
        if (!Files.exists(parentDirectoriesPath)) {
            boolean creationSuccessful = parentDirectoriesPath.toFile().mkdirs();
            log.info("Successful creation of directories " + resource.getName() + ": " + creationSuccessful);
        }
    }

    public static List<String> readContent(RegisteredResource resource) {
        Path resourcePath = Paths.get(CACHE_DIRECTORY, resource.getFullStructure());
        Path parentDirectoriesPath = Paths.get(CACHE_DIRECTORY, resource.getDirectoryStructure());
        ensureDirectories(resource, parentDirectoriesPath);
        if (Files.exists(resourcePath)) {
            try {
                return Files.lines(resourcePath).collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Could not read content of proposed resource: " + resource.getName() + " (" + e.getMessage() + ")");
            }
        }
        return Collections.emptyList();
    }

    public static String readFirstLine(RegisteredResource resource) {
        List<String> content = readContent(resource);
        return content.size() > 0 ? content.get(0) : null;
    }
}
