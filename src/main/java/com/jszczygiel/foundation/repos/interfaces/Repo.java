package com.jszczygiel.foundation.repos.interfaces;

import com.jszczygiel.foundation.containers.Tuple;

import java.util.List;

import rx.Observable;

public interface Repo<T extends BaseModel> {

  Observable<T> get(String id);

  Observable<T> getAll();

  Observable<T> remove(String id);

  Observable<Tuple<Integer, T>> observe();

  Observable<List<T>> observeAll();

  void clear();

  void notify(T model);

  void put(T model);
}
