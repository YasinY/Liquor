package com.liquor.launcher.functionality.profile;


import com.google.gson.annotations.SerializedName;
import com.liquor.launcher.Liquor;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.resourcemanagement.ResourceLoader;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.Optional;

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

    public Theme switchTheme() {
        Liquor.scene.getStylesheets().removeAll();
        this.theme = this.theme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT;
        Optional<URL> potentialSheet = ResourceLoader.getCSS(theme.getName());
        potentialSheet.ifPresent(stylesheet -> Liquor.scene.getStylesheets().add(stylesheet.toExternalForm()));
        return this.theme;
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
