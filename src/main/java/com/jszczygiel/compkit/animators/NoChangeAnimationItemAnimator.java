package com.jszczygiel.compkit.animators;

import android.support.v7.widget.DefaultItemAnimator;

public class NoChangeAnimationItemAnimator extends DefaultItemAnimator {

  public NoChangeAnimationItemAnimator() {
    setSupportsChangeAnimations(false);
  }
}
