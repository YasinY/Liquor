package com.liquor.launcher.viewcontroller.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;

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
        HTMLElement importProfileButton = (HTMLElement) document.getElementById("importProfileButton");
        Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
        if (potentialProfile.isPresent()) {
            Profile profile = potentialProfile.get();
            toggleActiveStates(darkButton, lightButton, profile);
            addDarkButtonListener(darkButton, lightButton, profile);
            addLightButtonListener(darkButton, lightButton, profile);
            addExportProfileButtonListener((EventTarget) exportProfileButton);
            addImportProfileButtonListener((EventTarget) importProfileButton);
        } else {
            log.error("Profile not present!");
        }
    }

    private void addImportProfileButtonListener(EventTarget importProfileButton) {
        importProfileButton.addEventListener("click", (event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Please choose a profile");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Liquor profile", "*.lqp"));
            fileChooser.setInitialFileName("profile");
            File file = fileChooser.showOpenDialog(new Stage());
            if(file != null) {
                System.out.println("Chose file " + file);
                Gson gson = new Gson();
                try {
                    Profile profile = gson.fromJson(Files.lines(file.toPath()).collect(Collectors.joining()), Profile.class);
                    ProfileManager.getInstance().setSelectedProfile(profile);
                    ProfileManager.getInstance().save(false);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Saved!");
                    alert.setContentText("Successfully imported profile. Restart the tool for final changes to occur.");
                    alert.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, false);
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
