package com.jszczygiel.foundation.repos.interfaces;

import com.jszczygiel.foundation.containers.Tuple;

import java.util.List;

import rx.Observable;

public interface Repo<T> {

    Observable<T> get(String id);

    Observable<T> getAll();

    void add(T model);

    Observable<T> remove(String id);

    Observable<T> remove(String id, boolean notify);

    void changed(T model);

    void changed(String id);

    Observable<Tuple<Integer, T>> observe();

    Observable<List<T>> observeAll();

    void clear();
}
