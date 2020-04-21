package com.liquor.launcher.viewcontroller.impl;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import com.liquor.launcher.viewcontroller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;

public class Terminal extends ViewController {

    @FXML
    private TabPane tabPane;


    @Override
    public void load() {
        TerminalConfig darkConfig = new TerminalConfig();
        darkConfig.setBackgroundColor(Color.rgb(16, 16, 16));
        darkConfig.setForegroundColor(Color.rgb(240, 240, 240));
        darkConfig.setCursorColor(Color.rgb(255, 0, 0, 0.5));

        TerminalBuilder terminalBuilder = new TerminalBuilder(darkConfig);
        TerminalTab terminal = terminalBuilder.newTerminal();
        this.tabPane.getTabs().add(terminal);
    }
}
