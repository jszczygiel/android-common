package com.jszczygiel.compkit.viewmodels;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.jszczygiel.compkit.animators.AnimationHelper;
import com.jszczygiel.foundation.containers.Tuple;

public class RevelOptions implements Parcelable {
    public static final Creator<RevelOptions> CREATOR = new Creator<RevelOptions>() {
        @Override
        public RevelOptions createFromParcel(Parcel in) {
            return new RevelOptions(in);
        }

        @Override
        public RevelOptions[] newArray(int size) {
            return new RevelOptions[size];
        }
    };
    final int x;
    final int y;
    final int width;
    final int fromColor;

    public RevelOptions(int y, int x) {
        this.y = y;
        this.x = x;
        this.fromColor = Color.TRANSPARENT;
        this.width = 0;
    }

    public RevelOptions(int x, int y, int fromColor) {
        this.x = x;
        this.y = y;
        this.fromColor = fromColor;
        this.width = 0;
    }

    public RevelOptions(View view) {
        this.fromColor = AnimationHelper.getColor(view);
        Tuple<Integer, Integer> positon = AnimationHelper.getCenter(view);
        this.x = positon.getFirst();
        this.y = positon.getSecond();
        this.width = Math.min(view.getWidth() / 2, view.getHeight() / 2);
    }

    protected RevelOptions(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        fromColor = in.readInt();
        width = in.readInt();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFromColor() {
        return fromColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(x);
        parcel.writeInt(y);
        parcel.writeInt(fromColor);
        parcel.writeInt(width);
    }

    public int getWidth() {
        return width;
    }
}
