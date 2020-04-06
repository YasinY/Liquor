package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.viewcontroller.ViewController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Dashboard extends ViewController {


    public Dashboard(Document document) {
        super(document);
    }

    @Override
    public void initAction() {
        Element element = document.createElement("button");
        element.setTextContent("LOLOLOLOLOL");
       document.getChildNodes().item(1).appendChild(element);
       System.out.println("Initialised Dashboard");
    }
}
