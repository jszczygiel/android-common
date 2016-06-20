package com.jszczygiel.foundation;

import android.app.Application;
import android.support.annotation.CallSuper;

public class BaseApplication extends Application {

    private boolean isDebug;
    private static BaseApplication INSTANCE;

    public static BaseApplication getInstance() {
        return INSTANCE;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
