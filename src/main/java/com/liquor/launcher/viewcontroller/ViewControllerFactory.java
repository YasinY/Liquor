package com.liquor.launcher.viewcontroller;

import com.liquor.launcher.exceptions.ControllerNotFoundException;
import org.w3c.dom.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ViewControllerFactory {


    public static IViewController produceViewController(String name, Document document)
            throws ControllerNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Optional<RegisteredController> registeredController = RegisteredController.find(name);
        if (!registeredController.isPresent()) {
            throw new ControllerNotFoundException("Could not find controller");
        }
        return produceViewController(registeredController.get(), document);
    }

    public static IViewController produceViewController(RegisteredController controller, Document document)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Constructor<?> constructor = controller.getReferencedClass().getConstructor(Document.class);
        return (IViewController) constructor.newInstance(document);
    }
}
