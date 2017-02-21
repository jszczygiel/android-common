package com.jszczygiel.foundation.rx.time;

import java.util.concurrent.TimeUnit;

public class ExponentialDelay extends Delay {

  private final long lower;
  private final long upper;
  private final double growBy;
  private final int powersOf;

  public ExponentialDelay(TimeUnit unit, long upper, long lower, double growBy, int powersOf) {
    super(unit);
    this.lower = lower;
    this.upper = upper;
    this.growBy = growBy;
    this.powersOf = powersOf <= 2 ? 2 : powersOf;
  }

  @Override
  public long calculate(long attempt) {
    long calc;
    if (attempt <= 0) { //safeguard against underflow
      calc = 0;
    } else if (powersOf == 2) {
      calc = calculatePowerOfTwo(attempt);
    } else {
      calc = calculateAlternatePower(attempt);
    }

    return applyBounds(calc);
  }

  //fastpath with bitwise operator
  protected long calculatePowerOfTwo(long attempt) {
    long step;
    if (attempt >= 64) { //safeguard against overflow in the bitshift operation
      step = Long.MAX_VALUE;
    } else {
      step = (1L << (attempt - 1));
    }
    //round will cap at Long.MAX_VALUE
    return Math.round(step * growBy);
  }

  protected long calculateAlternatePower(long attempt) {
    //round will cap at Long.MAX_VALUE and pow should prevent overflows
    double step = Math.pow(this.powersOf, attempt - 1); //attempt > 0
    return Math.round(step * growBy);
  }

  private long applyBounds(long calculatedValue) {
    if (calculatedValue < lower) {
      return lower;
    }
    if (calculatedValue > upper) {
      return upper;
    }
    return calculatedValue;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ExponentialDelay{");
    sb.append("growBy ").append(growBy);
    sb.append(" " + unit());
    sb.append(", powers of ").append(powersOf);
    sb.append("; lower=").append(lower);
    sb.append(", upper=").append(upper);
    sb.append('}');
    return sb.toString();
  }
}