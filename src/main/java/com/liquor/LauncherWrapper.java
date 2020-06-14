package com.liquor;

import com.liquor.launcher.splashscreen.SplashScreen;
import com.liquor.prerequisites.openvpn.OpenVPNResource;

public class LauncherWrapper {

    public static void main(String[] args) {

        OpenVPNResource.checkOpenVPN();
        new SplashScreen().showSplash();
    }

}
