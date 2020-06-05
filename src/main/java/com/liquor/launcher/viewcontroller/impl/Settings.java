package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.event.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

public class Settings extends ViewController {

    public Settings(Document document) {
        super(document);
    }

    @Override
    public void load() {
        EventTarget saveButton = (EventTarget) document.getElementById("saveProfileButton");
        saveButton.addEventListener("click", (event) -> {
            ProfileManager.getInstance().getSelectedProfile().get().switchTheme();
           /// ProfileManager.getInstance().save(true);
        }, false);
    }
}
