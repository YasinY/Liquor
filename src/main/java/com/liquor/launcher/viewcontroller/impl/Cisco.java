package com.liquor.launcher.viewcontroller.impl;

import com.liquor.launcher.cisco.CiscoCommandParser;
import com.liquor.launcher.model.CiscoCommand;
import com.liquor.launcher.viewcontroller.ViewController;
import com.sun.webkit.dom.HTMLAnchorElementImpl;
import javafx.scene.web.WebEngine;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLElement;

import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
public class Cisco extends ViewController {

    private String selectedCommand;

    public Cisco(WebEngine webEngine) {
        super(webEngine);
    }

    @Override
    public void load() {
        HTMLDivElement commandsContainer = (HTMLDivElement) document.getElementById("commandsContainer");
        HTMLAnchorElementImpl citySample = (HTMLAnchorElementImpl) document.getElementById("sampleCommand");
        fillCommands(commandsContainer, citySample);
        addFunctionality();
        log.info("Cisco action taken");


    }

    private void addFunctionality() {
        NodeList commands = document.getElementById("commandsContainer").getElementsByTagName("a");
        IntStream.range(0, commands.getLength()).forEach(index -> {
            HTMLAnchorElementImpl element = (HTMLAnchorElementImpl) commands.item(index);
            addCityClickEvent(element, commands, index);
        });
    }

    private void addCityClickEvent(HTMLAnchorElementImpl element, NodeList commands, int elementIndex) {
        ((EventTarget) element).addEventListener("click", (cityEvent) -> {
            IntStream.range(0, commands.getLength()).forEach(index -> {
                HTMLAnchorElementImpl otherCommand = (HTMLAnchorElementImpl) commands.item(index);
                if (index == elementIndex) {
                    return;
                }
                otherCommand.setClassName(otherCommand.getClassName().replace("active", ""));
            });
            this.selectedCommand = element.getText();
            element.setClassName(element.getClassName() + " active");
            HTMLElement commandName = (HTMLElement) document.getElementById("commandName");
            HTMLElement commandDescription = (HTMLElement) document.getElementById("commandDescription");
            Optional<CiscoCommand> command = CiscoCommandParser.CISCO_COMMANDS.stream().filter(ciscoCommand -> ciscoCommand.getName().equalsIgnoreCase(selectedCommand)).findFirst();
            command.ifPresent(ciscoCommand -> {
                commandName.setTextContent(ciscoCommand.getName());
                commandDescription.setTextContent(ciscoCommand.getDescription());
            });
        }, false);
    }

    private void fillCommands(HTMLDivElement commandsContainer, HTMLAnchorElementImpl citySample) {
        CiscoCommandParser.CISCO_COMMANDS.forEach(command -> {
            HTMLAnchorElementImpl clone = (HTMLAnchorElementImpl) citySample.cloneNode(true);
            clone.setClassName(clone.getClassName().replace("d-none", ""));
            clone.setTextContent(command.getName());
            commandsContainer.appendChild(clone);
        });
    }
}
