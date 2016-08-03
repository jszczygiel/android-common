package com.jszczygiel.compkit.customviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.StateSet;

import com.jszczygiel.R;
import com.jszczygiel.compkit.helpers.ColorHelper;
import com.jszczygiel.compkit.images.ImageBuilder;

public class TintedImageView extends AppCompatImageView {

    public static final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
    public static final int[] STATE_PRESSED_DISABLED = new int[]{android.R.attr.state_pressed, -android.R.attr.state_enabled};
    public static final int[] STATE_DISABLED = new int[]{-android.R.attr.state_enabled};

    protected ColorStateList colorStateList;

    protected PorterDuff.Mode colorTintMode = PorterDuff.Mode.SRC_OVER;

    public TintedImageView(Context context) {
        this(context, null);
    }

    public TintedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseColorAttributes(context, attrs);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
        }

        super.setImageDrawable(drawable);

        applyTint(drawable);
    }

    @Override
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);

        applyTint(getDrawable());
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        applyTint(getDrawable());
    }

    protected void applyTint(Drawable drawable) {
        if (colorStateList == null || drawable == null) {
            return;
        }

        drawable.mutate();

        DrawableCompat.setTintList(drawable, colorStateList);
        DrawableCompat.setTintMode(drawable, colorTintMode);

        drawable.setState(getDrawableState());
        invalidateDrawable(drawable);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setState(getDrawableState());
            invalidateDrawable(drawable);
        }
    }

    public void loadImage(@DrawableRes int placeHolder, String url) {
        final ImageBuilder imageLoader = ImageBuilder.with(getContext());

        if (!TextUtils.isEmpty(url)) {
            imageLoader.load(url).placeHolder(placeHolder).into(this);
        } else {
            imageLoader.load(placeHolder).into(this);
        }

    }

    public void loadImage(@DrawableRes int placeHolder) {
        loadImage(placeHolder, null);
    }

    public void setColorTintList(ColorStateList colors) {
        colorStateList = colors;
    }

    public void setColorTint(@ColorInt int color) {
        colorStateList = ColorHelper.ColorStateListBuilder
                .forStates(STATE_PRESSED, StateSet.WILD_CARD)
                .withColors(color, color)
                .toList();
        applyTint(getDrawable());
    }

    public void setColorTint(@ColorInt int pressedColor, @ColorInt int defaultColor) {
        colorStateList = ColorHelper.ColorStateListBuilder
                .forStates(STATE_PRESSED, StateSet.WILD_CARD)
                .withColors(pressedColor, defaultColor)
                .toList();
        applyTint(getDrawable());
    }

    public void setColorTint(@ColorInt int pressedColor, @ColorInt int defaultColor, @ColorInt int disabledColor) {
        colorStateList = ColorHelper.ColorStateListBuilder
                .forStates(STATE_PRESSED, STATE_DISABLED, StateSet.WILD_CARD)
                .withColors(pressedColor, disabledColor, defaultColor)
                .toList();
        applyTint(getDrawable());
    }

    protected void setColorTintMode(PorterDuff.Mode mode) {
        colorTintMode = mode;
    }

    protected void parseColorAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = null;

        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.TintedImageView, 0, 0);

            int tintColour = ta.getColor(R.styleable.TintedImageView_tintColour, ContextCompat.getColor(context, android.R.color.black));
            int selectedTintColour = ta.getColor(R.styleable.TintedImageView_selectedTintColour, ContextCompat.getColor(context,  android.R.color.black));

            colorStateList = ColorHelper.ColorStateListBuilder
                    .forStates(STATE_PRESSED, StateSet.WILD_CARD)
                    .withColors(selectedTintColour, tintColour)
                    .toList();
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }

        applyTint(getDrawable());
    }
}