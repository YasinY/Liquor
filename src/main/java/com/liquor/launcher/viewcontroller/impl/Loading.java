package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.annotations.Native;
import com.liquor.launcher.viewcontroller.ViewController;
import com.liquor.resourcemanagement.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

@Native
@Slf4j
public class Loading extends ViewController {


    @FXML
    private ImageView loadingImage;

    @FXML
    private ProgressBar progressBar;

    @Override
    public void load() {
        Optional<URL> potentialImage = ResourceLoader.getGIF("loading_bar");
        if (potentialImage.isPresent()) {
            URL image = potentialImage.get();
            loadingImage.setImage(new Image(image.toExternalForm()));
        }
        log.info("Loading image..");
    }
}
