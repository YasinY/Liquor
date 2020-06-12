package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.viewcontroller.ViewController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

public class VPN extends ViewController {

    public VPN(Document document) {
        super(document);
    }

    @Override
    public void load() {

        //document.getElementsByTagName("head").item(0).appendChild()
    }

    public static void establishConnection() throws IOException {
        URL url = new URL("https://www.perfect-privacy.com/en/login");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Scanner s = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        int csrfTokenIndex = result.indexOf("_csrf_protection_token");

        System.out.println(result);
        //urlConnection.setRequestMethod("POST");
    }
}
