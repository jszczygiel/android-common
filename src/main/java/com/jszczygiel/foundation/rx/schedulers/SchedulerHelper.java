package com.jszczygiel.foundation.rx.schedulers;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {

    private static final Scheduler databaseWriterScheduler;

    static {
        databaseWriterScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
    }

    private SchedulerHelper() {
    }

    public static Scheduler getDatabaseWriterScheduler() {
        return databaseWriterScheduler;
    }
}
