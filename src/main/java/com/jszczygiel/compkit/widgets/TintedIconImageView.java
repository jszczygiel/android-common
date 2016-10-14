package com.jszczygiel.compkit.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.StateSet;

import com.jszczygiel.R;
import com.jszczygiel.compkit.helpers.ColorHelper;

public class TintedIconImageView extends TintedImageView {

    public TintedIconImageView(Context context) {
        this(context, null);
    }

    public TintedIconImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintedIconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void parseColorAttributes(Context context, AttributeSet attrs) {
        setColorTintMode(PorterDuff.Mode.SRC_IN);
        TypedArray ta = null;

        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.TintedIconImageView, 0, 0);

            boolean shouldTint = ta.getBoolean(R.styleable.TintedIconImageView_tint, true);
            boolean enabled = ta.getBoolean(R.styleable.TintedIconImageView_android_enabled, true);

            colorStateList = ta.getColorStateList(R.styleable.TintedIconImageView_colorStateList);

            if (colorStateList == null) {
                Resources resources = getResources();
                int defaultColor = ta.getColor(R.styleable.TintedIconImageView_defaultColor, resources.getColor(android.R.color.black));
                int disabledColor = ta.getColor(R.styleable.TintedIconImageView_disabledColor, resources.getColor(android.R.color.black));
                int focusedColor = ta.getColor(R.styleable.TintedIconImageView_focusedColor, resources.getColor(android.R.color.black));
                int focusedDisabledColor = ta.getColor(R.styleable.TintedIconImageView_focusedDisabledColor, resources.getColor(android.R.color.black));

                colorStateList = ColorHelper.ColorStateListBuilder
                        .forStates(STATE_DISABLED, STATE_PRESSED, STATE_PRESSED_DISABLED, StateSet.WILD_CARD)
                        .withColors(disabledColor, focusedColor, focusedDisabledColor, defaultColor)
                        .toList();
            }

            setEnabled(enabled);
            setTint(shouldTint);
            applyTint(getDrawable());
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    public void setTint(boolean tint) {
        setColorTintList(tint ? colorStateList : null);
        setColorTintMode(colorTintMode);
    }
}
