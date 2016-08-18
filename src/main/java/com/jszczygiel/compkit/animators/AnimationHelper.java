package com.jszczygiel.compkit.animators;

import android.animation.Animator;
import android.graphics.Color;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import com.jszczygiel.foundation.containers.Tuple;

import io.codetail.animation.ViewAnimationUtils;

public class AnimationHelper {

    private static final long DURATION = 266;

    private AnimationHelper() {
    }

    public static Animator circularReveal(View view, int centerX, int centerY, int startRadius, int endRadius, SimpleAnimatorListener callback) {
        Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius, View.LAYER_TYPE_HARDWARE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(DURATION);
        animator.addListener(callback);
        return animator;
    }

    public static ViewPropertyAnimator expandIn(View view, SimpleAnimatorListener callback) {
        return view.animate().alpha(1).setListener(callback);
    }

    public static ViewPropertyAnimator expandOut(View view, SimpleAnimatorListener callback) {
        return view.animate().alpha(0).setListener(callback);
    }

    public static Tuple<Integer, Integer> getCenter(View view) {
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        return new Tuple<>(outLocation[0] + view.getWidth() / 2, outLocation[1]);
    }

    public static class SimpleAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    public static class SimpleAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public static int darker(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
}
