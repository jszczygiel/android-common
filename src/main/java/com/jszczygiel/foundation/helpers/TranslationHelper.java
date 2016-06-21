package com.jszczygiel.foundation.helpers;

import com.jszczygiel.foundation.BaseApplication;

public class TranslationHelper {
    private TranslationHelper() {
    }

    public static String getString(int resourceId) {
        return BaseApplication.getInstance().getString(resourceId);
    }

    public static String[] getStringArray(int resourceId) {
        return BaseApplication.getInstance().getResources().getStringArray(resourceId);
    }

    public static String getString(int resourceId, Object... formatArgs) {
        return BaseApplication.getInstance().getString(resourceId, formatArgs);
    }

}
