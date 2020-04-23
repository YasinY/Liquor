package com.liquor.launcher.functionality.profile;


import com.google.gson.annotations.SerializedName;
import com.liquor.launcher.functionality.theme.Theme;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Profile {

    @Builder.Default
    @SerializedName("total-time-spent")
    private int totalTime = 0;

    @Builder.Default
    @SerializedName("selected-theme")
    private Theme theme = Theme.LIGHT;

    @Override
    public String toString() {
        return "[Total time spent: " + totalTime + ", current theme: " + theme + "]";
    }

    @Override
    public int hashCode() {
        return -1;
    }

    public int getSeconds() {
        return totalTime * 60;
    }

    public int getMinutes() {
        return totalTime;
    }

    public int getHours() {
        return totalTime / 60;
    }

}
