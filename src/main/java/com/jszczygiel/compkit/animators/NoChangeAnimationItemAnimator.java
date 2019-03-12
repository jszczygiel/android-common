package com.jszczygiel.compkit.animators;

import androidx.recyclerview.widget.DefaultItemAnimator;

public class NoChangeAnimationItemAnimator extends DefaultItemAnimator {

  public NoChangeAnimationItemAnimator() {
    setSupportsChangeAnimations(false);
  }
}
