package com.liquor.resourcemanagement;

import com.liquor.launcher.Liquor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static Optional<InputStream> getResourceStream(String name, String extension, Class context) {
        String completeName = name + "." + extension;
        return Optional.ofNullable(context.getResourceAsStream(completeName));
    }

    private static Optional<InputStream> getRelativeResourceAsStream(String name, String extension, Class context) {
        String completeName = name + "." + extension;
        return Optional.ofNullable(context.getClassLoader().getResourceAsStream(completeName));
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

    public static void extractOpenVPN() {
        log.info("Extracting openvpn...");
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        if (os.contains("Windows")) {
            String path = String.format("openvpn/windows%s/", version.contains("10") ? "10" : version.contains("8") ? "8" : "7");
            Optional<InputStream> potentialExe = getResourceStream(path + "openvpn", "exe", Liquor.class);
            log.info("OS is windows, exe is present? " + potentialExe.isPresent());
            log.info("Path: " + path + "openvpn.exe");
            if (potentialExe.isPresent()) {
                try (InputStream exeFile = potentialExe.get()) {
                    OutputStream exeOutputStream = new FileOutputStream("openvpn.exe");
                    byte[] bytesPerIteration = new byte[2048];
                    int length;
                    while ((length = exeFile.read(bytesPerIteration)) != -1) {
                        exeOutputStream.write(bytesPerIteration, 0, length);
                    }
                    exeOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
