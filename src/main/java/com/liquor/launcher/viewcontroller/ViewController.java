package com.liquor.launcher.viewcontroller;

import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.functionality.theme.Theme;
import javafx.scene.web.WebEngine;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLLinkElement;

import java.util.Optional;

public class ViewController implements IViewController {

    protected WebEngine webEngine;
    protected Document document;

    public ViewController() {

    }

    public ViewController(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.document = webEngine.getDocument();
    }

    @Override
    public void init() {
        if (document != null) {
            HTMLHeadElement head = (HTMLHeadElement) document.getElementsByTagName("head").item(0);
            Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
            potentialProfile.ifPresent(profile -> {
                NodeList elements = head.getElementsByTagName("link");
                HTMLLinkElement stylesheet = (HTMLLinkElement) elements.item(elements.getLength() - 1);
                if (profile.getTheme() == Theme.LIGHT) {
                    stylesheet.setHref("./style-light.css");
                } else {
                    stylesheet.setHref("./style-dark.css");
                }
            });
        }
        load();
    }

    @Override
    public void load() {

    }
}
