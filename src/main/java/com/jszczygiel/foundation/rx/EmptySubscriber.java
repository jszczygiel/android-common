package com.jszczygiel.foundation.rx;

import com.jszczygiel.foundation.helpers.LoggerHelper;

import rx.Subscriber;

public class EmptySubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        LoggerHelper.log(e);
    }

    @Override
    public void onNext(T t) {

    }
}
