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
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jszczygiel.R;
import com.jszczygiel.compkit.animators.AnimationHelper;
import com.jszczygiel.compkit.viewmodels.RevelOptions;
import com.jszczygiel.foundation.helpers.SystemHelper;

import java.util.Random;

public abstract class RevelSimpleFragmentActivityImpl<T extends BaseFragmentImpl> extends SimpleFragmentActivityImpl<T> {
    public static final String EXTRA_REVEAL_OPTIONS = "extra_revel_options";
    protected Point point;
    Random random = new Random();
    private ViewGroup container;
    private Animator animator;
    private int x;
    private int y;
    private boolean isReveled;
    private TransitionDrawable transition;
    private RevelOptions revelOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        point = SystemHelper.getScreenDimension(this);

        revelOptions = getIntent().getParcelableExtra(EXTRA_REVEAL_OPTIONS);

        x = getX();
        y = getY();

        final int color = revelOptions == null ? Color.TRANSPARENT : revelOptions.getFromColor();

        container = (ViewGroup) findViewById(R.id.activity_simple_root);
        container.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {

                    if (animator == null) {
                        animator = AnimationHelper.circularReveal(container, x, y, 0, point.y, new AnimationHelper.SimpleAnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator a) {
                                animator = null;
                                isReveled = true;
                            }
                        });
                        animator.start();
                    }
                    if (color != Color.TRANSPARENT) {
                        transition = new TransitionDrawable(new Drawable[]{new ColorDrawable(color), getFragmentView().getBackground()});
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            getFragmentView().setBackground(transition);
                        } else {
                            getFragmentView().setBackgroundDrawable(transition);
                        }
                        transition.startTransition(AnimationHelper.DURATION);
                    }
                }
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reveal;
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
            super.finish();
            overridePendingTransition(0, 0);
        } else if (animator == null) {
            animator = AnimationHelper.circularReveal(container, x, y, point.y, 0, new AnimationHelper.SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator a) {
                    RevelSimpleFragmentActivityImpl.super.finish();
                    container.setVisibility(View.INVISIBLE);
                    overridePendingTransition(0, 0);
                }
            });
            if (transition != null) {
                transition.reverseTransition(AnimationHelper.DURATION);
            }
            animator.start();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean navigateUpTo(final Intent upIntent) {
        if (!isReveled) {
            if (animator != null) {
                animator.cancel();
                animator = null;
            }
            overridePendingTransition(0, 0);
            return super.navigateUpTo(upIntent);
        } else if (animator == null) {
            animator = AnimationHelper.circularReveal(container, x, y, point.y, 0, new AnimationHelper.SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator a) {
                    RevelSimpleFragmentActivityImpl.super.navigateUpTo(upIntent);
                    container.setVisibility(View.INVISIBLE);
                    overridePendingTransition(0, 0);
                }
            });
            if (transition != null) {
                transition.reverseTransition(AnimationHelper.DURATION);
            }
            animator.start();
        }
        return true;
    }

    public View getContainer() {
        return container;
    }

}
