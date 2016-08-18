package com.jszczygiel.foundation.views;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.jszczygiel.R;
import com.jszczygiel.compkit.animators.AnimationHelper;
import com.jszczygiel.foundation.helpers.SystemHelper;

import java.util.Random;

public abstract class RevelSimpleFragmentActivityImpl<T extends Fragment> extends SimpleFragmentActivityImpl<T> {
    public static final String EXTRA_X = "extra_x";
    public static final String EXTRA_Y = "extra_y";
    public static final String EXTRA_COLOR = "extra_color";
    private View container;
    private Animator animator;
    private int x;
    private int y;
    Random random = new Random();

    private boolean isReveled;
    protected Point point;

    @Override
    public int getLayoutId() {
        return R.layout.activity_reveal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        point = SystemHelper.getScreenDimension(this);

        x = getX();
        y = getY();
        int transitionColor = getIntent().getIntExtra(EXTRA_COLOR, -1);

        container = findViewById(com.jszczygiel.R.id.activity_simple_root);
        if (transitionColor != -1) {
            container.setBackgroundColor(transitionColor);
        }
        container.post(new Runnable() {
            @Override
            public void run() {
                if (animator == null) {
                    animator = AnimationHelper.circularReveal(container, x, y, 0, point.x, new AnimationHelper.SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator a) {
                            animator = null;
                            isReveled = true;
                        }
                    });
                    animator.start();
                }
            }
        });

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
            animator = AnimationHelper.circularReveal(container, x, y, point.x, 0, new AnimationHelper.SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator a) {
                    container.setVisibility(View.GONE);
                    RevelSimpleFragmentActivityImpl.super.finish();
                    overridePendingTransition(0, 0);
                }
            });
            animator.start();
        }
    }

    public View getContainer() {
        return container;
    }

    public int getX() {
        return getIntent().getIntExtra(EXTRA_X, random.nextInt(point.x));
    }

    public int getY() {
        return getIntent().getIntExtra(EXTRA_Y, random.nextInt(point.y));
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
            animator = AnimationHelper.circularReveal(container, x, y, point.x, 0, new AnimationHelper.SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator a) {
                    container.setVisibility(View.GONE);
                    RevelSimpleFragmentActivityImpl.super.navigateUpTo(upIntent);
                    overridePendingTransition(0, 0);
                }
            });
            animator.start();
        }
        return true;
    }
}
