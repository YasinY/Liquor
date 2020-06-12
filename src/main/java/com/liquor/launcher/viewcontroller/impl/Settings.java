package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.launcher.viewcontroller.ViewController;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLElement;

import java.util.Optional;

@Slf4j
public class Settings extends ViewController {

    public Settings(Document document) {
        super(document);
    }

    @Override
    public void load() {
        HTMLElement darkButton = (HTMLElement) document.getElementById("darkButton");
        HTMLElement lightButton = (HTMLElement) document.getElementById("lightButton");
        Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
        if (potentialProfile.isPresent()) {
            Profile profile = potentialProfile.get();
            ((EventTarget) darkButton).addEventListener("click", (event) -> {
                if(profile.getTheme() == Theme.DARK) {
                    return;
                }
                if (lightButton.getClassName().contains("active")) {
                    lightButton.setClassName(lightButton.getClassName().replace("active", ""));
                }
                updateButton(darkButton, String.format("%s %s", darkButton.getClassName(), "active"));
                updateTheme(profile, Theme.DARK);
            }, false);
            ((EventTarget) lightButton).addEventListener("click", (event) -> {
                if(profile.getTheme() == Theme.LIGHT) {
                    return;
                }
                if (darkButton.getClassName().contains("active")) {
                    darkButton.setClassName(darkButton.getClassName().replace("active", ""));
                }
                updateButton(lightButton, String.format("%s %s", lightButton.getClassName(), "active"));
                updateTheme(profile, Theme.LIGHT);
            }, false);

        } else {
            log.error("Profile not present!");
        }
    }

    private void updateButton(HTMLElement lightButton, String active) {
        lightButton.setClassName(active);
    }

    private void updateTheme(Profile profile, Theme dark) {
        profile.updateStyle(dark);
        ProfileManager.getInstance().save(false);
    }
}
