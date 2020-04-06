package com.liquor.launcher.viewcontroller;

import com.liquor.launcher.exceptions.ControllerNotFoundException;

import java.util.Optional;

public class ViewControllerFactory {


    public static IViewController produceViewController(String name) throws ControllerNotFoundException {
        Optional<RegisteredController> registeredController = RegisteredController.find(name);
        if (!registeredController.isPresent()) {
            throw new ControllerNotFoundException("Could not find controller");
        }
        return produceViewController(registeredController.get());
    }

    public static IViewController produceViewController(RegisteredController controller) {

        return null;
    }
}
