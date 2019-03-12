package com.jszczygiel.foundation.enums;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface SubjectAction {

  int ADDED = 0;
  int CHANGED = 1;
  int REMOVED = 2;

  @IntDef({ADDED, CHANGED, REMOVED})
  @Retention(RetentionPolicy.SOURCE)
  @interface Type {}
}
