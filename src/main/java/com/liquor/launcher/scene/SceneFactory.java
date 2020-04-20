package com.liquor.launcher.scene;

import com.liquor.launcher.viewcontroller.RegisteredController;
import com.liquor.resourcemanagement.ResourceLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class SceneFactory {

    public static Optional<Scene> produceScene(String name) {
        Optional<RegisteredController> scene = RegisteredController.find(name);
        if (scene.isPresent()) {
            RegisteredController availableScene = scene.get();
            return produce(availableScene);
        }
        return Optional.empty();
    }

    private static Optional<Scene> produce(RegisteredController availableScene) {
        Optional<URL> fxml = ResourceLoader.getFXML(availableScene.name().toLowerCase(), availableScene.getReferencedClass());
        if (fxml.isPresent()) {
            FXMLLoader fxmlLoader = new FXMLLoader(fxml.get());
            try {
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                return Optional.of(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

}
