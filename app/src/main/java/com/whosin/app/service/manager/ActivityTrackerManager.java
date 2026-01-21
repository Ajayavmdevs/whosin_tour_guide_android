package com.whosin.app.service.manager;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ActivityTrackerManager implements Application.ActivityLifecycleCallbacks {

    private static ActivityTrackerManager instance;

    private Activity currentActivity;

    private ActivityTrackerManager() {}

    public static synchronized ActivityTrackerManager getInstance() {
        if (instance == null) {
            instance = new ActivityTrackerManager();
        }
        return instance;
    }

    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }
}

