package com.jszczygiel.foundation.views;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;

import com.jszczygiel.R;
import com.jszczygiel.compkit.animators.AnimationHelper;

import java.util.Random;

public abstract class RevelSimpleFragmentActivityImpl<T extends Fragment> extends SimpleFragmentActivityImpl<T> {
    public static final String EXTRA_X = "extra_x";
    public static final String EXTRA_Y = "extra_y";
    private DisplayMetrics metrics;
    private View container;
    private Animator animator;
    private int x;
    private int y;

    private boolean isReveled;

    @Override
    public int getLayoutId() {
        return R.layout.activity_reveal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Random random = new Random();
        x = getIntent().getIntExtra(EXTRA_X, random.nextInt(metrics.widthPixels));
        y = getIntent().getIntExtra(EXTRA_Y, random.nextInt(metrics.heightPixels));

        container = findViewById(com.jszczygiel.R.id.activity_simple_root);

        container.post(new Runnable() {
            @Override
            public void run() {
                if (animator == null) {
                    animator = AnimationHelper.circularReveal(container, x, y, 0, metrics.widthPixels, new AnimationHelper.SimpleAnimatorListener() {
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
            animator = AnimationHelper.circularReveal(container, x, y, metrics.widthPixels, 0, new AnimationHelper.SimpleAnimatorListener() {
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
}
