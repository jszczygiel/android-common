package com.jszczygiel.foundation.helpers;

import android.util.Log;

public class LoggerHelper {

  private static int LOGLEVEL = Log.VERBOSE;
  private static final String TAG = "APP";

  public static void logInfo(String message) {
    if (LOGLEVEL > Log.INFO) {
      Log.i(TAG, message);
    }
  }

  public static void logDebug(String message) {
    if (LOGLEVEL > Log.DEBUG) {
      Log.d(TAG, message);
    }
  }

  public static void logError(String message) {
    if (LOGLEVEL > Log.ERROR) {
      Log.e(TAG, message);
    }
  }

  public static void printMethodStack() {
    new Throwable().printStackTrace();
  }

  public static void log(Throwable e) {
    if (LOGLEVEL > Log.DEBUG) {
      e.printStackTrace();
    }
  }


  public static void setLogLevel(int logLevel) {
    LOGLEVEL = logLevel;
  }
}
