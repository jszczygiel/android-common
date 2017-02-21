package com.jszczygiel.foundation.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface ListAction {
  int ADD = 0;
  int REMOVE = 1;
  int CLEAR_ALL = 2;
  int SCROLL_TO_TOP = 3;

  @IntDef({ADD, REMOVE, CLEAR_ALL})
  @Retention(RetentionPolicy.SOURCE)
  @interface Type {
  }
}
