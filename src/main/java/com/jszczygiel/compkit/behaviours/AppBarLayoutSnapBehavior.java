package com.jszczygiel.compkit.behaviours;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class AppBarLayoutSnapBehavior extends AppBarLayout.Behavior {

    private boolean nestedScrollStarted = false;

    public AppBarLayoutSnapBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        nestedScrollStarted = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        return nestedScrollStarted;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        if (!nestedScrollStarted) {
            return;
        }

        nestedScrollStarted = false;

        int scrollRange = child.getTotalScrollRange();
        int topOffset = getTopAndBottomOffset();

        if (topOffset <= -scrollRange || topOffset >= 0) {
            // Already fully visible or fully invisible
            return;
        }

        if (topOffset < -(scrollRange / 2f)) {
            // Snap up (to fully invisible)
            child.setExpanded(false,true);
        } else {
            // Snap down (to fully visible)
            child.setExpanded(true, true);
        }
    }
}