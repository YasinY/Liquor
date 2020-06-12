package com.liquor;

import com.liquor.launcher.security.Decrypter;
import com.liquor.launcher.splashscreen.SplashScreen;
import com.liquor.launcher.viewcontroller.impl.VPN;

import java.io.IOException;

public class LauncherWrapper {

    public static void main(String[] args) throws IOException {
        Decrypter decrypter = new Decrypter();
        decrypter.encryptConfigs();
        //VPN.establishConnection();
        //OpenVPNResource.checkOpenVPN();
        //System.exit(0);
        new SplashScreen().showSplash();
    }

}
