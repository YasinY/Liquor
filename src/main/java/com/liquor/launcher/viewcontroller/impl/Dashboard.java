package com.liquor.launcher.viewcontroller.impl;

import com.google.gson.Gson;
import com.liquor.launcher.model.CheckIpModel;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.scene.web.WebEngine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
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
        paragraphs.item(0).setTextContent("IP: " + model.getIpAddress());
        paragraphs.item(1).setTextContent("DNS: " + model.getDns());
        paragraphs.item(2).setTextContent("City: " + model.getCity());
        paragraphs.item(3).setTextContent("Country: " + model.getCountry());
        paragraphs.item(4).setTextContent("Using a VPN? " + (model.isUsingVpn() ? "yes" : "no"));
        paragraphs.item(5).setTextContent("Time spent on this application: ");
        log.info("Dashboard action taken");

    }
}
