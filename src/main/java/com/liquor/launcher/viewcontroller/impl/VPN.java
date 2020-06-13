package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.functionality.perfectprivacy.PerfectPrivacyAuthenticator;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.security.Decrypter;
import com.liquor.launcher.viewcontroller.ViewController;
import com.liquor.prerequisites.openvpn.OpenVPNLocation;
import com.liquor.prerequisites.openvpn.OpenVPNResource;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import com.sun.webkit.dom.HTMLAnchorElementImpl;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLInputElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class VPN extends ViewController {


    Thread currentThread;

    Process vpnConnection;

    private String selectedConfiguration;

    public VPN(WebEngine webEngine) {
        super(webEngine);
    }

    @Override
    public void load() {
        HTMLElement checkCredentialsButton = (HTMLButtonElement) document.getElementById("checkCredentialsButton");
        NodeList list = document.getElementsByTagName("input");
        HTMLInputElement usernameInput = (HTMLInputElement) list.item(0);
        HTMLInputElement passwordInput = (HTMLInputElement) list.item(1);
        ProfileManager.getInstance().getSelectedProfile().ifPresent(profile -> {
            if (!profile.getUsername().isEmpty()) {
                usernameInput.setValue(profile.getUsername());
            }

            if (!profile.getPassword().isEmpty()) {
                passwordInput.setValue(Decrypter.getInstance().decrypt(profile.getPassword()));
            }
        });
        fillCities();
        PauseTransition transition = new PauseTransition(Duration.seconds(2));
        addCheckCredentialsButtonAction(checkCredentialsButton, transition);
    }

    private void addCheckCredentialsButtonAction(HTMLElement checkCredentialsButton, PauseTransition transition) {
        ((EventTarget) checkCredentialsButton).addEventListener("click", checkCredentialsButtonAction(checkCredentialsButton, transition), false);
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

    private EventListener checkCredentialsButtonAction(HTMLElement checkCredentialsButton, PauseTransition transition) {
        return (event) -> {

            HTMLInputElement usernameInput = (HTMLInputElement) document.getElementById("usernameInput");
            HTMLInputElement passwordInput = (HTMLInputElement) document.getElementById("passwordInput");
            HTMLDivElement citiesContainer = (HTMLDivElement) document.getElementById("citiesContainer");
            HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
            HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");
            NodeList cities = document.getElementsByTagName("a");
            IntStream.range(0, cities.getLength()).forEach(index -> {
                HTMLAnchorElementImpl element = (HTMLAnchorElementImpl) cities.item(index);
                addCityClickEvent(element, cities, index);
            });
            String username = getValidInput(usernameInput);
            String password = getValidInput(passwordInput);
            if (transition.getStatus() == Animation.Status.RUNNING) {
                return;
            }
            if (invalidCredentials(transition, username, password)) {
                return;
            }
            boolean successful = PerfectPrivacyAuthenticator.authenticate(username, password);
            handleRequest(checkCredentialsButton, transition, successful);
            if (successful) {
                handleSuccessful(citiesContainer, username, password);
                connectButton.setClassName(connectButton.getClassName().replace("d-none", ""));
                addConnectButtonAction();
                addDisconnectButtonAction((EventTarget) disconnectButton);
            }
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

    private void addConnectButtonAction() {
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

    private void connectToVpn() {
        Runnable runnable = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                ProcessBuilder builder = new ProcessBuilder(
                        "cmd.exe", "/c", "openvpn --cd " + RegisteredResource.AUTH.getFullDirectoryPath() + " --config " + selectedConfiguration + ".ovpn"
                );
                builder.redirectErrorStream(true);
                vpnConnection = builder.start();
                BufferedReader r = new BufferedReader(new InputStreamReader(vpnConnection.getInputStream()));
                String line;
                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    log.info(line);
                }
                vpnConnection.waitFor();
            }
        };
        currentThread = new Thread(runnable);
        currentThread.setDaemon(true);
        currentThread.start();
    }

    private void initDisconnectFunction() {
        HTMLButtonElement connectButton = (HTMLButtonElement) document.getElementById("connectButton");
        HTMLButtonElement disconnectButton = (HTMLButtonElement) document.getElementById("disconnectButton");
        ((EventTarget) disconnectButton).addEventListener("click", (disconnectEvent) -> {
            disconnectButton.setClassName(String.format("%s %s", disconnectButton.getClassName(), "d-none"));
            connectButton.setClassName(connectButton.getClassName().replace("d-none", ""));
        }, false);
    }

    private void handleSuccessful(HTMLDivElement citiesContainer, String username, String password) {
        OpenVPNResource.updateAuthentication(username, password);
        final Optional<Profile> selectedProfile = ProfileManager.getInstance().getSelectedProfile();
        selectedProfile.ifPresent(profile -> {
            if (profile.isRememberData()) {
                profile.setUsername(username);
                profile.setPassword(Decrypter.getInstance().encrypt(password));
                ProfileManager.getInstance().save(false);
            }
        });
        final HTMLElement loginContainer = (HTMLElement) document.getElementById("loginContainer");
        loginContainer.setClassName(String.format("%s %s", loginContainer.getClassName(), "d-none"));
        citiesContainer.setClassName(citiesContainer.getClassName().replace("d-none", ""));
    }

    private String getValidInput(HTMLInputElement usernameInput) {
        return usernameInput.getValue() == null ? "" : usernameInput.getValue();
    }

    private void handleRequest(HTMLElement checkCredentialsButton, PauseTransition transition, boolean successful) {
        if (successful) {
            handleSuccessfulRequest(checkCredentialsButton, transition);
        } else {
            handleFailedRequest(transition);
        }
    }


    private void handleSuccessfulRequest(HTMLElement checkCredentialsButton, PauseTransition transition) {
        HTMLElement notification = getSuccessfulLoginNotification();
        playNotification(transition, notification);
        checkCredentialsButton.setClassName(String.format("%s %s", checkCredentialsButton.getClassName(), "d-none"));
    }

    private void handleFailedRequest(PauseTransition transition) {
        HTMLElement notification = getWrongCredentialsNotification();
        playNotification(transition, notification);
    }

    private boolean invalidCredentials(PauseTransition transition, String username, String password) {
        if (invalidUsername(transition, username)) {
            return true;
        }
        if (invalidPassword(transition, password)) {
            return true;
        }
        return false;
    }

    private boolean invalidPassword(PauseTransition transition, String password) {
        HTMLElement notification;
        if (password.length() < 5) {
            notification = getMissingPasswordNotification();
            playNotification(transition, notification);
            return true;
        }
        return false;
    }

    private boolean invalidUsername(PauseTransition transition, String username) {
        HTMLElement notification;
        if (username.length() < 5) {
            notification = getMissingUsernameNotification();
            playNotification(transition, notification);
            return true;
        }
        return false;
    }

    private void playNotification(PauseTransition transition, HTMLElement notification) {
        notify(notification);
        handleNotificationPlayback(transition, notification);
    }

    private void handleNotificationPlayback(PauseTransition transition, HTMLElement notification) {
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

    private HTMLElement getSuccessfulLoginNotification() {
        return (HTMLElement) getAllNotifications().item(0);
    }

    private HTMLElement getMissingUsernameNotification() {
        return (HTMLElement) getAllNotifications().item(1);
    }

    private HTMLElement getMissingPasswordNotification() {
        return (HTMLElement) getAllNotifications().item(2);
    }

    private HTMLElement getWrongCredentialsNotification() {
        return (HTMLElement) getAllNotifications().item(3);
    }


}
