package com.liquor.launcher.viewcontroller.impl;

import com.google.gson.Gson;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.model.CheckIpModel;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.scene.web.WebEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class Dashboard extends ViewController {


    private Gson gson;

    public Dashboard(WebEngine webEngine) {
        super(webEngine);
        gson = new Gson();
    }

    @SneakyThrows
    @Override
    public void load() {
        HttpsURLConnection request = (HttpsURLConnection) new URL("https://checkip.perfect-privacy.com/json").openConnection();
        request.setRequestMethod("GET");
        request.connect();
        Scanner scannedInput = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String json = scannedInput.hasNext() ? scannedInput.next() : "";
        CheckIpModel model = gson.fromJson(json, CheckIpModel.class);
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
