package com.liquor.prerequisites.openvpn;

import com.liquor.launcher.Liquor;
import com.liquor.resourcemanagement.ResourceLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

@Slf4j
public class OpenVPNResource {

    String os = System.getProperty("os.name");
    String version = System.getProperty("os.version");



    public static void checkOpenVPN() {

    }

    public static void extractOpenVPN() {
        log.info("Extracting openvpn...");
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        if (os.contains("Windows")) {
            String path = String.format("openvpn/windows%s/", version.contains("10") ? "10" : version.contains("8") ? "8" : "7");
            Optional<InputStream> potentialExe = ResourceLoader.getResourceStream(path + "openvpn", "exe", Liquor.class);
            log.info("OS is windows, exe is present? " + potentialExe.isPresent());
            if (potentialExe.isPresent()) {
                InputStream exeFile = potentialExe.get();
                displayAlert(exeFile);
            }
        }
    }


    private static void displayAlert(InputStream exeFile) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("OpenVPN has to be installed for the VPN feature to function properly. Would you like to install it now?");
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                exportAndRunExe(exeFile);
            } else {
                alert.close();
            }
        });
    }

    private static void exportAndRunExe(InputStream exeFile) {
        try {
            OutputStream exeOutputStream = new FileOutputStream("openvpn.exe");
            byte[] bytesPerIteration = new byte[2048];
            int length;
            while ((length = exeFile.read(bytesPerIteration)) != -1) {
                exeOutputStream.write(bytesPerIteration, 0, length);
            }
            exeFile.close();
            exeOutputStream.close();
            ResourceLoader.openFile("openvpn", "exe");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
