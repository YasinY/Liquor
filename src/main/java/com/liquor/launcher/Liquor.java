package com.liquor.launcher;

import com.liquor.launcher.annotations.Native;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.launcher.functionality.timer.TaskManager;
import com.liquor.launcher.splashscreen.SplashScreen;
import com.liquor.launcher.viewcontroller.IViewController;
import com.liquor.launcher.viewcontroller.ViewControllerFactory;
import com.liquor.launcher.window.ActiveWebViews;
import com.liquor.prerequisites.openvpn.OpenVPNResource;
import com.liquor.resourcemanagement.ResourceLoader;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class Liquor extends Application {

    public static Parent parent;

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane nativeView;

    @FXML
    private GridPane buttonGrid;


    @FXML
    public void handleTransition(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            if (nativeView.getId().equalsIgnoreCase("loadingView") && !webView.isVisible()) {
                return;
            }
            Button clickedButton = (Button) actionEvent.getSource();
            String viewName = clickedButton.getId();
            loadView(viewName);
        }
    }

    private void loadView(String viewName) {
        renderView(viewName, viewName.equalsIgnoreCase("terminal"));
    }

    private void disableButtons() {
        buttonGrid.getChildren().filtered(node -> node instanceof Button).forEach(button -> button.setDisable(true));
    }

    private void enableButtons() {
        buttonGrid.getChildren().filtered(node -> node instanceof Button).forEach(button -> button.setDisable(false));
    }

    private void renderView(String viewName, boolean nativeView) {
        disableButtons();
        if (nativeView) {
            log.info("Attempting to render native view " + viewName + "..");
            associateController(viewName);
            return;
        }
        log.info("Attempting to render web view..");
        initialiseWebView(viewName);
    }

    private void initialiseWebView(String viewName) {
        log.info("Getting html..");
        Optional<URL> url = ResourceLoader.getHTML(viewName);
        if(url == null) {
            System.out.println("FUCK");
        }
        url.ifPresent(consumer -> {
            setVisibilities(false);
            initialiseWebView(viewName, consumer);
        });
    }

    private void setVisibilities(boolean prioritizeNative) {
        log.info("Ensuring visibility ..");
        if (prioritizeNative) {
            if (this.webView.isVisible()) {
                this.webView.setVisible(false);
            }
            if (!this.nativeView.isVisible()) {
                this.nativeView.setVisible(true);
            }
        } else {
            if (this.nativeView.isVisible()) {
                this.nativeView.setVisible(false);
            }
            if (!webView.isVisible()) {
                webView.setVisible(true);
            }
        }
    }

    private synchronized void initialiseWebView(String viewName, URL consumer) {
        log.info("Loading view..");
        if(ActiveWebViews.ACTIVE_WEB_VIEWS.containsKey(viewName)) {
            webView = ActiveWebViews.ACTIVE_WEB_VIEWS.get(viewName);
            log.info("Loading already active web view.. (" + viewName + ")");

        } else {
            webView.getEngine().load(consumer.toExternalForm());
            log.info("Adding listener..");
            ActiveWebViews.ACTIVE_WEB_VIEWS.put(viewName, webView);
            log.info("Creating active web view..");
            webView.getEngine().getLoadWorker().stateProperty().addListener(getChangedListener(viewName));
        }
    }

    private ChangeListener<Object> getChangedListener(String viewName) {
        return new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    log.info("Initialising controller..");
                    initialiseViewController(viewName);
                    webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
                    enableButtons();
                }
            }
        };
    }

    private void initialiseViewController(String viewName) {
        log.info("Loading view controller of view " + viewName + "..");
        IViewController webViewController = ViewControllerFactory.produceViewController(viewName, webView.getEngine());

        if (webViewController.getClass().isAnnotationPresent(Native.class)) {
            log.error("Aborted loading controller for " + viewName + " as it has been marked as native.");
            return;
        }
        webViewController.init();
    }

    private void associateController(String viewName) {
        log.info("Locating view controller..");
        IViewController viewController = ViewControllerFactory.produceViewController(viewName);
        Optional<URL> potentialFxml = ResourceLoader.getFXML(viewName, viewController.getClass());
        if (potentialFxml.isPresent()) {
            URL fxml = potentialFxml.get();
            try {
                log.info("Loading fxml file-content of file " + viewName + "..");
                FXMLLoader loader = new FXMLLoader(fxml);
                AnchorPane loadedContent = loader.load();
                loadViewController(loader);
                appendNativeWindow(loadedContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadViewController(FXMLLoader loader) {
        log.info("Loading native view controller..");
        if (loader.getController() != null && loader.getController() instanceof IViewController) {
            IViewController nativeViewController = loader.getController();
            nativeViewController.load();
        }
    }

    private void appendNativeWindow(AnchorPane loadedContent) {
        this.nativeView.setId(loadedContent.getId());
        this.nativeView.getChildren().setAll(loadedContent.getChildren());
        setVisibilities(true);
        enableButtons();
    }


    @Override
    public void start(Stage currentStage) throws IOException {
        log.info("Starting application.. ");
        startup(currentStage);
    }

    private void startup(Stage currentStage) throws IOException {
        Optional<URL> potentialResource = ResourceLoader.getFXML("launcher");
        Optional<URL> potentialStylesheet = ResourceLoader.getCSS("launcher");
        if (!potentialStylesheet.isPresent()) {
            log.error("Couldn't find stylesheet");
            return;
        }
        URL stylesheet = potentialStylesheet.get();
        if (!potentialResource.isPresent()) {
            log.error("Couldn't find resource");
            return;
        }
        if (!RegisteredResource.PROFILE.exists()) {
            ProfileManager.getInstance().init();
        }
        URL resource = potentialResource.get();
        stylesheet = assignDarkThemeIfPossible(stylesheet); //this is bad tbh, whatever
        Scene scene = createScene(currentStage, stylesheet, resource);
        initScene(currentStage, scene);
        renderView("Dashboard", false);
        TaskManager.getInstance().init();
    }


    private Scene createScene(Stage currentStage, URL stylesheet, URL resource) throws IOException {
        currentStage.initStyle(StageStyle.UNDECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setController(this);
        Parent root = fxmlLoader.load();
        parent = root;
        root.getStylesheets().add(stylesheet.toExternalForm());
        return new Scene(root);
    }

    private URL assignDarkThemeIfPossible(URL stylesheet) {
        Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
        if (potentialProfile.isPresent()) {
            Profile profile = potentialProfile.get();
            if (profile.getTheme() == Theme.DARK) {
                log.info("Initialising dark theme..");
                Optional<URL> potentialDarkStylesheet = ResourceLoader.getCSS(Theme.DARK.getName());
                if (potentialDarkStylesheet.isPresent()) {
                    stylesheet = potentialDarkStylesheet.get();
                }
            }
        }
        return stylesheet;
    }

    private void initScene(Stage currentStage, Scene scene) {
        currentStage.setScene(scene);
        currentStage.setResizable(true);
        currentStage.setTitle("Liquor - the professional all in one networking tool");
        currentStage.show();
        currentStage.centerOnScreen();
        SplashScreen.FRAME.setVisible(false);
    }


    public void init() {
    }

    public static void main(String[] args) {
        //Privileges.setProperty("javafx.preloader", SplashScreen.class.getCanonicalName());
        launch(args);
    }


}
