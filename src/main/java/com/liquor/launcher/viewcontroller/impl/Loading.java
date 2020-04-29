package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.annotations.Native;
import com.liquor.launcher.viewcontroller.ViewController;
import com.liquor.resourcemanagement.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Native
@Slf4j
public class Loading extends ViewController {


    @FXML
    private ImageView loadingImage;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    @Override
    public void load() {
        Optional<URL> potentialImage = ResourceLoader.getGIF("loading_bar");
        if (potentialImage.isPresent()) {
            URL image = potentialImage.get();
            loadingImage.setImage(new Image(image.toExternalForm()));
            fillBar();
        }
        log.info("Loading image..");
    }

    private void fillBar() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int amount = 100;
            int cycle = 1;

            @Override
            public void run() {
                if(amount <= 1 && cycle > 6) {
                    this.cancel();
                    log.info("Stopped bar filling task!");
                    return;
                }
                int randomAmount = new Random().nextInt(amount);
                final double value = (double) randomAmount / 100;
                progressBar.setProgress(progressBar.getProgress() + value);
                progressIndicator.setProgress(progressBar.getProgress());
                amount = amount - randomAmount;
                cycle++;
            }
        }, 0, 400);
    }
}
