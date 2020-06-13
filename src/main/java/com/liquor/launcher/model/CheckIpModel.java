package com.liquor.launcher.model;

import com.google.gson.annotations.SerializedName;

public class CheckIpModel {


    @SerializedName("VPN")
    private boolean usingVpn;

    @SerializedName("TOR")
    private boolean usingTor;

    @SerializedName("IP")
    private String ipAddress;

    @SerializedName("DNS")
    private String dns;

    @SerializedName("CITY")
    private String city;

    @SerializedName("COUNTRY")
    private String country;

    public CheckIpModel(boolean usingVpn, boolean usingTor, String ipAddress, String dns, String city, String country) {
        this.usingVpn = usingVpn;
        this.usingTor = usingTor;
        this.ipAddress = ipAddress;
        this.dns = dns;
        this.city = city;
        this.country = country;
    }

    public boolean isUsingVpn() {
        return usingVpn;
    }

    public boolean isUsingTor() {
        return usingTor;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDns() {
        return dns;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
