package com.liquor.resourcemanagement.registered;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public enum RegisteredResource {
    PROFILE("currentprofile", "lqp", "profile", "suck", "a", "cock"),
    AUTH("auth", "txt", "config");
    private String name;
    private String extension;
    private String[] directory;

    RegisteredResource(String name, String extension, String... directory) {
        this.name = name;
        this.extension = extension;
        this.directory = directory;

    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String[] getDirectories() {
        return directory;
    }

    public String getDirectoryStructure() {
        return String.format("%s",
                (directory.length > 0 ? String.join("\\", Arrays.asList(directory)) + "\\" : ""));
    }

    public String getFileStructure() {
        return String.format("%s.%s", name, extension);
    }

    public String getFullStructure() {
        return String.format("%s%s", getDirectoryStructure(), getFileStructure());
    }

    public String getFullFilePath() {
        return String.format("%s\\%s\\%s", System.getProperty("user.home"), ".liquor", getFullStructure());
    }
    public String getFullDirectoryPath() {
        return String.format("%s\\%s\\%s", System.getProperty("user.home"), ".liquor", getDirectoryStructure());
    }

    public boolean exists() {
        return Files.exists(Paths.get(getFullFilePath()));
    }
}
