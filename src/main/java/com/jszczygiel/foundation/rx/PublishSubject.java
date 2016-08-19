package com.jszczygiel.foundation.rx;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.operators.BackpressureUtils;
import rx.internal.util.atomic.SpscLinkedAtomicQueue;
import rx.subjects.Subject;

public class PublishSubject<T> extends Subject<T, T> {

    public final static int DROP = 0;
    public final static int BUFFER = 1;
    public final static int ERROR = 2;
    final State<T> state;                            // (2)

    protected PublishSubject(State<T> state) {       // (3)
        super(state);
        this.state = state;
    }

    public static <T> PublishSubject<T> createWith(@BackPressureStrategy int strategy) {
        State<T> state = new State<>(strategy);
        return new PublishSubject<>(state);
    }

    @Override
    public void onCompleted() {
        state.onCompleted();
    }

    @Override
    public boolean hasObservers() {
        return state.subscribers != null;
    }    @Override
    public void onNext(T t) {
        state.onNext(t);
    }

    @IntDef({BUFFER, ERROR, DROP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BackPressureStrategy {
    }    @Override
    public void onError(Throwable e) {
        state.onError(e);
    }

    static class State<T> implements OnSubscribe<T>, Observer<T>, Producer, Subscription {
        @SuppressWarnings("rawtypes")
        static final SubscriberState[] EMPTY = new SubscriberState[0];
        @SuppressWarnings("rawtypes")
        static final SubscriberState[] TERMINATED =
                new SubscriberState[0];
        final int strategy;
        @SuppressWarnings("unchecked")
        volatile SubscriberState<T>[] subscribers = EMPTY;
        volatile boolean done;
        Throwable error;

        public State(int strategy) {
            this.strategy = strategy;
        }

        @Override
        public void call(Subscriber<? super T> t) {
            SubscriberState<T> innerState =
                    new SubscriberState<>(t, this);
            t.add(innerState);
            t.setProducer(innerState);

            if (add(innerState)) {
                if (strategy == BUFFER) {
                    innerState.drain();
                } else if (innerState.unsubscribed) {
                    remove(innerState);
                }
            } else {
                Throwable e = error;
                if (e != null) {
                    t.onError(e);
                } else {
                    t.onCompleted();
                }
            }
        }

        boolean add(SubscriberState<T> subscriber) {
            synchronized (this) {
                SubscriberState<T>[] a = subscribers;
                if (a == TERMINATED) {
                    return false;
                }
                int n = a.length;

                @SuppressWarnings("unchecked")
                SubscriberState<T>[] b = new SubscriberState[n + 1];

                System.arraycopy(a, 0, b, 0, n);
                b[n] = subscriber;
                subscribers = b;
                return true;
            }
        }

        @SuppressWarnings("unchecked")
        void remove(SubscriberState<T> subscriber) {
            synchronized (this) {
                SubscriberState<T>[] a = subscribers;
                if (a == TERMINATED || a == EMPTY) {
                    return;
                }
                int n = a.length;

                int j = -1;
                for (int i = 0; i < n; i++) {
                    if (a[i] == subscriber) {
                        j = i;
                        break;
                    }
                }

                if (j < 0) {
                    return;
                }
                SubscriberState<T>[] b;
                if (n == 1) {
                    b = EMPTY;
                } else {
                    b = new SubscriberState[n - 1];
                    System.arraycopy(a, 0, b, 0, j);
                    System.arraycopy(a, j + 1, b, j, n - j - 1);
                }
                subscribers = b;
            }
        }        @SuppressWarnings("unchecked")
        SubscriberState<T>[] terminate() {
            synchronized (this) {
                SubscriberState<T>[] a = subscribers;
                if (a != TERMINATED) {
                    subscribers = TERMINATED;
                }
                return a;
            }
        }

        @Override
        public void request(long n) {
            for (SubscriberState<T> innerState : subscribers) {
                innerState.request(n);
            }
        }

        @Override
        public void unsubscribe() {
            for (SubscriberState<T> innerState : subscribers) {
                innerState.unsubscribe();
            }
        }        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            for (SubscriberState<T> innerState : subscribers) {
                innerState.onNext(t);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (done) {
                return;
            }
            error = e;
            done = true;
            for (SubscriberState<T> innerState : terminate()) {
                innerState.onError(e);
            }
        }

        @Override
        public void onCompleted() {
            if (done) {
                return;
            }
            done = true;
            for (SubscriberState<T> innerState : terminate()) {
                innerState.onCompleted();
            }
        }





        @Override
        public boolean isUnsubscribed() {
            boolean unsubscribed = false;

            for (SubscriberState<T> innerState : terminate()) {
                if (innerState.isUnsubscribed()) {
                    unsubscribed = true;
                    break;
                }
            }
            return unsubscribed;
        }
    }

    static final class SubscriberState<T> implements Producer, Subscription, Observer<T> {
        final Subscriber<? super T> child;
        final State<T> state;
        final int strategy;

        final AtomicLong requested = new AtomicLong();

        final AtomicInteger wip = new AtomicInteger();
        final Queue<T> queue;
        volatile boolean unsubscribed;
        volatile boolean done;
        Throwable error;

        public SubscriberState(
                Subscriber<? super T> child, State<T> state) {
            this.child = child;
            this.state = state;
            this.strategy = state.strategy;
            Queue<T> q = null;
            if (strategy == BUFFER) {
                q = new SpscLinkedAtomicQueue<>();
            }
            this.queue = q;
        }

        @Override
        public void onNext(T t) {
            if (unsubscribed) {
                return;
            }
            switch (strategy) {
                case BUFFER:
                    queue.offer(t);                               // (1)
                    drain();
                    break;
                case DROP: {
                    long r = requested.get();                     // (2)
                    if (r != 0L) {
                        child.onNext(t);
                        if (r != Long.MAX_VALUE) {
                            requested.decrementAndGet();
                        }
                    }
                    break;
                }
                case ERROR: {
                    long r = requested.get();                     // (3)
                    if (r != 0L) {
                        child.onNext(t);
                        if (r != Long.MAX_VALUE) {
                            requested.decrementAndGet();
                        }
                    } else {
                        unsubscribe();
                        child.onError(
                                new MissingBackpressureException());
                    }

                    break;
                }
                default:
            }
        }

        @Override
        public void onError(Throwable e) {
            if (unsubscribed) {
                return;
            }
            if (strategy == BUFFER) {
                error = e;
                done = true;
                drain();
            } else {
                child.onError(e);
            }
        }

        @Override
        public void onCompleted() {
            if (unsubscribed) {
                return;
            }
            if (strategy == BUFFER) {
                done = true;
                drain();
            } else {
                child.onCompleted();
            }
        }

        @Override
        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException();
            }
            if (n > 0) {
                BackpressureUtils.getAndAddRequest(requested, n);
                if (strategy == BUFFER) {
                    drain();
                }
            }
        }

        @Override
        public boolean isUnsubscribed() {
            return unsubscribed;
        }

        @Override
        public void unsubscribe() {
            if (!unsubscribed) {
                unsubscribed = true;
                state.remove(this);
                if (strategy == BUFFER) {
                    if (wip.getAndIncrement() == 0) {
                        queue.clear();
                    }
                }
            }
        }

        void drain() {
            if (wip.getAndIncrement() != 0) {
                return;
            }

            int missed = 1;

            Queue<T> q = queue;
            Subscriber child = this.child;

            for (; ; ) {

                if (checkTerminated(done, q.isEmpty(), child)) {
                    return;
                }

                long r = requested.get();
                boolean unbounded = r == Long.MAX_VALUE;
                long e = 0L;

                while (r != 0) {
                    boolean d = done;
                    T v = q.poll();
                    boolean empty = v == null;

                    if (checkTerminated(d, empty, child)) {
                        return;
                    }

                    if (empty) {
                        break;
                    }

                    child.onNext(v);

                    r--;
                    e--;
                }

                if (e != 0) {
                    if (!unbounded) {
                        requested.addAndGet(e);
                    }
                }

                missed = wip.addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            }
        }

        boolean checkTerminated(boolean done,
                                boolean empty,
                                Subscriber<? super T> child) {
            if (unsubscribed) {
                queue.clear();                     // (1)
                state.remove(this);
                return true;
            }
            if (done && empty) {
                unsubscribed = true;               // (2)
                Throwable e = error;
                if (e != null) {
                    child.onError(e);
                } else {
                    child.onCompleted();
                }
                return true;
            }
            return false;
        }
    }




}