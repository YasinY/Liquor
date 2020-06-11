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
            ((EventTarget) darkButton).addEventListener("click", getToggle(profile, Theme.DARK, darkButton, lightButton), false);
            ((EventTarget) lightButton).addEventListener("click", getToggle(profile, Theme.LIGHT, lightButton, darkButton), false);

        } else {
            log.error("Profile not present!");
        }
    }

    private EventListener getToggle(Profile profile, Theme theme, HTMLElement button, HTMLElement previousButton) {
        String className = previousButton.getClassName();
        if (className.contains("active")) {
            previousButton.setClassName(className.replace("active", ""));
        }
        button.setClassName(button.getClassName() + " active");
        return getListener(profile, theme);
    }

    private EventListener getListener(Profile profile, Theme theme) {
        return (event) -> {
            if (profile.getTheme() == theme) {
                return;
            }
            log.info("Changed theme to " + theme.getName());
            profile.updateStyle(theme);
            ProfileManager.getInstance().save(false);
        };
    }
}
