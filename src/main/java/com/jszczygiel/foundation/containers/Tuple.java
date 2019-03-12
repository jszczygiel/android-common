package com.jszczygiel.foundation.containers;

import androidx.annotation.NonNull;
import com.jszczygiel.foundation.helpers.CompareHelper;

public final class Tuple<T1, T2> implements Comparable<Tuple<T1, T2>> {
  private final T1 o1;
  private final T2 o2;

  public Tuple(T1 o1, T2 o2) {
    this.o1 = o1;
    this.o2 = o2;
  }

  public T1 getFirst() {
    return o1;
  }

  public T2 getSecond() {
    return o2;
  }

  @Override
  public int compareTo(@NonNull Tuple<T1, T2> another) {
    return CompareHelper.compare(hashCode(), another.hashCode());
  }

  @Override
  public int hashCode() {
    int result = o1 != null ? o1.hashCode() : 0;
    result = 31 * result + (o2 != null ? o2.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Tuple<?, ?> tuple = (Tuple<?, ?>) o;

    return o1 != null
        ? o1.equals(tuple.o1)
        : tuple.o1 == null && (o2 != null ? o2.equals(tuple.o2) : tuple.o2 == null);
  }
}
