package com.jszczygiel.foundation.helpers;

public class StringHelper {
  public static String decapitalize(String string) {
    if (string == null || string.length() == 0) {
      return string;
    }
    char c[] = string.toCharArray();
    c[0] = Character.toLowerCase(c[0]);
    return new String(c);
  }
}
