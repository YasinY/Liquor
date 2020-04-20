package com.liquor.launcher.functionality.net.windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkManagerWindows {

    public static String NOT_SET = "NOT_SET";

    public static boolean isEnabled() {
        try {
            String state;
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh interface show interface \"Wi-Fi\"");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = r.readLine()) != null) {
                //line = r.readLine();
                if (line.contains("Administrative state")) {
                    state = line.split("\\s+")[3];
                    //System.out.println(state);
                    state = state.toLowerCase();
                    if (state.equals("enabled")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isConnected() {
        try {
            String state;
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh interface show interface \"Wi-Fi\"");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                if(line.contains("registered.") || line.contains("registriert.")) {
                    return false;
                }
                //line = r.readLine();
                if (line.contains("Connect state")) {
                    state = line.split("\\s+")[3];
//                    System.out.println(state);
                    state = state.toLowerCase();
                    if (state.equals("connected")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String getConnectedSSID() {
        String ssid = "Kein Wi-Fi Adapter erkannt.";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh wlan show interfaces");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                //line = r.readLine();
                if (line.contains("SSID")) {
                    ssid = line.split("\\s+")[3];
//                    System.out.println(ssid);
                    return ssid;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ssid;
    }

    public static String getIP() {
        String ip = "Kein Wi-Fi Adapter erkannt.";
        String adapter = Locale.getDefault().getCountry().toLowerCase().equalsIgnoreCase("de") ? "\"WLAN\"" : "\"Wi-FI\"";
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh interface ip show addresses " + adapter);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {

                if (line.contains("IP-Ad")) {
                    ip = line.split("\\s+")[2];
                    //System.out.println(ip);
                    return ip;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ip;
    }

    public static String getPublicIp() {
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "nslookup myip.opendns.com");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while((line = reader.readLine()) != null) {
                if(line.contains("Address")) {
                    return line.split("\\s+")[1];
                }
            }
        } catch (IOException e) {

        }

        return NOT_SET;
    }

    public static String getSubnetMask() {
        String sb = NOT_SET;
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh interface ip show addresses \"Wi-Fi\"");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                //line = r.readLine();
                if (line.contains("Subnet Prefix")) {
                    sb = line.split("\\s+")[5];
                    sb = sb.substring(0, sb.length() - 1);
                    //System.out.println(sb);
                    return sb;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb;
    }
}