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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class OpenVPNResource {

    static String OS = System.getProperty("os.name");
    static String OS_VERSION = System.getProperty("os.version");
    static String OPENVPN_PATH = String.format("openvpn/windows%s/", OS_VERSION.contains("10") ? "10" : OS_VERSION.contains("8") ? "8" : "7");


    public static void checkOpenVPN() {
        //check if openvpn is in path variable TODO
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
        new JFXPanel();
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
    public static void exportAuth() {
        String path = String.format("%s%s%s", OPENVPN_PATH, "config/", "perfectprivacy/");
        URI uri = Liquor.class.getResource(path).toURI();
        try (FileSystem fileSystem = (uri.getScheme().equals("jar") ? FileSystems.newFileSystem(uri, Collections.emptyMap()) : null)) {
            Path myPath = Paths.get(uri);
            Files.walkFileTree(myPath, new SimpleFileVisitor<Path>() {
                @SneakyThrows
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.toString();
                    if (fileName.endsWith(".ovpn")) {
                        final String fullDirectoryPath = RegisteredResource.AUTH.getFullDirectoryPath();
                        Path destination = Paths.get(fullDirectoryPath  + file.getFileName().toString());
                        Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
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
