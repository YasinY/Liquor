package com.liquor.resourcemanagement;

import com.liquor.launcher.Liquor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * TODO USE ENUMS FOR THE EXTENSIONS AND SHIT !
 */
@Slf4j
public class ResourceLoader {


    /**
     * starts from /resources/com/liquor/launcher
     * @param directories
     * @return
     */
    public static Optional<URL> getResourceDirectory(String ... directories) {
        String result = Arrays
                .stream(directories)
                .map(element -> element + System.lineSeparator())
                .collect(Collectors.joining());
        System.out.println("result: " + result);
        return Optional.ofNullable(Liquor.class.getResource(result));
    }
    public static Optional<URL> getResourceFile(String name, String extension, Class context) {
        String completeName = name + "." + extension;
        System.out.println("Getting resource file " + completeName + " on context " + context.getName());
        return Optional.of(context.getResource(completeName));
    }

    public static Optional<InputStream> getResourceStream(String name, String extension, Class context) {
        String completeName = name + "." + extension;
        return Optional.ofNullable(context.getResourceAsStream(completeName));
    }


    public static Optional<URL> getFXML(String name, Class context) {
        return getResourceFile(name, "fxml", context);
    }

    public static Optional<URL> getMP4(String name, Class context) {
        return getResourceFile(name, "mp4", context);
    }

    public static Optional<URL> getFXML(String name) {
        return getResourceFile(name, "fxml", Liquor.class);
    }

    public static Optional<URL> getHTML(String name, Class context) {
        return getResourceFile(name, "html", context);
    }

    public static Optional<URL> getHTML(String name) {
        log.info("Retrieving html " + name + "");
        return getResourceFile(name, "html", Liquor.class);
    }

    public static Optional<URL> getCSS(String name, Class context) {
        return getResourceFile(name, "css", context);
    }

    public static Optional<URL> getCSS(String name) {
        return getResourceFile(name, "css", Liquor.class);
    }

    public static Optional<URL> getImage(String name, Class context) {
        return getResourceFile(name, "png", context);
    }

    public static Optional<URL> getImage(String name) {
        return getResourceFile(name, "png", Liquor.class);
    }

    public static Optional<URL> getGIF(String name) {
        return getResourceFile(name, "gif", Liquor.class);
    }


    public static void openFile(String name, String extension) {
        try {
            Desktop.getDesktop().open(new File(String.format("%s.%s", name, extension)));
        } catch (Exception e) {
            // silence is golden
        }
    }

}
