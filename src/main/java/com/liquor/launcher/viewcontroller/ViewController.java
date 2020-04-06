package com.liquor.launcher.viewcontroller;

import javafx.scene.web.WebView;

public abstract class ViewController implements IViewController {

    protected WebView webView;

    public ViewController(WebView webView) {
        this.webView = webView;
    }


}
