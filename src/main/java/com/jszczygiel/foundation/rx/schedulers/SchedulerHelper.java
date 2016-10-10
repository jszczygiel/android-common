package com.jszczygiel.foundation.rx.schedulers;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {

    private static final Scheduler databaseWriterScheduler;

    private static final Scheduler applicationScheduler;

    static {
        databaseWriterScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
        applicationScheduler = Schedulers.from(Executors.newFixedThreadPool(4));
    }

    private SchedulerHelper() {
    }

    public static Scheduler getApplicationScheduler() {
        return applicationScheduler;
    }

    public static Scheduler getDatabaseWriterScheduler() {
        return databaseWriterScheduler;
    }
}
