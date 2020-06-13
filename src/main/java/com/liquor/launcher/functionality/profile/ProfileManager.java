package com.liquor.launcher.functionality.profile;

import com.google.gson.Gson;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.resourcemanagement.FileSystem;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import lombok.extern.slf4j.Slf4j;

import java.awt.peer.LightweightPeer;
import java.util.Optional;

@Slf4j
public class ProfileManager {

    private static ProfileManager instance;

    private Profile selectedProfile;

    private Gson gson;

    private ProfileManager() {
        this.gson = new Gson();
    }

    public boolean init() {
        selectedProfile = Profile.builder().theme(Theme.LIGHT).totalTime(0).build();
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
