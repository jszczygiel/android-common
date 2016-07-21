package com.jszczygiel.foundation.helpers;

import android.util.Log;

import com.jszczygiel.foundation.BaseApplication;

public class LoggerHelper {
    public static final String TAG = "APP";

    public static void logInfo(String message) {
        Log.i(TAG, message);
    }

    public static void logDebug(String message) {
        if (BaseApplication.getInstance().isDebug()) {
            Log.d(TAG, message);
        }
    }

    public static void logError(String message) {
        if (BaseApplication.getInstance().isDebug()) {
            Log.e(TAG, message);
        }
    }

    public static void printMethodStack() {
        new Throwable().printStackTrace();
    }

    public static void log(Throwable e) {
        if (BaseApplication.getInstance().isDebug()) {
            e.printStackTrace();
        }

    }

}
