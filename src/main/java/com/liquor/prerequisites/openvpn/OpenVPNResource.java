package com.liquor.prerequisites.openvpn;

import com.liquor.launcher.Liquor;
import com.liquor.resourcemanagement.ResourceLoader;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class OpenVPNResource {

    static String OS = System.getProperty("os.name");
    static String OS_VERSION = System.getProperty("os.version");
    public static String OPENVPN_PATH = String.format("openvpn/windows%s/", OS_VERSION.contains("10") ? "10" : OS_VERSION.contains("8") ? "8" : "7");
    public static String OPENVPN_CONFIG_PATH = String.format("%s%s%s", OPENVPN_PATH, "config/", "perfectprivacy/");

    public static void checkOpenVPN() {
        boolean existsInPath = Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> Files.exists(path.resolve("openvpn")));
        if (!existsInPath) {
            extractOpenVPN();
        }


    }

    public static void extractOpenVPN() {
        log.info("Extracting openvpn...");
        if (OS.contains("Windows")) {
            String path = String.format("openvpn/windows%s/", OS_VERSION.contains("10") ? "10" : OS_VERSION.contains("8") ? "8" : "7");
            Optional<InputStream> potentialExe = ResourceLoader.getResourceStream(path + "openvpn", "exe", Liquor.class);
            log.info("OS is windows, exe is present? " + potentialExe.isPresent());
            if (potentialExe.isPresent()) {
                InputStream exeFile = potentialExe.get();
                displayAlert(exeFile);
            }
        }
    }


    private static void displayAlert(InputStream exeFile) {
        new JFXPanel(); //initialises
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("OpenVPN has to be installed for the VPN feature to function properly. Would you like to install it now?");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    exportFile(exeFile, "openvpn.exe");
                    ResourceLoader.openFile("openvpn", "exe");
                } else {
                    alert.close();
                }
            });
        });
    }

    @SneakyThrows
    public static void exportConfigurations(boolean validate) {
        URI uri = Liquor.class.getResource(OPENVPN_CONFIG_PATH).toURI();
        try (FileSystem fileSystem = (uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Collections.emptyMap()) : null)) {
            Path myPath = Paths.get(uri);
            Files.walkFileTree(myPath, replaceBehaviour());
        }
    }

    private static SimpleFileVisitor<Path> validateBehaviour() {
        return new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                return FileVisitResult.CONTINUE;
            }
        };
    }

    private static SimpleFileVisitor<Path> replaceBehaviour() {
        return new SimpleFileVisitor<Path>() {
            @SneakyThrows
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String fileName = file.toString();
                if (fileName.endsWith(".ovpn") || fileName.endsWith(".txt")) {
                    final String fullDirectoryPath = RegisteredResource.AUTH.getFullDirectoryPath();
                    Path destination = Paths.get(fullDirectoryPath + file.getFileName().toString());
                    Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                return FileVisitResult.CONTINUE;
            }
        };
    }


    private static void exportFile(InputStream inputFile, String outputName) {
        try {
            OutputStream outputExe = new FileOutputStream(outputName);
            byte[] bytesPerIteration = new byte[2048];
            int length;
            while ((length = inputFile.read(bytesPerIteration)) != -1) {
                outputExe.write(bytesPerIteration, 0, length);
            }
            inputFile.close();
            outputExe.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
