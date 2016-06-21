package com.jszczygiel.foundation.rx.schedulers;

import com.jszczygiel.foundation.helpers.SystemHelper;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class SchedulerHelper {
    private static final Scheduler parserScheduler;

    private static final Scheduler uiScheduler;

    static {
        int cores = SystemHelper.getNumberOfCores();
        parserScheduler = Schedulers.from(Executors.newFixedThreadPool(cores * 2 - 1));
        uiScheduler = Schedulers.from(Executors.newFixedThreadPool(cores == 1 ? 1 : cores / 2));
    }

    private SchedulerHelper() {
    }

    public static Scheduler uiScheduler() {
        return uiScheduler;
    }

    public static Scheduler parserScheduler() {
        return parserScheduler;
    }
}
