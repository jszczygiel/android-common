package com.jszczygiel.foundation.helpers;

public class CompareHelper {
  private CompareHelper() {}

  public static int compare(long lhs, long rhs) {
    return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
  }

  public static int compare(int lhs, int rhs) {
    return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
  }

  public static int compareInverse(long lhs, long rhs) {
    return lhs < rhs ? 1 : (lhs == rhs ? 0 : -1);
  }
}
