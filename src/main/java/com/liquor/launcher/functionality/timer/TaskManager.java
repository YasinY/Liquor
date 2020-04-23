package com.liquor.launcher.functionality.timer;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Set;

@Slf4j
public class TaskManager {

    private static TaskManager instance;

    public void init() {
        final String PACKAGE_TO_TASKS = "com.liquor.launcher.functionality.timer.impl";
        Set<Class<? extends ITask>> subTypes = new Reflections(PACKAGE_TO_TASKS).getSubTypesOf(ITask.class);
        subTypes.forEach(referencedClass -> {
            try {
                referencedClass.newInstance().init();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Error initialising class " + referencedClass.getName());
            }
        });
        log.info("Initialised " + subTypes.size() + " tasks");
    }

    public static TaskManager getInstance() {
        return instance == null ? instance = new TaskManager() : instance;
    }
}
