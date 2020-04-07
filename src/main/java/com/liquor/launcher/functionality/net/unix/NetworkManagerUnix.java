package com.liquor.launcher.functionality.net.unix;

public class NetworkManagerUnix {


    public String getPublicIpCommand() {
        return "dig +short myip.opendns.com";
    }
}
