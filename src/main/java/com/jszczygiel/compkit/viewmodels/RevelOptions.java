package com.jszczygiel.compkit.viewmodels;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.jszczygiel.compkit.animators.AnimationHelper;
import com.jszczygiel.foundation.containers.Tuple;
import com.jszczygiel.foundation.views.RevelSimpleFragmentActivityImpl;

public class RevelOptions implements Parcelable {
    final int x;
    final int y;
    final int fromColor;

    public RevelOptions(int y, int x) {
        this.y = y;
        this.x = x;
        this.fromColor = Color.TRANSPARENT;
    }

    public RevelOptions(int x, int y, int fromColor) {
        this.x = x;
        this.y = y;
        this.fromColor = fromColor;
    }

    public RevelOptions(View view) {
        this.fromColor = AnimationHelper.getColor(view);
        Tuple<Integer, Integer> positon = AnimationHelper.getCenter(view);
        this.x = positon.getFirst();
        this.y = positon.getSecond();
    }

    protected RevelOptions(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        fromColor = in.readInt();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(x);
        parcel.writeInt(y);
        parcel.writeInt(fromColor);
    }

}
