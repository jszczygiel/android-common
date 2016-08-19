package com.jszczygiel.foundation.rx.retry;

import rx.Observable;
import rx.functions.Func1;

/**
 * Combine a {@link Retry#errorsWithAttempts(Observable, int) mapping of errors to their attempt number} with
 * a flatmap that {@link RetryWithDelayHandler induces a retry delay} into a function that can be passed to
 * an Observable's {@link Observable#retryWhen(Func1) retryWhen operation}.
 *
 * @author Simon Baslé
 * @see RetryBuilder how to construct such a function in a fluent manner.
 * @since 2.1
 */
public class RetryWhenFunction implements Func1<Observable<? extends Throwable>, Observable<?>> {

    protected RetryWithDelayHandler handler;

    public RetryWhenFunction(RetryWithDelayHandler handler) {
        this.handler = handler;
    }

    public Observable<?> call(Observable<? extends Throwable> errors) {
        return Retry.errorsWithAttempts(errors, handler.maxAttempts + 1)
                .flatMap(handler);
    }
}