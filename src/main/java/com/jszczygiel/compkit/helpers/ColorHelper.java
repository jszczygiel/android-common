package com.jszczygiel.compkit.helpers;

import static android.graphics.PorterDuff.Mode;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.drawable.DrawableCompat;

public class ColorHelper {

  @TargetApi(Build.VERSION_CODES.M)
  public static int getColor(Context context, @ColorRes int resId) {
    Resources res = context.getResources();

    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        ? res.getColor(resId, context.getTheme())
        : res.getColor(resId);
  }

  public static void tintDrawable(Drawable drawable, @ColorInt int color) {
    tintDrawable(drawable, ColorStateList.valueOf(color), Mode.SRC_IN);
  }

  public static void tintDrawable(Drawable drawable, ColorStateList colors, Mode mode) {
    if (drawable == null) {
      return;
    }

    drawable.mutate();

    DrawableCompat.setTintList(drawable, colors);
    DrawableCompat.setTintMode(drawable, mode);
  }

  public static void tintDrawable(Drawable drawable, ColorStateList colors) {
    tintDrawable(drawable, colors, Mode.SRC_IN);
  }

  public static class ColorStateListBuilder {

    int[] mColors;

    int[][] mStates;

    private ColorStateListBuilder(int[]... states) {
      mStates = states;
      mColors = new int[mStates.length];
    }

    public static ColorStateListBuilder forStates(int... states) {
      int[][] stateList = new int[states.length][];

      for (int i = 0; i < states.length; i++) {
        stateList[i] = new int[] {states[i]};
      }

      return new ColorStateListBuilder(stateList);
    }

    public static ColorStateListBuilder forStates(int[]... states) {
      return new ColorStateListBuilder(states);
    }

    public ColorStateList toList() {
      return new ColorStateList(mStates, mColors);
    }

    public ColorStateListBuilder withColors(int... colors) {
      if (colors.length != mStates.length) {
        throw new IllegalArgumentException("Invalid number of colors given!");
      }

      System.arraycopy(colors, 0, mColors, 0, colors.length);

      return this;
    }
  }
}
