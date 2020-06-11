package com.liquor;

import com.liquor.launcher.Liquor;
import com.liquor.launcher.security.Decrypter;
import com.liquor.launcher.splashscreen.SplashScreen;

public class LauncherWrapper {

    public static void main(String[] args)  {
        Decrypter decrypter = new Decrypter();
        decrypter.encryptConfigs();
        new SplashScreen().showSplash();
    }

}
