package com.jszczygiel.compkit.adapter;

import android.os.Parcelable;
import java.util.Arrays;

public abstract class BaseViewModel implements Parcelable {

  public static final int NOT_SET = -1;
  public static final String ID_NOT_SET = "";

  public abstract String id();

  public abstract int modelType();

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {id(), modelType()});
  }

  protected abstract static class BaseBuilder<T> {

    public abstract T setId(String value);

    public abstract T setModelType(int value);
  }
}
