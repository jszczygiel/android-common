package com.jszczygiel.foundation.rx.schedulers;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {

    private static final Scheduler databaseReaderScheduler;
    private static final Scheduler databaseWriterScheduler;

    static {
        databaseReaderScheduler = Schedulers.from(Executors.newFixedThreadPool(3));
        databaseWriterScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
    }

    private SchedulerHelper() {
    }

    public static Scheduler getDatabaseReaderScheduler() {
        return databaseReaderScheduler;
    }

    public static Scheduler getDatabaseWriterScheduler() {
        return databaseWriterScheduler;
    }
}
