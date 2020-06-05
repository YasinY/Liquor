package com.liquor.launcher.splashscreen;

import com.liquor.launcher.Liquor;
import com.liquor.launcher.annotations.Native;
import com.liquor.resourcemanagement.ResourceLoader;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Native
@Slf4j
public class SplashScreen extends Preloader {


    @FXML
    private ImageView loadingImage;

    @FXML
    private ProgressBar progressBar;


    public static Stage CURRENT_SPLASHSCREEN;

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", SplashScreen.class.getCanonicalName());
        launch(Liquor.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Optional<URL> potentialResource = ResourceLoader.getFXML("splashscreen", SplashScreen.class);
        if (potentialResource.isPresent()) {
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            FXMLLoader loader = new FXMLLoader(potentialResource.get());
            loader.setController(this);
            Parent parent = loader.load();
            parent.setStyle("-fx-background-color:  transparent;");
            Scene scene = new Scene(parent);
            scene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            SplashScreen.CURRENT_SPLASHSCREEN = primaryStage;
        }
        loadNodes();
        log.info("Loading splashscreen..");
    }

    public void loadNodes() {
        Optional<URL> potentialImage = ResourceLoader.getGIF("loading_bar");
        if (potentialImage.isPresent()) {
            URL image = potentialImage.get();
            loadingImage.setImage(new Image(image.toExternalForm(), true));
            //fillBar();
        }
        log.info("SplashScreen image..");
    }


    @Override
    public void handleApplicationNotification(PreloaderNotification arg0) {
        if (arg0 instanceof ProgressNotification) {
            ProgressNotification pn = (ProgressNotification) arg0;
            progressBar.setProgress(pn.getProgress());
            System.out.println("Progress " + progressBar.getProgress());
        }
    }

    @SneakyThrows
    @Override
    public void handleStateChangeNotification(StateChangeNotification notification) {
        Thread.sleep(300);
        switch (notification.getType()) {
            case BEFORE_START:
                break;

            case BEFORE_LOAD:
            case BEFORE_INIT:
                break;
        }

    }

}
