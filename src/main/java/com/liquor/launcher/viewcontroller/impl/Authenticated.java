package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.functionality.ip.IpChecker;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.model.CheckIpModel;
import com.liquor.launcher.viewcontroller.ViewController;
import com.liquor.prerequisites.openvpn.OpenVPNLocation;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import com.sun.webkit.dom.HTMLAnchorElementImpl;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLElement;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class Authenticated extends ViewController {


    private Process vpnConnection;

    private String selectedConfiguration;

    private PauseTransition transition = new PauseTransition(Duration.seconds(2));


    public Authenticated(WebEngine webEngine) {
        super(webEngine);
    }

    @Override
    public void load() {
        log.info("Loading authenticated...");
        refreshData();

        fillCities();
        NodeList cities = document.getElementsByTagName("a");
        assignCityActions(cities);
        addConnectButtonAction();
        addDisconnectButtonAction();
        addLogoutButtonAction();
        hideDisconnect();
    }

    private void addLogoutButtonAction() {
        ((EventTarget) document.getElementById("logoutButton")).addEventListener("click", (event) -> {
            logout();
        }, false);
    }

    private void refreshData() {
        CheckIpModel model = IpChecker.getInstance().refresh();
        Platform.runLater(() -> {
            document.getElementById("assignedIp").setTextContent(model.getIpAddress());
            document.getElementById("usingVPN").setTextContent(model.isUsingVpn() ? "Yes" : "No");
        });
    }

    private void assignCityActions(NodeList cities) {
        IntStream.range(0, cities.getLength()).forEach(index -> {
            HTMLAnchorElementImpl element = (HTMLAnchorElementImpl) cities.item(index);
            addCityClickEvent(element, cities, index);
        });
    }

    private void addConnectButtonAction() {
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        ((EventTarget) connectButton).addEventListener("click", (connectEvent) -> {
            log.info("Connecting to VPN");
            connectToVpn();
        }, false);
    }

    private void addDisconnectButtonAction() {
        ((EventTarget) document.getElementById("disconnectButton")).addEventListener("click", (disconnectEvent) -> {
            disconnect();
            playNotification(getSuccessfulDisconnect());
        }, false);
    }

    private void disconnect() {
        if (vpnConnection != null) {
            vpnConnection.destroyForcibly();
            try {
                Runtime.getRuntime().exec("wmic process where \"name like '%openvpn%'\" delete");
                Thread.sleep(5000);
                refreshData();
            } catch (IOException | InterruptedException e) {
                log.error("Couldnt run command to destroy openvpn process.");
            }
        }
    }

    private void logout() {
        Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
        potentialProfile.ifPresent(profile -> profile.setAuthenticated(false));
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


    private void connectToVpn() {
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        connectButton.setTextContent("Connecting..");
        connectButton.setDisabled(true);
        Runnable runnable = () -> {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/C", "openvpn.exe --cd " + RegisteredResource.AUTH.getFullDirectoryPath() + " --config " + selectedConfiguration + ".ovpn"
            );
            builder.redirectErrorStream(true);
            try {
                vpnConnection = builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(vpnConnection.getInputStream()));
            String line;
            while (true) {
                try {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.contains("netsh command failed")) {
                        disconnect();
                        Platform.runLater(() -> {
                            connectButton.setTextContent("Connect");
                            connectButton.setDisabled(false);
                            playNotification(getErrorConnecting());
                        });
                        break;
                    }
                    if (line.contains("End ipconfig commands for register-dns...")) {
                        Platform.runLater(() -> {
                            connectButton.setTextContent("Connect");
                            connectButton.setDisabled(false);
                            HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");
                            connectButton.setClassName(connectButton.getClassName() + " d-none");
                            disconnectButton.setClassName(disconnectButton.getClassName().replace("d-none", ""));
                            playNotification(getSuccessfulConnection());
                            refreshData();
                        });
                    }
                    log.info(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                vpnConnection.waitFor();
            } catch (InterruptedException e) {
                log.info("Disconnected!");
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();

    }

    private void playNotification(HTMLElement notification) {
        notify(notification);
        handleNotificationPlayback(notification);
    }

    private void handleNotificationPlayback(HTMLElement notification) {
        transition.setOnFinished((event1 -> hideNotification(notification)));
        transition.play();
    }

    private void notify(HTMLElement notification) {
        String className = notification.getClassName();
        className = className.replace("d-none", "");
        notification.setClassName(className);
    }

    private void hideNotification(HTMLElement notification) {
        notification.setClassName(notification.getClassName() + " d-none");
    }

    private NodeList getAllNotifications() {
        return document.getElementById("notificationContainer").getElementsByTagName("div");
    }

    private HTMLElement getSuccessfulConnection() {
        return (HTMLElement) getAllNotifications().item(0);
    }

    private HTMLElement getErrorConnecting() {
        return (HTMLElement) getAllNotifications().item(1);
    }

    private HTMLElement getSuccessfulDisconnect() {
        return (HTMLElement) getAllNotifications().item(2);
    }

    private void hideDisconnect() {
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");
        ((EventTarget) disconnectButton).addEventListener("click", (disconnectEvent) -> {
            disconnectButton.setClassName(String.format("%s %s", disconnectButton.getClassName(), "d-none"));
            connectButton.setClassName(connectButton.getClassName().replace("d-none", ""));
        }, false);
    }

}
