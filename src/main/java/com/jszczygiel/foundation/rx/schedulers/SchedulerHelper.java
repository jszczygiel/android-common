package com.jszczygiel.foundation.rx.schedulers;

import com.jszczygiel.foundation.helpers.SystemHelper;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {

    private static final Scheduler databaseScheduler;

    static {
        databaseScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
    }

    private SchedulerHelper() {
    }

    public static Scheduler uiScheduler() {
        return Schedulers.newThread();
    }

    public static Scheduler parserScheduler() {
        return Schedulers.newThread();
    }

    public static Scheduler getDatabaseScheduler() {
        return databaseScheduler;
    }
}
