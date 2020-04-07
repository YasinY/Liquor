package com.liquor.launcher.functionality.net.windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WifiManager {

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
        String ssid = NOT_SET;
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

    public static String[] getListOfSSIDs() {
        String[] ssid_List;
        String ssid;
        ArrayList<String> arr = new ArrayList<>();
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh wlan show networks");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                //line = r.readLine();
                if (line.contains("SSID")) {
                    ssid = line.split("\\s+")[3];
                    //System.out.println(ssid);
                    arr.add(ssid);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ssid_List = new String[arr.size()];
        arr.toArray(ssid_List);
        return ssid_List;
    }

    public static String getIP() {
        String ip = NOT_SET;
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh interface ip show addresses \"Wi-Fi\"");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                //line = r.readLine();
                if (line.contains("IP Address")) {
                    ip = line.split("\\s+")[3];
                    //System.out.println(ip);
                    return ip;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ip;
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

    public static String getBroadcast() {
        String subnetMask = getSubnetMask();
        String ip = getIP();

        String[] arrSubnetMask = subnetMask.split("\\.");
        String[] arrIP = ip.split("\\.");
        int[] networkAddress = new int[4];
        int[] broadcastAddress = new int[4];

        String broadcast = "";

        for (int i = 0; i < 4; i++) {
            networkAddress[i] = Integer.parseInt(arrIP[i]) & Integer.parseInt(arrSubnetMask[i]);
            //System.out.println(networkAddress[i]);
        }

        for (int i = 0; i < 4; i++) {
            //broadcastAddress[i] =  networkAddress[i] | (~Integer.parseInt(arrSubnetMask[i]) & 0xff);
            //System.out.println(broadcastAddress[i]);
            broadcast = broadcast + "." + (networkAddress[i] | (~Integer.parseInt(arrSubnetMask[i]) & 0xff));
        }

//        System.out.println(broadcast.substring(1));

        //mask AND ip you get network address
        //Invert Mask OR Network Address you get broadcast

        return broadcast.substring(1);
    }
}