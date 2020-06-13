package com.liquor;

import com.liquor.launcher.splashscreen.SplashScreen;
import com.liquor.prerequisites.openvpn.OpenVPNResource;
import com.vnetpublishing.java.suapp.SU;
import com.vnetpublishing.java.suapp.SuperUserApplication;

public class LauncherWrapper extends SuperUserApplication {

    public static void main(String[] args) {

        SU.run(new LauncherWrapper(), args);
    }

    @Override
    public int run(String[] strings) {
        OpenVPNResource.checkOpenVPN();
        new SplashScreen().showSplash();
        return 0;
    }
}
