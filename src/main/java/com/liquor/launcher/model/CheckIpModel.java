package com.liquor.launcher.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CheckIpModel {

    @SerializedName("VPN")
    @Builder.Default
    private boolean usingVpn = false;

    @SerializedName("TOR")
    @Builder.Default
    private boolean usingTor = false;

    @SerializedName("IP")
    @Builder.Default
    private String ipAddress = "";

    @SerializedName("DNS")
    @Builder.Default
    private String dns = "";

    @SerializedName("CITY")
    @Builder.Default
    private String city = "";

    @SerializedName("COUNTRY")
    @Builder.Default
    private String country = "";

}
