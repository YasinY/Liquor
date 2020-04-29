package com.liquor.resourcemanagement;

import com.liquor.launcher.Liquor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

/**
 * TODO USE ENUMS FOR THE EXTENSIONS AND SHIT !
 */
@Slf4j
public class ResourceLoader {


    private static Optional<URL> getResource(String name, String extension, Class context) {
        String completeName = name + "." + extension;
        return Optional.ofNullable(context.getResource(completeName));
    }


    public static Optional<URL> getFXML(String name, Class context) {
        return getResource(name, "fxml", context);
    }

    public static Optional<URL> getFXML(String name) {
        return getResource(name, "fxml", Liquor.class);
    }

    public static Optional<URL> getHTML(String name, Class context) {
        return getResource(name, "html", context);
    }

    public static Optional<URL> getHTML(String name) {
        return getResource(name, "html", Liquor.class);
    }

    public static Optional<URL> getCSS(String name, Class context) {
        return getResource(name, "css", context);
    }

    public static Optional<URL> getCSS(String name) {
        return getResource(name, "css", Liquor.class);
    }

    public static Optional<URL> getImage(String name, Class context) {
        return getResource(name, "png", context);
    }

    public static Optional<URL> getImage(String name) {
        return getResource(name, "png", Liquor.class);
    }

    public static Optional<URL> getGIF(String name) {
        return getResource(name, "gif", Liquor.class);
    }

}
