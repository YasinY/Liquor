package com.liquor.launcher;

import com.liquor.launcher.exceptions.ControllerNotFoundException;
import com.liquor.launcher.viewcontroller.IViewController;
import com.liquor.launcher.viewcontroller.RegisteredController;
import com.liquor.launcher.viewcontroller.ViewControllerFactory;
import com.liquor.resourcemanagement.ResourceLoader;
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
import java.lang.reflect.InvocationTargetException;
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
            String buttonName = clickedButton.getText();
            renderView(buttonName, buttonName.equalsIgnoreCase("terminal"));
        }
    }

    public void renderView(String viewName, boolean nativeView) {
        if (nativeView) {
            registerNativeView(viewName);
            return;
        }
        registerWebView(viewName);
    }

    private void registerWebView(String viewName) {
        Optional<URL> url = ResourceLoader.getHTML(viewName, Liquor.class);
        url.ifPresent(consumer -> {
            if (this.nativeView.isVisible()) {
                this.nativeView.setVisible(false);
            }
            if (!webView.isVisible()) {
                webView.setVisible(true);
            }
            log.info("Attempting to render view " + viewName + " ..");
            initialiseWebView(viewName, consumer);
        });
    }

    private void registerNativeView(String viewName) {
        Optional<RegisteredController> registeredController = RegisteredController.find(viewName);
        if (registeredController.isPresent()) {
            Optional<URL> fxml = ResourceLoader.getFXML(viewName, registeredController.get().getReferencedClass());
            if (fxml.isPresent()) {
                try {
                    webView.setVisible(false);
                    FXMLLoader loader = new FXMLLoader(fxml.get());
                    AnchorPane loadedContent = loader.load();
                    if (loader.getController() != null && loader.getController() instanceof IViewController) {
                        IViewController controller = loader.getController();
                        controller.load();
                    }
                    this.nativeView.getChildren().setAll(loadedContent.getChildren());
                    this.nativeView.setVisible(true);
                    //this.initialiseNativeViewController(viewName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawNativePane() {

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
                    log.info("Self-removing listener..");
                    webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
                }
            }
        };
    }

    private void initialiseNativeViewController(String viewName) {

        try {
            IViewController nativeViewController = ViewControllerFactory.produceViewController(viewName);

            nativeViewController.load();
        } catch (ControllerNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initialiseViewController(String viewName) {
        try {
            log.info("Loading controller..");
            IViewController viewController = ViewControllerFactory.produceViewController(viewName, webView.getEngine().getDocument());
            log.info("Loading controller action..");
            viewController.load();
        } catch (ControllerNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage currentStage) throws IOException {
        log.info("Starting application..");
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
