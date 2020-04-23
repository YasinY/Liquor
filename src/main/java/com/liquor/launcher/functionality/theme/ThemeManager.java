package com.liquor.launcher.functionality.theme;

public class ThemeManager {

    private static ThemeManager instance;

    private Theme currentTheme;

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public static ThemeManager getInstance() {
        return instance == null ? instance = new ThemeManager() : instance;
    }
}
