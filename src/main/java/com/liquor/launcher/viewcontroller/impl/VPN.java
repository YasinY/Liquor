package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.functionality.perfectprivacy.PerfectPrivacyAuthenticator;
import com.liquor.launcher.viewcontroller.ViewController;
import com.liquor.prerequisites.openvpn.OpenVPNResource;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLButtonElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLInputElement;

@Slf4j
public class VPN extends ViewController {


    public VPN(Document document) {
        super(document);
    }

    @Override
    public void load() {
        HTMLElement checkCredentialsButton = (HTMLButtonElement) document.getElementById("checkCredentialsButton");
        PauseTransition transition = new PauseTransition(Duration.seconds(2));
        ((EventTarget) checkCredentialsButton).addEventListener("click", checkCredentialsButtonAction(checkCredentialsButton, transition), false);
    }

    private EventListener checkCredentialsButtonAction(HTMLElement checkCredentialsButton, PauseTransition transition) {
        return (event) -> {
            if (transition.getStatus() == Animation.Status.RUNNING) {
                return;
            }
            NodeList list = document.getElementsByTagName("input");
            HTMLInputElement usernameInput = (HTMLInputElement) list.item(0);
            HTMLInputElement passwordInput = (HTMLInputElement) list.item(1);
            String username = getValidInput(usernameInput);
            String password = getValidInput(passwordInput);
            if (invalidCredentials(transition, username, password)) {
                return;
            }

            boolean successful = PerfectPrivacyAuthenticator.authenticate(username, password);
            handleRequest(checkCredentialsButton, transition, successful);
            if(successful) {
                OpenVPNResource.updateAuthentication(username, password);
            }
        };
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
        if (username.length() < 5 || username.matches("^[a-zA-Z0-9]{3,}$")) {
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
