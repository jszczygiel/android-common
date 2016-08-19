package com.jszczygiel.foundation;

import android.app.Application;
import android.support.annotation.CallSuper;

public class BaseApplication extends Application {

    private static BaseApplication INSTANCE;
    private boolean isDebug;

    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
