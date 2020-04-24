package com.liquor.launcher.functionality.theme;

public enum Theme {
    LIGHT("launcher"),
    DARK("launcher_dark");

    private String name;

    Theme(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
