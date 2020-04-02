package com.liquor.resourcemanagement;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

@Slf4j
public class ResourceLoader {


    private static Optional<URL> getResource(String name, String extension, Class context) {
        String completeName = name + "." + extension;
        return Optional.ofNullable(context.getResource(completeName));
    }

    public static Optional<URL> getFXML(String name, Class context) {
        return getResource(name, "fxml", context);
    }

    public static Optional<URL> getCSS(String name, Class context) {
        return getResource(name, "css", context);
    }

    public static Optional<URL> getImage(String name, Class context) {
        return getResource(name, "png", context);
    }

}
