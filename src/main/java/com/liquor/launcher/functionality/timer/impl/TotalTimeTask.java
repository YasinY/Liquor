package com.liquor.launcher.functionality.timer.impl;

import com.liquor.launcher.functionality.profile.Profile;
import com.liquor.launcher.functionality.profile.ProfileManager;
import com.liquor.launcher.functionality.timer.ITask;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TotalTimeTask implements ITask {

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void init() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Optional<Profile> potentialProfile = ProfileManager.getInstance().getSelectedProfile();
            potentialProfile.ifPresent(profile -> profile.setTotalTime(profile.getTotalTime() + 1));
        }, 0, 1, TimeUnit.MINUTES);
    }
}
