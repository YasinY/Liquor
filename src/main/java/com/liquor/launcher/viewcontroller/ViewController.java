package com.liquor.launcher.viewcontroller;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

public class ViewController implements IViewController {

    protected Document document;

    public ViewController() {

    }

    public ViewController(Document document) {
        this.document = document;
    }

    @Override
    public void load() {

    }
}
