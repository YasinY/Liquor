package com.liquor.launcher.viewcontroller.impl;

import com.google.gson.Gson;
import com.liquor.launcher.functionality.ip.IpChecker;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.model.CheckIpModel;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.scene.web.WebEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class Dashboard extends ViewController {


    public Dashboard(WebEngine webEngine) {
        super(webEngine);
    }

    @SneakyThrows
    @Override
    public void load() {
        CheckIpModel model = IpChecker.getInstance().refresh();
        NodeList paragraphs = document.getElementsByTagName("p");
        paragraphs.item(1).setTextContent(model.getIpAddress());
        paragraphs.item(3).setTextContent( model.getDns());
        paragraphs.item(5).setTextContent( model.getCity());
        paragraphs.item(7).setTextContent(model.getCountry());
        paragraphs.item(9).setTextContent((model.isUsingVpn() ? "Yes" : "No"));

        final Optional<Profile> selectedProfile = ProfileManager.getInstance().getSelectedProfile();
        if(selectedProfile.isPresent()) {
            Profile profile = selectedProfile.get();
           paragraphs.item(11).setTextContent(String.format("%d D %d H %d M", profile.getDays(), profile.getHours(), profile.getMinutes() % 60));
        }
        log.info("Dashboard action taken");

    }

}
