package com.jszczygiel.foundation.containers;

public class Quadruple<T1, T2, T3, T4> {
  private final T1 o1;
  private final T2 o2;
  private final T3 o3;
  private final T4 o4;

  public Quadruple(T1 o1, T2 o2, T3 o3, T4 o4) {
    this.o1 = o1;
    this.o2 = o2;
    this.o3 = o3;
    this.o4 = o4;
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

  public T4 getFourth() {
    return o4;
  }

  public boolean isOk() {
    return o1 != null && o2 != null && o3 != null && o4 != null;
  }
}
