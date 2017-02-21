package com.jszczygiel.foundation.views;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;

import com.jszczygiel.R;
import com.jszczygiel.compkit.animators.AnimationHelper;
import com.jszczygiel.compkit.viewmodels.RevelOptions;
import com.jszczygiel.foundation.helpers.SystemHelper;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class RevelSimpleFragmentActivityImpl<T extends BaseFragmentImpl> extends
    SimpleFragmentActivityImpl<T> {
  public static final String EXTRA_REVEAL_OPTIONS = "extra_revel_options";
  private static final long FRAME = 16;
  protected Point point;
  Random random = new Random();
  private ViewGroup container;
  private Animator animator;
  private int x;
  private int y;
  private boolean isReveled;
  private TransitionDrawable transition;
  private RevelOptions revelOptions;
  private Subscription subscription;
  private View revealLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(0, 0);

    point = SystemHelper.getScreenDimension(this);

    revelOptions = getIntent().getParcelableExtra(EXTRA_REVEAL_OPTIONS);

    x = getX();
    y = getY();

    revealLayout = findViewById(R.id.revel_layout);
    container = (ViewGroup) findViewById(R.id.activity_simple_root);

    if (savedInstanceState == null) {
      container.setVisibility(View.INVISIBLE);
      subscription = Observable.interval(FRAME, TimeUnit.MILLISECONDS)
          .onBackpressureDrop()
          .filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long filter) {
              return RevelSimpleFragmentActivityImpl.this.getFragment().isVisible();
            }
          })
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Action1<Long>() {
            @Override
            public void call(Long next) {
              subscription.unsubscribe();
              circularRevealActivity();
            }
          });

    }
  }

  private void circularRevealActivity() {
    if (!isFinishing()) {
      if (animator == null) {
        animator = AnimationHelper.circularReveal(container, x, y, getWidth(), point.y,
            new AnimationHelper.SimpleAnimatorListener() {
              @Override
              public void onAnimationEnd(Animator a) {
                animator = null;
                isReveled = true;
                if (getFragment() != null && getFragment().getView() != null) {
                  getFragment().getView().requestLayout();
                }

              }
            });
        container.setVisibility(View.VISIBLE);
        animator.start();
      }
      final int color = revelOptions == null ? Color.TRANSPARENT : revelOptions
          .getFromColor();
      if (color != Color.TRANSPARENT) {
        transition = new TransitionDrawable(new Drawable[]{new ColorDrawable(
            color), getFragmentView().getBackground()});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          getFragmentView().setBackground(transition);
        } else {
          getFragmentView().setBackgroundDrawable(transition);
        }
        transition.startTransition(AnimationHelper.LONG_DURATION);
      }
    }
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_reveal;
  }

  public int getWidth() {
    return revelOptions == null ? 0 : revelOptions.getWidth();
  }

  public int getX() {
    return revelOptions == null ? random.nextInt(point.x) : revelOptions.getX();
  }

  public int getY() {
    return revelOptions == null ? random.nextInt(point.y) : revelOptions.getY();
  }

  private View getFragmentView() {
    return container.getChildAt(0);
  }

  @Override
  public void finish() {
    if (!isReveled) {
      if (animator != null) {
        animator.cancel();
        animator = null;
      }
      ActivityCompat.finishAfterTransition(RevelSimpleFragmentActivityImpl.this);
    } else if (animator == null) {
      animator = AnimationHelper.circularReveal(container, x, y, point.y, getWidth(),
          new AnimationHelper.SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator a) {

              if (container != null) {
                container.setVisibility(View.GONE);
              }
              ActivityCompat.finishAfterTransition(
                  RevelSimpleFragmentActivityImpl.this);
            }
          });
      if (transition != null) {
        transition.reverseTransition(AnimationHelper.LONG_DURATION);
      }

      animator.start();
    }
  }


  @Override
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public boolean navigateUpTo(final Intent upIntent) {
    finish();
    return true;
  }

  public View getContainer() {
    return container;
  }

}
