package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.viewcontroller.ViewController;
import org.w3c.dom.Document;

public class Cisco extends ViewController {


    public Cisco(Document document) {
        super(document);
    }

    @Override
    public void initAction() {
        System.out.println("Initialised Cisco");
    }
}
