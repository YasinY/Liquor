package com.liquor.launcher;

import com.liquor.resourcemanagement.ResourceLoader;
import javafx.application.Application;
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

import java.net.URL;
import java.util.Optional;

@Slf4j
public class Liquor extends Application {

    @FXML
    private WebView webView;


    @FXML
    public void handleTransition(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button) {
            Button clickedButton = (Button) actionEvent.getSource();
            String buttonName = clickedButton.getText();
            renderView(buttonName);
        }
    }

    public void renderView(String viewName) {
        Optional<URL> url = ResourceLoader.getHTML(viewName, Liquor.class);
        url.ifPresent(consumer -> {
            webView.getEngine().load(consumer.toExternalForm());

        });
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
