package com.jszczygiel.compkit.adapter;

import android.os.Parcelable;

public abstract class BaseViewModel implements Parcelable {

  public static final String ID_NOT_SET = "";
  public static final int NOT_SET = -1;

  public abstract String id();

  public abstract int modelType();

  protected abstract static class Builder<T> {

    public abstract T setId(String value);

    public abstract T setModelType(int value);
  }

}