package com.liquor.launcher;

import com.liquor.resourcemanagement.ResourceLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Optional;

public class Liquor extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Optional<URL> resource = ResourceLoader.getFXML("launcher", Liquor.class);
        Optional<URL> stylesheet = ResourceLoader.getCSS("launcher", Liquor.class);
        if (resource.isPresent() && stylesheet.isPresent()) {
            primaryStage.initStyle(StageStyle.UNDECORATED);
            FXMLLoader fxmlLoader = new FXMLLoader(resource.get());
            fxmlLoader.setController(this);
            Parent root = fxmlLoader.load();
            root.getStylesheets().add(stylesheet.get().toExternalForm());
            Scene scene = new Scene(root);
            initScene(primaryStage, scene);
        }
    }

    private void initScene(Stage primaryStage, Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Liquor - the professional all in one networking tool");
        primaryStage.setHeight(500);
        primaryStage.setWidth(762);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public void init() {

    }

    public static void main(String[] args) {
        //System.setProperty("javafx.preloader", SplashScreen.class.getCanonicalName());
        launch(args);
    }

}
