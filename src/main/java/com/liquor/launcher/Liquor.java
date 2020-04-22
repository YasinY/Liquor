package com.liquor.launcher;

import com.liquor.launcher.annotations.Native;
import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.viewcontroller.IViewController;
import com.liquor.launcher.viewcontroller.ViewControllerFactory;
import com.liquor.resourcemanagement.FileSystem;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class Liquor extends Application {

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane nativeView;

    @FXML
    public void handleTransition(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            Button clickedButton = (Button) actionEvent.getSource();
            String viewName = clickedButton.getText();
            renderView(viewName, viewName.equalsIgnoreCase("terminal"));
        }
    }

    public void renderView(String viewName, boolean nativeView) {
        if (nativeView) {
            log.info("Attempting to render native view..");
            associateController(viewName);
            return;
        }
        log.info("Attempting to render web view..");
        initialiseWebView(viewName);
    }

    private void initialiseWebView(String viewName) {
        Optional<URL> url = ResourceLoader.getHTML(viewName, Liquor.class);
        url.ifPresent(consumer -> {
            setVisibilities();
            initialiseWebView(viewName, consumer);
        });
    }

    private void setVisibilities() {
        log.info("Ensuring visibility ..");
        if (this.nativeView.isVisible()) {
            this.nativeView.setVisible(false);
        }
        if (!webView.isVisible()) {
            webView.setVisible(true);
        }
    }

    private synchronized void initialiseWebView(String viewName, URL consumer) {
        log.info("Loading view..");
        webView.getEngine().load(consumer.toExternalForm());
        log.info("Adding listener..");
        webView.getEngine().getLoadWorker().stateProperty().addListener(getChangedListener(viewName));
    }

    private ChangeListener<Object> getChangedListener(String viewName) {
        return new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    log.info("Initialising controller..");
                    initialiseViewController(viewName);
                    webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
                }
            }
        };
    }

    private void initialiseViewController(String viewName) {
        log.info("Loading view controller..");
        IViewController viewController = ViewControllerFactory.produceViewController(viewName, webView.getEngine().getDocument());
        if (viewController.getClass().isAnnotationPresent(Native.class)) {
            log.error("Aborted loading controller for " + viewName + " as it has been marked as native.");
            return;
        }
        viewController.load();
    }

    private void associateController(String viewName) {
        log.info("Finding controller..");
        IViewController registeredController = ViewControllerFactory.produceViewController(viewName);
        Optional<URL> potentialFxml = ResourceLoader.getFXML(viewName, registeredController.getClass());
        log.info("Trying to find fxml file..");
        if (potentialFxml.isPresent()) {
            URL fxml = potentialFxml.get();
            try {
                log.info("Loading fxml file-content..");
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
            IViewController viewController = loader.getController();
            viewController.load();
        }
    }

    private void appendNativeWindow(AnchorPane loadedContent) {
        webView.setVisible(false);
        this.nativeView.getChildren().setAll(loadedContent.getChildren());
        this.nativeView.setVisible(true);
    }


    @Override
    public void start(Stage currentStage) throws IOException {
        ProfileManager.getInstance().load();
        System.out.println(ProfileManager.getInstance().getSelectedProfile().toString());
        log.info("Starting application.. ");
        startup(currentStage);
    }

    private void startup(Stage currentStage) throws IOException {
        Optional<URL> resource = ResourceLoader.getFXML("launcher", Liquor.class);
        Optional<URL> stylesheet = ResourceLoader.getCSS("launcher", Liquor.class);
        if (resource.isPresent() && stylesheet.isPresent()) {
            currentStage.initStyle(StageStyle.UNDECORATED);
            FXMLLoader fxmlLoader = new FXMLLoader(resource.get());
            fxmlLoader.setController(this);
            Parent root = fxmlLoader.load();
            root.getStylesheets().add(stylesheet.get().toExternalForm());
            Scene scene = new Scene(root);
            initScene(currentStage, scene);
            renderView("Dashboard", false);
        }
    }

    private void initScene(Stage currentStage, Scene scene) {
        currentStage.setScene(scene);
        currentStage.setResizable(false);
        currentStage.setTitle("Liquor - the professional all in one networking tool");
        currentStage.show();
        currentStage.centerOnScreen();
    }

    public void init() {

    }

    public static void main(String[] args) {
        //System.setProperty("javafx.preloader", SplashScreen.class.getCanonicalName());
        launch(args);
    }

}
