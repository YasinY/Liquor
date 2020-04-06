package com.liquor.launcher;

import com.liquor.launcher.exceptions.ControllerNotFoundException;
import com.liquor.launcher.viewcontroller.IViewController;
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
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class Liquor extends Application {


    @FXML
    private WebView webView;

    @FXML
    public void handleTransition(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            System.out.println("CLick!");
            Button clickedButton = (Button) actionEvent.getSource();
            String buttonName = clickedButton.getText();
            renderView(buttonName);
        }
    }

    public void renderView(String viewName) {
        Optional<URL> url = ResourceLoader.getHTML(viewName, Liquor.class);
        url.ifPresent(consumer -> {
                initialiseWebView(viewName, consumer);
        });
        System.out.println("Rendering view: " + viewName);
    }

    private synchronized void initialiseWebView(String viewName, URL consumer) {
        webView.getEngine().load(consumer.toExternalForm());
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    initialiseViewController(viewName);
                    webView.getEngine().getLoadWorker().stateProperty().removeListener(this);
                }
            }
        });
    }

    private void initialiseViewController(String viewName) {
        try {
            IViewController viewController = ViewControllerFactory.produceViewController(viewName, webView.getEngine().getDocument());
            viewController.initAction();
        } catch (ControllerNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage currentStage) throws Exception {
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
