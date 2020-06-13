package com.liquor.launcher.splashscreen;

import com.liquor.launcher.Liquor;
import com.liquor.launcher.annotations.Native;
import com.liquor.prerequisites.openvpn.OpenVPNResource;
import com.liquor.resourcemanagement.FileSystem;
import com.liquor.resourcemanagement.ResourceLoader;
import com.liquor.resourcemanagement.registered.RegisteredResource;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

@Native
@Slf4j
public class SplashScreen {

    /**
     * The label the image is being displayed at
     */
    private JLabel label = new JLabel();
    /**
     * The frame the jlabel will be located at
     */
    public static JFrame FRAME = new JFrame();
    /**
     * The image being displayed
     */
    private ImageIcon image = null;
    /**
     * The dimension of the screen
     */
    private final Dimension dimension = Toolkit.getDefaultToolkit()
            .getScreenSize();

    /**
     * Displays the splash screen
     * {@code image is being loaded from the string parameter,
     * JFrame is used to display the JLabel and the Jlabel the image.
     * First we're setting the properties of both the image and frame. Then
     * we're letting the current thread sleep for X {@value seconds}, after that
     * the frame is gonna dissappear and the object is able to get collected
     * by the gc}
     */
    public void showSplash() {
        FileSystem.ensureDirectories(RegisteredResource.AUTH);
        OpenVPNResource.exportConfigurations(false);
        Optional<URL> projectGif = ResourceLoader.getGIF("project");
        if (projectGif.isPresent()) {
            try {
                image = new ImageIcon(projectGif.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            FRAME.setUndecorated(true);
            label.setIcon(image);
            FRAME.setSize(image.getIconWidth(), image.getIconHeight());
            FRAME.setLocationRelativeTo(null); //Thanks to king fox!
            FRAME.add(label);
            FRAME.setVisible(true);
            Liquor.main(null);
        }
    }

}