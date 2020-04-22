package com.liquor.launcher.functionality.profile;


import com.google.gson.annotations.SerializedName;
import com.liquor.launcher.functionality.theme.Theme;

public class Profile {

    @SerializedName("total-time-spent")
    private long totalTime;

    @SerializedName("selected-theme")
    private Theme theme;

}
