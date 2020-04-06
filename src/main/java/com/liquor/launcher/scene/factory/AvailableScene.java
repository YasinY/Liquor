package com.liquor.launcher.scene.factory;

import com.liquor.launcher.page.*;

import java.util.Optional;
import java.util.stream.Stream;

public enum AvailableScene {
    DASHBOARD(Dashboard.class),
    CISCO(Cisco.class),
    SETTINGS(Settings.class),
    STATISTICS(Statistics.class),
    TERMINAL(Terminal.class),
    VPN(Vpn.class);

    private Class<?> clazz;

    AvailableScene(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static Optional<AvailableScene> parseScene(String name) {
        return Stream.of(values()).filter(value -> value.name().equalsIgnoreCase(name)).findFirst();
    }

    public static boolean exists(String name) {
        return Stream.of(values()).anyMatch(value -> value.name().toLowerCase().equalsIgnoreCase(name));
    }
}
