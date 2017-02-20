package com.jszczygiel.compkit.adapter;

import android.os.Parcelable;

import java.util.Arrays;


public abstract class BaseViewModel implements Parcelable {

  public static final String ID_NOT_SET = "";
  public static final int NOT_SET = -1;

  public abstract String id();

  public abstract int modelType();

  protected abstract static class Builder<T> {

    public abstract T setId(String value);

    public abstract T setModelType(int value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof BaseViewModel)) {
      return false;
    }

    return equals(id(), ((BaseViewModel) obj).id()) && equals(modelType(), ((BaseViewModel) obj)
        .modelType());
  }

  public static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{id(), modelType()});
  }
}