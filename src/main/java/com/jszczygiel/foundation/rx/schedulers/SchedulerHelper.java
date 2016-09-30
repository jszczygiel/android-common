package com.jszczygiel.foundation.rx.schedulers;

import com.jszczygiel.foundation.helpers.SystemHelper;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {
    private static final Scheduler parserScheduler;

    private static final Scheduler uiScheduler;

    private static final Scheduler databaseReaderScheduler;
    private static final Scheduler databaseWriterScheduler;

    static {
        int cores = SystemHelper.getNumberOfCores();
        parserScheduler = Schedulers.from(Executors.newFixedThreadPool(cores * 2 - 1));
        uiScheduler = Schedulers.from(Executors.newFixedThreadPool(cores == 1 ? 2 : cores / 2 + 1));
        databaseReaderScheduler = Schedulers.from(Executors.newFixedThreadPool(2));
        databaseWriterScheduler = Schedulers.from(Executors.newFixedThreadPool(1));
    }

    private SchedulerHelper() {
    }

    public static Scheduler uiScheduler() {
        return uiScheduler;
    }

    public static Scheduler parserScheduler() {
        return parserScheduler;
    }

    public static Scheduler getDatabaseReaderScheduler() {
        return databaseReaderScheduler;
    }

    public static Scheduler getDatabaseWriterScheduler() {
        return databaseWriterScheduler;
    }
}
