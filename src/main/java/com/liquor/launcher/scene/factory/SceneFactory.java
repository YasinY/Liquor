package com.liquor.launcher.scene.factory;

import com.liquor.launcher.exceptions.MisconfiguredSceneException;
import com.liquor.launcher.exceptions.SceneNotFoundException;
import com.liquor.resourcemanagement.ResourceLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class SceneFactory {

    public static Optional<Scene> produceScene(String name) throws IOException, SceneNotFoundException, MisconfiguredSceneException {
        Optional<AvailableScene> scene = AvailableScene.parseScene(name);
        if (!scene.isPresent()) {
            throw new SceneNotFoundException("Could not find scene!");
        }
        AvailableScene availableScene = scene.get();

        return produce(availableScene);
    }

    public static Optional<Scene> produce(AvailableScene availableScene) throws IOException, MisconfiguredSceneException {
        Optional<URL> fxml = ResourceLoader.getFXML(availableScene.name().toLowerCase(), availableScene.getClazz());
        if(!fxml.isPresent()) {
          throw new MisconfiguredSceneException("Misconfigured scene");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxml.get());
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        return Optional.of(scene);

    }
}
