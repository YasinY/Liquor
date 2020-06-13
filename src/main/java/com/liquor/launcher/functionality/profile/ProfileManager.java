package com.liquor.launcher.functionality.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.resourcemanagement.FileSystem;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
public class ProfileManager {

    private static ProfileManager instance;

    private Profile selectedProfile;

    private Gson gson;

    private ProfileManager() {
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public boolean init() {
        selectedProfile = Profile.builder().theme(Theme.LIGHT).minutes(0).build();
        return save(false);
    }

    public boolean save(boolean open) {
        if (selectedProfile == null) {
            log.error("Unspecified profile, can't serialize.");
            return false;
        }
        String profile = gson.toJson(selectedProfile);
        FileSystem.writeContent(RegisteredResource.PROFILE, false, open, profile);
        log.info("Saved profile!");
        return true;
    }

    public boolean load() {
        if (!RegisteredResource.PROFILE.exists()) {
            log.error("Couldn't load non-existent profile.");
            return false;
        }
        String line = FileSystem.readFirstLine(RegisteredResource.PROFILE);
        this.selectedProfile = gson.fromJson(line, Profile.class);
        return true;
    }

    public boolean exportProfile() {
        if(selectedProfile == null) {
            return false;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the location to which your profile should get exported to");
        File defaultDirectory = new File(RegisteredResource.PROFILE.getFullDirectoryPath());
        directoryChooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if(selectedDirectory != null) {
            try {
                Files.copy(Paths.get(RegisteredResource.PROFILE.getFullFilePath()), selectedDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error("Error copying file..");
            }
        }
        return true;
    }

    public Optional<Profile> getSelectedProfile() {
        if (selectedProfile == null) {
            load();
        }
        return Optional.ofNullable(selectedProfile);
    }

    public void setSelectedProfile(Profile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public static ProfileManager getInstance() {
        return instance == null ? instance = new ProfileManager() : instance;
    }
}
