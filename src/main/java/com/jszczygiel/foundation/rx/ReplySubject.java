package com.jszczygiel.foundation.rx;

import rx.Observable;

public class ReplySubject<T> {
  private final PublishSubject<T> subject;
  private T value;

  public ReplySubject(T value) {
    this.value = value;
    subject = PublishSubject.createWith(PublishSubject.BUFFER);
  }

  public Observable<T> observe() {
    return Observable.merge(Observable.just(value), subject);
  }

  public void changed(T value) {
    this.value = value;
    subject.onNext(value);
  }

  public T getValue() {
    return value;
  }
}
