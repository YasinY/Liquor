package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.viewcontroller.ViewController;
import com.liquor.prerequisites.openvpn.OpenVPNLocation;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import com.sun.webkit.dom.HTMLAnchorElementImpl;
import javafx.animation.PauseTransition;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class Authenticated extends ViewController {


    private Thread currentThread;

    private Process vpnConnection;

    private String selectedConfiguration;

    private PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));


    public Authenticated(WebEngine webEngine) {
        super(webEngine);
    }

    @Override
    public void load() {
        log.info("Loading authenticated...");
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");

        fillCities();
        NodeList cities = document.getElementsByTagName("a");
        assignCityActions(cities);
        addConnectButtonAction(pauseTransition);
        addDisconnectButtonAction((EventTarget) disconnectButton);
    }

    private void assignCityActions(NodeList cities) {
        IntStream.range(0, cities.getLength()).forEach(index -> {
            HTMLAnchorElementImpl element = (HTMLAnchorElementImpl) cities.item(index);
            addCityClickEvent(element, cities, index);
        });
    }

    private void addConnectButtonAction(PauseTransition transition) {
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");
        ((EventTarget) connectButton).addEventListener("click", (connectEvent) -> {
            connectButton.setClassName(String.format("%s %s", connectButton.getClassName(), "d-none"));
            disconnectButton.setClassName(disconnectButton.getClassName().replace("d-none", ""));
            initDisconnectFunction();
            log.info("Connecting to VPN");
            connectToVpn();
        }, false);
    }

    private void addDisconnectButtonAction(EventTarget disconnectButton) {
        disconnectButton.addEventListener("click", (disconnectEvent) -> {
            if (vpnConnection != null) {
                currentThread.interrupt();
                vpnConnection.destroyForcibly();
                try {
                    Runtime.getRuntime().exec("wmic process where \"name like '%openvpn%'\" delete");
                } catch (IOException e) {
                    log.error("Couldnt run command to destroy openvpn process.");
                }
            }
        }, false);
    }

    private void fillCities() {
        HTMLDivElement cities = (HTMLDivElement) document.getElementById("cities");
        HTMLAnchorElementImpl citySample = (HTMLAnchorElementImpl) document.getElementById("sample");
        Stream.of(OpenVPNLocation.values()).map(formatLocation()).forEach(prepareAndAppendCity(cities, citySample));
    }

    private Consumer<String> prepareAndAppendCity(HTMLDivElement cities, HTMLAnchorElementImpl sample) {
        return location -> {
            HTMLAnchorElementImpl clone = (HTMLAnchorElementImpl) sample.cloneNode(true);
            clone.setClassName(clone.getClassName().replace("d-none", ""));
            clone.setTextContent(location);
            cities.appendChild(clone);
        };
    }

    private Function<OpenVPNLocation, String> formatLocation() {
        return location -> {
            String locationName = location.name().replace("_", " ");
            return locationName.substring(0, 1).toUpperCase() + locationName.substring(1).toLowerCase();
        };
    }

    private void addCityClickEvent(HTMLAnchorElementImpl element, NodeList cities, int elementIndex) {
        ((EventTarget) element).addEventListener("click", (cityEvent) -> {
            IntStream.range(0, cities.getLength()).forEach(index -> {
                HTMLAnchorElementImpl otherCity = (HTMLAnchorElementImpl) cities.item(index);
                if (index == elementIndex) {
                    return;
                }
                otherCity.setClassName(otherCity.getClassName().replace("active", ""));
            });
            this.selectedConfiguration = element.getText();
            element.setClassName(element.getClassName() + " active");
        }, false);
    }


    @SneakyThrows
    private void connectToVpn() {
        Runnable runnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                ProcessBuilder builder = new ProcessBuilder(
                        "cmd.exe", "/C", "openvpn.exe --cd " + RegisteredResource.AUTH.getFullDirectoryPath() + " --config " + selectedConfiguration + ".ovpn"
                );
                System.out.println("Command: " + String.format("%s%s%s", "cmd.exe", "/C", "openvpn --cd " + RegisteredResource.AUTH.getFullDirectoryPath() + " --config " + selectedConfiguration + ".ovpn"));
                builder.redirectErrorStream(true);
                vpnConnection = builder.start();
                BufferedReader r = new BufferedReader(new InputStreamReader(vpnConnection.getInputStream()));
                String line;
                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.contains("netsh command failed")) {
                        handleFailedNotification();
                    }
                    if (line.contains("End ipconfig commands for register-dns...")) {
                        handleSuccessfulConnect();
                    }
                    log.info(line);
                }
                try {
                    vpnConnection.waitFor();
                } catch (InterruptedException e) {
                    log.info("Disconnected!");
                }
            }
        };
        currentThread = new Thread(runnable);
        currentThread.setDaemon(true);
        currentThread.start();
    }

    private void playNotification(HTMLElement notification) {
        notify(notification);
        handleNotificationPlayback(notification);
    }

    private void handleNotificationPlayback(HTMLElement notification) {
        pauseTransition.setOnFinished((event1 -> hideNotification(notification)));
        pauseTransition.play();
    }

    private void hideNotification(HTMLElement notification) {
        notification.setClassName(notification.getClassName() + " d-none");
    }

    private void handleSuccessfulConnect() {
        HTMLElement notification = getSuccessfullyConnected();
        playNotification(notification);
    }

    private void handleFailedNotification() {
        HTMLElement notification = getErrorConnecting();
        playNotification(notification);
    }

    private void handleSuccessfulDisconnect() {
        HTMLElement notification = getSuccessfullDisconnect();
        notify(notification);
    }

    private void notify(HTMLElement notification) {
        String className = notification.getClassName();
        className = className.replace("d-none", "");
        notification.setClassName(className);
    }

    private NodeList getAllNotifications() {
        return document.getElementById("notificationContainer").getElementsByTagName("div");
    }

    private HTMLElement getSuccessfullyConnected() {
        return (HTMLElement) getAllNotifications().item(0);
    }

    private HTMLElement getErrorConnecting() {
        return (HTMLElement) getAllNotifications().item(1);
    }

    private HTMLElement getSuccessfullDisconnect() {
        return (HTMLElement) getAllNotifications().item(2);
    }

    private void initDisconnectFunction() {
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");
        ((EventTarget) disconnectButton).addEventListener("click", (disconnectEvent) -> {
            disconnectButton.setClassName(String.format("%s %s", disconnectButton.getClassName(), "d-none"));
            connectButton.setClassName(connectButton.getClassName().replace("d-none", ""));
        }, false);
    }

}
