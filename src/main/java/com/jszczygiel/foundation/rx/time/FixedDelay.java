package com.jszczygiel.foundation.rx.time;

import java.util.concurrent.TimeUnit;

public class FixedDelay extends Delay {

  private final long delay;

  FixedDelay(long delay, TimeUnit unit) {
    super(unit);
    this.delay = delay;
  }

  @Override
  public long calculate(long attempt) {
    return delay;
  }

  @Override
  public String toString() {
    return "FixedDelay{" + delay + " " + unit() + "}";
  }
}