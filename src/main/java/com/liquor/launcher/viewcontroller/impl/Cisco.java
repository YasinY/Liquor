package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.viewcontroller.ViewController;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

@Slf4j
public class Cisco extends ViewController {

    public Cisco(Document document) {
        super(document);
    }

    @Override
    public void load() {
        log.info("Cisco action taken");
    }
}
