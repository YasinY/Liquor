package com.liquor.launcher.functionality.os.net.unix;

public class NetworkManagerUnix {


    public String getPublicIpCommand() {
        return "dig +short myip.opendns.com";
    }
}
