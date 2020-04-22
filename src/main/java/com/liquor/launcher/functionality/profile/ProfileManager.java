package com.liquor.launcher.functionality.profile;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileManager {

    private static ProfileManager instance;

    private Profile selectedProfile;


    public boolean serialize() {
        if (selectedProfile == null) {
            log.error("Unspecified profile, can't serialize.");
            return false;
        }
        new Gson().toJson(selectedProfile);
        return true;
    }

    public boolean deserialize() {
        if(selectedProfile == null) {
            log.error("Unspecified profile, can't deserialize.");
            return false;
        }

        return true;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(Profile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public static ProfileManager getInstance() {
        return instance == null ? instance = new ProfileManager() : instance;
    }
}
