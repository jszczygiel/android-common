package com.jszczygiel.foundation.helpers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.regex.Pattern;

public class SystemHelper {

  public static boolean isTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK)
        >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }

  public static void hideKeyboard(Context context, View view) {
    if (view != null && context != null) {
      InputMethodManager imm =
          (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public static Point getScreenDimension(Activity activity) {
    Display display = activity.getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    return size;
  }

  public static int getNumberOfCores() {
    if (Build.VERSION.SDK_INT >= 17) {
      return Runtime.getRuntime().availableProcessors();
    } else {
      // Use saurabh64's answer
      return getNumCoresOldPhones();
    }
  }

  public static boolean isRTL(Context ctx) {
    Configuration config = ctx.getResources().getConfiguration();
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
      return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
    return false;
  }

  public static boolean isArabic() {
    try {
      return Locale.getDefault().getISO3Language().equals("ara");
    } catch (MissingResourceException e) {
      return false;
    }
  }

  /**
   * Gets the number of cores available in this device, across all processors. Requires: Ability to
   * peruse the filesystem at "/sys/devices/system/cpu"
   *
   * @return The number of cores, or 1 if failed to get result
   */
  private static int getNumCoresOldPhones() {
    //Private Class to display only CPU devices in the directory listing
    class CpuFilter implements FileFilter {

      @Override
      public boolean accept(File pathname) {
        //Check if filename is "cpu", followed by a single digit number
        return Pattern.matches("cpu[0-9]+", pathname.getName());
      }
    }

    try {
      //Get directory containing CPU info
      File dir = new File("/sys/devices/system/cpu/");
      //Filter to only list the devices we care about
      File[] files = dir.listFiles(new CpuFilter());
      //Return the number of cores (virtual CPU devices)
      return files.length;
    } catch (Exception e) {
      //Default to return 1 core
      return 1;
    }
  }

  public static void showKeyboard(final EditText message) {
    message.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            InputMethodManager keyboard =
                (InputMethodManager)
                    message.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(message, 0);
          }
        },
        100);
  }

  public static String getProcessName(Context context) {
    int pid = android.os.Process.myPid();
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

    List<ActivityManager.RunningServiceInfo> runningServiceInfos =
        manager.getRunningServices(Integer.MAX_VALUE);
    if (runningServiceInfos != null) {
      for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos) {
        if (runningServiceInfo.pid == pid) {
          return runningServiceInfo.process;
        }
      }
    }

    List<ActivityManager.RunningAppProcessInfo> processInfos = manager.getRunningAppProcesses();
    if (processInfos != null) {
      for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
        if (processInfo.pid == pid) {
          return processInfo.processName;
        }
      }
    }
    return "";
  }

  public static boolean hasNfc(Context context) {
    try {
      NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
      NfcAdapter adapter = manager.getDefaultAdapter();
      return adapter != null;
    } catch (Exception | Error e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean hasRearCamera(Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
  }

  public static boolean hasFrontCamera(Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
  }

  public static boolean isScreenLocked(Context context) {
    KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    if (myKM.inKeyguardRestrictedInputMode()) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean hasFlashLight(Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
  }
}
