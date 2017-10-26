package com.jszczygiel.foundation.rx;

import rx.Observable;
import rx.exceptions.CompositeException;
import rx.functions.Func1;

public final class DropBreadcrumb<T> implements Observable.Transformer<T, T> {
  @Override
  public Observable<T> call(Observable<T> upstream) {
    final BreadcrumbException breadcrumb = new BreadcrumbException();
    return upstream.onErrorResumeNext(
        new Func1<Throwable, Observable<? extends T>>() {
          @Override
          public Observable<? extends T> call(Throwable throwable) {
            throw new CompositeException(throwable, breadcrumb);
          }
        });
  }
}
