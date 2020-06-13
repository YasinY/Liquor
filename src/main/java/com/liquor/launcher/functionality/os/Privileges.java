package com.liquor.launcher.functionality.os;

import com.sun.security.auth.module.NTSystem;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.stream.Stream;

public class Privileges {



    public String openVPNPath() {
        return "setx path \"%path\";\"C:\\Program Files\\OpenVPN\\bin\"";
    }

    public static boolean canInstall() {
        File installDir = new File(System.getenv("ProgramFiles"));
        if (!installDir.canWrite()) {
            return false;
        }
        if (!installDir.isDirectory()) {
            return false;
        }
        File fileTest = null;
        try {
            // we use the .dll suffix to properly test on Vista virtual directories
            // on Vista you are not allowed to write executable files on virtual directories like "Program Files"
            fileTest = File.createTempFile("writableArea", ".dll", installDir);
        } catch (IOException e) {
            //If an exception occured while trying to create the file, it means that it is not writable
            return false;
        } finally {
            if (fileTest != null) {
                boolean success = fileTest.delete();
            }
        }
        return true;
    }
}
