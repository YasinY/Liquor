package com.liquor.launcher.viewcontroller;

import java.util.Optional;
import java.util.stream.Stream;

public enum RegisteredController {
    DASHBOARD,
    CISCO,
    VPN,
    STATISTICS,
    TERMINAL,
    SETTINGS;

    public static Optional<RegisteredController> find(String name) {
        return Stream.of(values()).filter(value -> value.name().equalsIgnoreCase(name)).findFirst();
    }
}
