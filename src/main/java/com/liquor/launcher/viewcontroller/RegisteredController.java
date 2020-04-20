package com.liquor.launcher.viewcontroller;

import com.liquor.launcher.viewcontroller.impl.*;

import java.util.Optional;
import java.util.stream.Stream;

public enum RegisteredController {
    DASHBOARD(Dashboard.class),
    CISCO(Cisco.class),
    VPN(VPN.class),
    STATISTICS(Statistics.class),
    TERMINAL(Terminal.class),
    SETTINGS(Settings.class);

    private Class<?> referencedClass;

    RegisteredController(Class<?> referencedClass) {
        this.referencedClass = referencedClass;
    }

    public Class<?> getReferencedClass() {
        return referencedClass;
    }

    public static Optional<RegisteredController> find(String name) {
        return Stream.of(values()).filter(value -> value.name().equalsIgnoreCase(name)).findFirst();
    }
}
