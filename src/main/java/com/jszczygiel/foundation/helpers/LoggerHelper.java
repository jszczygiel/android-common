package com.jszczygiel.foundation.helpers;

import android.util.Log;

public class LoggerHelper {

  private static int LOGLEVEL = Log.ASSERT;
  private static boolean ERROR = LOGLEVEL > Log.ERROR;
  private static boolean INFO = LOGLEVEL > Log.INFO;
  private static boolean DEBUG = LOGLEVEL > Log.DEBUG;
  private static final String TAG = "APP";

  public static void logInfo(String message) {
    if (INFO) {
      Log.i(TAG, message);
    }
  }

  public static void logDebug(String message) {
    if (DEBUG) {
      Log.d(TAG, message);
    }
  }

  public static void logError(String message) {
    if (ERROR) {
      Log.e(TAG, message);
    }
  }

  public static void printMethodStack() {
    new Throwable().printStackTrace();
  }

  public static void log(Throwable e) {
    if (INFO) {
      e.printStackTrace();
    }
  }
}
