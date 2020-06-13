package com.liquor.launcher.functionality.profile;


import com.google.gson.annotations.SerializedName;
import com.liquor.launcher.Liquor;
import com.liquor.launcher.functionality.theme.Theme;
import com.liquor.resourcemanagement.ResourceLoader;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

@Builder
@Getter
@Setter
@Slf4j
public class Profile {

    @Builder.Default
    @SerializedName("total-time-spent")
    private int minutes = 0;

    @Builder.Default
    @SerializedName("selected-theme")
    private Theme theme = Theme.LIGHT;

    @Override
    public String toString() {
        return "[Total time spent: " + minutes + " minutes, current theme: " + theme + "]";
    }

    @Override
    public int hashCode() {
        return -1;
    }


    public Theme switchTheme() {
        updateStyle(this.theme == Theme.LIGHT ? Theme.DARK : Theme.LIGHT);
        return this.theme;
    }

    public void updateStyle(Theme theme) {
        this.theme = theme;
        Optional<URL> potentialSheet = ResourceLoader.getCSS(theme.getName());
        potentialSheet.ifPresent(stylesheet -> {
            log.info("Updated style to " + theme.getName());
            Liquor.parent.getStylesheets().clear();
            Liquor.parent.getStylesheets().add(stylesheet.toExternalForm());
        });
    }

    public int getMinutes() {
        return minutes;
    }
    public int getHours() {
        return minutes / 60;
    }

    public int getDays() {
        return getHours() / 24;
    }

    public int getSeconds() {
        return getMinutes() * 60;
    }

}
