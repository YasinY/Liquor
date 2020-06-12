package com.liquor.launcher.viewcontroller;

import com.liquor.launcher.viewcontroller.impl.Default;
import javafx.scene.web.WebEngine;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Slf4j
public class ViewControllerFactory {


    public static IViewController produceViewController(String name, WebEngine document) {
        Optional<RegisteredController> registeredController = RegisteredController.find(name);
        if (!registeredController.isPresent()) {
            log.error("Could not find registered view controller " + name);
            return new Default();
        }
        return produceViewController(registeredController.get(), document);
    }

    public static IViewController produceViewController(String name) {
        Optional<RegisteredController> registeredController = RegisteredController.find(name);
        if (!registeredController.isPresent()) {
            log.error("Could not find registered view controller " + name);
            return new Default();
        }
        return produceViewController(registeredController.get());
    }

    private static IViewController produceViewController(RegisteredController controller) {
        try {
            Constructor<?> constructor = controller.getReferencedClass().getConstructor();

            return (IViewController) constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return new Default();
    }

    private static IViewController produceViewController(RegisteredController controller, WebEngine document) {
        try {
            Constructor<?> constructor = controller.getReferencedClass().getConstructor(WebEngine.class);

            return (IViewController) constructor.newInstance(document);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return new Default();
    }
}
