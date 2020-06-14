package com.liquor.launcher.functionality.ip;

import com.google.gson.Gson;
import com.liquor.launcher.model.CheckIpModel;
import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Scanner;

public class IpChecker {

    private static IpChecker instance;

    private Gson gson;

    private CheckIpModel model;

    private IpChecker() {
        this.gson = new Gson();
        this.model = CheckIpModel.builder().build();
    }

    @SneakyThrows
    public CheckIpModel refresh() {
        HttpsURLConnection request = (HttpsURLConnection) new URL("https://checkip.perfect-privacy.com/json").openConnection();
        request.setRequestMethod("GET");
        request.connect();
        Scanner scannedInput = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String json = scannedInput.hasNext() ? scannedInput.next() : "";
        return model = gson.fromJson(json, CheckIpModel.class);
    }

    public static IpChecker getInstance() {
        return instance == null ? instance = new IpChecker() : instance;
    }

}
