package com.jszczygiel.compkit.drawables;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.IntRange;

public class MultiDrawable extends LayerDrawable implements Drawable.Callback {

    private final int drawableCount;
    private int[] alphas;
    private int drawableAlpha;

    public MultiDrawable(Drawable[] drawables) {
        super(drawables);
        drawableCount = drawables.length;
        alphas = new int[drawableCount];
        for (int i = 0; i < drawableCount; i++) {
            setId(i, i);
        }
    }

    public void setAlphas(@IntRange(from = 0, to = 255) int[] alphas) {
        if (alphas.length != drawableCount) {
            throw new IllegalArgumentException("alphas length and drawableCount are not equal");
        }
        this.alphas = alphas;
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < drawableCount; i++) {
            Drawable drawable = getDrawable(i);
            drawable.setAlpha(alphas[i] * drawableAlpha / 255);
            if (alphas[i] != 0) {
                drawable.draw(canvas);
            }
        }

        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        this.drawableAlpha = alpha;
    }
}