package com.jszczygiel.foundation.rx;

import com.jszczygiel.foundation.rx.schedulers.SchedulerHelper;
import com.jszczygiel.foundation.rx.time.Delay;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.subscriptions.MultipleAssignmentSubscription;

public class OnSubscribeExponentialDelay implements Observable.OnSubscribe<Long> {
  static final long CLOCK_DRIFT_TOLERANCE_NANOS;

  static {
    CLOCK_DRIFT_TOLERANCE_NANOS = TimeUnit.MINUTES.toNanos(
        Long.getLong("rx.scheduler.drift-tolerance", 15));
  }

  final long initialDelay;
  final Delay period;
  final Scheduler scheduler;
  long counter;

  public OnSubscribeExponentialDelay() {
    this(10);

  }

  public OnSubscribeExponentialDelay(long initialDelay) {
    this(initialDelay, Delay.exponential(TimeUnit.SECONDS, 10 * 60, 10, 2));
  }

  public OnSubscribeExponentialDelay(long initialDelay, Delay period) {
    this(initialDelay, period, SchedulerHelper.computation());
  }

  public OnSubscribeExponentialDelay(long initialDelay, Delay period, Scheduler scheduler) {
    this.initialDelay = initialDelay;
    this.period = period;
    this.scheduler = scheduler;
  }

  @Override
  public void call(final Subscriber<? super Long> child) {
    final Scheduler.Worker worker = scheduler.createWorker();
    child.add(worker);
    schedulePeriodically(worker, new Action0() {

      @Override
      public void call() {
        try {
          child.onNext(counter++);
        } catch (Throwable e) {
          try {
            worker.unsubscribe();
          } finally {
            Exceptions.throwOrReport(e, child);
          }
        }
      }

    }, initialDelay, period, period.unit());
  }

  public Subscription schedulePeriodically(final Scheduler.Worker worker, final Action0 action,
                                           long initialDelay, Delay periodDelay,
                                           final TimeUnit unit) {
    final Delay period = periodDelay;
    final long firstNowNanos = TimeUnit.MILLISECONDS.toNanos(worker.now());
    final long firstStartInNanos = firstNowNanos + unit.toNanos(initialDelay);

    final MultipleAssignmentSubscription mas = new MultipleAssignmentSubscription();
    final Action0 recursiveAction = new Action0() {
      long count;
      long lastNowNanos = firstNowNanos;
      long startInNanos = firstStartInNanos;

      @Override
      public void call() {
        if (!mas.isUnsubscribed()) {
          action.call();
          long periodInNanos = unit.toNanos(period.calculate(count));
          long nextTick;

          long nowNanos = TimeUnit.MILLISECONDS.toNanos(worker.now());
          // If the clock moved in a direction quite a bit, rebase the repetition period
          if (nowNanos + CLOCK_DRIFT_TOLERANCE_NANOS < lastNowNanos
              || nowNanos >= lastNowNanos + periodInNanos +
              CLOCK_DRIFT_TOLERANCE_NANOS) {
            nextTick = nowNanos + periodInNanos;
                            /*
                             * Shift the start point back by the drift as if the whole thing
                             * started count periods ago.
                             */
            startInNanos = nextTick - (periodInNanos * (++count));
          } else {
            nextTick = startInNanos + (++count * periodInNanos);
          }
          lastNowNanos = nowNanos;

          long delay = nextTick - nowNanos;
          mas.set(worker.schedule(this, delay, TimeUnit.NANOSECONDS));
        }
      }
    };
    MultipleAssignmentSubscription s = new MultipleAssignmentSubscription();
    // Should call `mas.set` before `schedule`, or the new Subscription may replace the old one.
    mas.set(s);
    s.set(worker.schedule(recursiveAction, initialDelay, unit));
    return mas;
  }

}
