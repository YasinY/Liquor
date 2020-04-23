package com.liquor.launcher.functionality.timer;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Set;

@Slf4j
public class TaskManager {

    private static TaskManager instance;

    public void init() {
        Set<Class<? extends ITask>> subTypes = new Reflections("com.liquor.launcher.functionality.timer.impl").getSubTypesOf(ITask.class);
        subTypes.forEach(referencedClass -> {
            try {
                referencedClass.newInstance().init();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        log.info("Initialised " + subTypes.size() + " tasks");
    }

    public static TaskManager getInstance() {
        return instance == null ? instance = new TaskManager() : instance;
    }
}
