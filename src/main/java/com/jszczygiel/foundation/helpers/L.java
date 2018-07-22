package com.jszczygiel.foundation.helpers;

import android.util.Log;

public class L {

  private static int LOGLEVEL = Log.VERBOSE;
  private static final String TAG = "APP";

  public static void i(String message) {
    if (LOGLEVEL > Log.INFO) {
      Log.i(TAG, message);
    }
  }

  public static void d(String message) {
    if (LOGLEVEL > Log.DEBUG) {
      Log.d(TAG, message);
    }
  }

  public static void e(String message) {
    if (LOGLEVEL > Log.ERROR) {
      Log.e(TAG, message);
    }
  }

  public static void printMethodStack() {
    new Throwable().printStackTrace();
  }

  public static void print(Throwable e) {
    if (LOGLEVEL > Log.DEBUG) {
      e.printStackTrace();
    }
  }


  public static void setLogLevel(int logLevel) {
    LOGLEVEL = logLevel;
  }
}
