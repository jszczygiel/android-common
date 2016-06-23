package com.jszczygiel.foundation.containers;

import android.support.annotation.NonNull;

import com.jszczygiel.foundation.helpers.CompareHelper;

public class Triple<T1, T2, T3> implements Comparable<Triple<T1, T2, T3>> {
    private final T1 o1;
    private final T2 o2;
    private final T3 o3;

    public Triple(T1 o1, T2 o2, T3 o3) {
        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
    }

    public T1 getFirst() {
        return o1;
    }

    public T2 getSecond() {
        return o2;
    }

    public T3 getThird() {
        return o3;
    }

    public boolean isOk() {
        return o1 != null && o2 != null && o3 != null;
    }

    @Override
    public int compareTo(@NonNull Triple<T1, T2, T3> another) {
        return CompareHelper.compare(hashCode(), another.hashCode());
    }
}
