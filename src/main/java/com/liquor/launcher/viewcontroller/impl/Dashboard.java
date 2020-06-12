package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.viewcontroller.ViewController;
import javafx.scene.web.WebEngine;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

@Slf4j
public class Dashboard extends ViewController {


    public Dashboard(WebEngine webEngine) {
        super(webEngine);
    }

    @Override
    public void load() {
      log.info("Dashboard action taken");

    }
}
