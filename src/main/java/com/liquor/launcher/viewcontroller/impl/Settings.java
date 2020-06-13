package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.scene.web.WebEngine;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLElement;

import java.util.Optional;

@Slf4j
public class Settings extends ViewController {

    public Settings(WebEngine webEngine) {
        super(webEngine);
    }

    @Override
    public void load() {
        log.info("Loading settings...");
        HTMLElement darkButton = (HTMLElement) document.getElementById("darkButton");
        HTMLElement lightButton = (HTMLElement) document.getElementById("lightButton");
        HTMLElement exportProfileButton = (HTMLElement) document.getElementById("exportProfileButton");
        Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
        if (potentialProfile.isPresent()) {
            Profile profile = potentialProfile.get();
            toggleActiveStates(darkButton, lightButton, profile);
            addDarkButtonListener(darkButton, lightButton, profile);
            addLightButtonListener(darkButton, lightButton, profile);
            addExportProfileButtonListener((EventTarget) exportProfileButton);
        } else {
            log.error("Profile not present!");
        }
    }

    private void toggleActiveStates(HTMLElement darkButton, HTMLElement lightButton, Profile profile) {
        if (profile.getTheme() == Theme.DARK) {
            darkButton.setClassName(String.format("%s %s", darkButton.getClassName(), "active"));
        } else {
            lightButton.setClassName(String.format("%s %s", lightButton.getClassName(), "active"));
        }
    }

    private void addExportProfileButtonListener(EventTarget exportProfileButton) {
        exportProfileButton.addEventListener("click", (event) -> ProfileManager.getInstance().exportProfile(), false);
    }

    private void addLightButtonListener(HTMLElement darkButton, HTMLElement lightButton, Profile profile) {
        ((EventTarget) lightButton).addEventListener("click", (event) -> {
            if (profile.getTheme() == Theme.LIGHT) {
                return;
            }
            if (darkButton.getClassName().contains("active")) {
                darkButton.setClassName(darkButton.getClassName().replace("active", ""));
            }
            updateButton(lightButton, String.format("%s %s", lightButton.getClassName(), "active"));
            updateTheme(profile, Theme.LIGHT);
        }, false);
    }

    private void addDarkButtonListener(HTMLElement darkButton, HTMLElement lightButton, Profile profile) {
        ((EventTarget) darkButton).addEventListener("click", (event) -> {
            if (profile.getTheme() == Theme.DARK) {
                return;
            }
            if (lightButton.getClassName().contains("active")) {
                lightButton.setClassName(lightButton.getClassName().replace("active", ""));
            }
            updateButton(darkButton, String.format("%s %s", darkButton.getClassName(), "active"));
            updateTheme(profile, Theme.DARK);
        }, false);
    }

    private void updateButton(HTMLElement lightButton, String active) {
        lightButton.setClassName(active);
    }

    private void updateTheme(Profile profile, Theme dark) {
        profile.updateStyle(dark);
        ProfileManager.getInstance().save(false);
        init();
    }
}
