package com.jszczygiel.compkit.animators;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;

import com.jszczygiel.compkit.animators.animation.ViewAnimationUtils;
import com.jszczygiel.foundation.rx.BackPressureSubscriber;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class AnimationHelper {

  public static final int DURATION = 200;
  public static final int LONG_DURATION = 400;

  public static final int VERY_LONG_DURATION = DURATION * 6;
  private static final TimeInterpolator FAST_OUT = new FastOutLinearInInterpolator();
  private static final TimeInterpolator LINEAR_OUT = new LinearOutSlowInInterpolator();

  private AnimationHelper() {
  }

  public static Animator circularReveal(View view, int centerX, int centerY, int startRadius,
                                        int endRadius, SimpleAnimatorListener callback) {
    Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY,
        startRadius, endRadius, View.LAYER_TYPE_HARDWARE);
    animator.setInterpolator(startRadius < endRadius ? FAST_OUT : LINEAR_OUT);
    animator.setDuration(LONG_DURATION);
    animator.addListener(callback);
    return animator;
  }

  public static ViewPropertyAnimator expandIn(View view, SimpleAnimatorListener callback) {
    return view.animate().alpha(1).setListener(callback);
  }

  public static ViewPropertyAnimator expandOut(View view, SimpleAnimatorListener callback) {
    return view.animate().alpha(0).setListener(callback);
  }

  public static Point getCenter(View view) {
    int[] outLocation = new int[2];
    view.getLocationOnScreen(outLocation);
    return new Point(outLocation[0] + view.getWidth() / 2,
        outLocation[1] + view.getHeight() / 2);
  }

  public static int getColor(View view) {

    if (view.getBackground() instanceof ColorDrawable) {
      return ((ColorDrawable) view.getBackground()).getColor();
    }
    Object tag = view.getTag();
    if (tag instanceof String) {
      return Color.parseColor((String) tag);
    }

    return Color.TRANSPARENT;
  }

  public static void transitionBackground(View view, Drawable background) {
    Drawable drawable = view.getBackground();
    if (drawable instanceof TransitionDrawable) {
      drawable = ((TransitionDrawable) drawable).getDrawable(1);
    } else if (drawable == null) {
      drawable = new ColorDrawable(Color.TRANSPARENT);
    }
    TransitionDrawable transitionDrawable = new TransitionDrawable(
        new Drawable[]{drawable, background});
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      view.setBackground(transitionDrawable);
    } else {
      view.setBackgroundDrawable(transitionDrawable);
    }
    transitionDrawable.startTransition(VERY_LONG_DURATION);

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

  public static void swipe(final ViewGroup view, final int y, int swipeSeconds,
                           final int holdTimeSeconds, final int keyline) {
    final int frames = swipeSeconds * 1000 / 16;
    final long downTime = SystemClock.uptimeMillis();
    final long eventTime = SystemClock.uptimeMillis();
    final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();

    Observable.range(0, frames)
        .delay(new Func1<Integer, Observable<Integer>>() {
          @Override
          public Observable<Integer> call(Integer integer) {
            return Observable.just(integer).delay(
                integer == frames - 1 ? holdTimeSeconds * 1000 : 16,
                TimeUnit.MILLISECONDS);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new BackPressureSubscriber<Integer>() {
          @Override
          public void onNext(Integer next) {
            int x = (int) (interpolator.getInterpolation(
                (float) next / (float) frames) * keyline);
            MotionEvent event;
            if (next == 0) {
              event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN,
                  x, y, 0);

            } else if (next == frames - 1) {
              event = MotionEvent.obtain(downTime, eventTime,
                  MotionEvent.ACTION_CANCEL, x, y, 0);

            } else {
              event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE,
                  x, y, 0);

            }
            view.dispatchTouchEvent(event);
            super.onNext(next);
          }
        });
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
}
