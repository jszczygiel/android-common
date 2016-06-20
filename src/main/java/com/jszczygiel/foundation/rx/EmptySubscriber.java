package com.jszczygiel.foundation.rx;

import com.livechat.foundation.helpers.Logger;

import rx.Subscriber;

public class EmptySubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Logger.log(e);
    }

    @Override
    public void onNext(T t) {

    }
}
