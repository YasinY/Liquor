package com.liquor;

import com.liquor.launcher.security.Decrypter;
import com.liquor.launcher.splashscreen.SplashScreen;
import com.liquor.launcher.viewcontroller.impl.VPN;
import com.liquor.prerequisites.openvpn.OpenVPNResource;

import java.io.IOException;

public class LauncherWrapper {

    public static void main(String[] args) throws IOException {

        OpenVPNResource.checkOpenVPN();
        new SplashScreen().showSplash();
    }

}
