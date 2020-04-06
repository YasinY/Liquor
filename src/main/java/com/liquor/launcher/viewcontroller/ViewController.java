package com.liquor.launcher.viewcontroller;

import org.w3c.dom.Document;

public class ViewController implements IViewController {

    protected Document document;

    public ViewController(Document document) {
        this.document = document;
    }

    @Override
    public void load() {

    }
}
