package com.jszczygiel.foundation.helpers;

import android.util.Log;

import com.jszczygiel.foundation.BaseApplication;

public class LoggerHelper {
    public static final String TAG = "APP";

    private static boolean isDebug;

    private LoggerHelper() {
        isDebug = BaseApplication.getInstance().isDebug();
    }

    public static void logInfo(String message) {
        Log.i(TAG, message);
    }

    public static void logDebug(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }

    public static void logError(String message) {
        if (isDebug) {
            Log.e(TAG, message);
        }
    }

    public static void printMethodStack() {
        new Throwable().printStackTrace();
    }

    public static void log(Throwable e) {
        if (isDebug) {
            e.printStackTrace();
        }

    }

}
