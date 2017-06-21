package com.jszczygiel.foundation.rx.schedulers;

import java.util.concurrent.Executors;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {

  private static final Scheduler databaseWriterScheduler;
  private static final Scheduler uiDelayScheduler;
  private static final Scheduler repoObserverScheduler;
  private static final Scheduler subscribeOn;

  static {
    databaseWriterScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
    uiDelayScheduler = Schedulers.from(Executors.newFixedThreadPool(4));
    repoObserverScheduler = Schedulers.from(Executors.newFixedThreadPool(4));
    subscribeOn = Schedulers.from(Executors.newFixedThreadPool(5));
  }

  private SchedulerHelper() {}

  public static Scheduler databaseWriterScheduler() {
    return databaseWriterScheduler;
  }

  public static Scheduler uiDelayScheduler() {
    return uiDelayScheduler;
  }

  public static Scheduler repoObserverScheduler() {
    return repoObserverScheduler;
  }

  public static Scheduler appScheduler() {
    return subscribeOn;
  }

  public static Scheduler computation() {
    return Schedulers.computation();
  }
}
