package com.jszczygiel.foundation.repos;

import com.jszczygiel.foundation.containers.Tuple;
import com.jszczygiel.foundation.enums.SubjectAction;
import com.jszczygiel.foundation.repos.interfaces.BaseModel;
import com.jszczygiel.foundation.repos.interfaces.Repo;
import com.jszczygiel.foundation.rx.PublishSubject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.functions.Func1;

public abstract class MemoryRepoImpl<T extends BaseModel> implements Repo<T> {

    private static final boolean NOTIFY = true;
    private final PublishSubject<Tuple<Integer, T>> subject;
    private final PublishSubject<List<T>> collectionSubject;
    private final Map<String, T> models;

    protected MemoryRepoImpl() {
        models = new ConcurrentHashMap<>();
        collectionSubject = PublishSubject.createWith(PublishSubject.BUFFER);
        subject = PublishSubject.createWith(PublishSubject.BUFFER);
    }

    @Override
    public Observable<T> get(String id) {
        return Observable.just(models.get(id));
    }

    @Override
    public void add(T model) {
        models.put(model.getId(), model);
        subject.onNext(new Tuple<>(SubjectAction.ADDED, model));
    }

    @Override
    public Observable<T> remove(String id) {
        return Observable.just(models.remove(id)).map(new Func1<T, T>() {
            @Override
            public T call(T model) {
                subject.onNext(new Tuple<>(SubjectAction.REMOVED, model));
                return model;
            }
        });
    }

    @Override
    public void update(T model) {
        subject.onNext(new Tuple<>(SubjectAction.CHANGED, model));
    }

    @Override
    public void clear() {
        models.clear();
    }

    @Override
    public Observable<List<T>> observeAll() {
        return collectionSubject;
    }

    @Override
    public Observable<Tuple<Integer, T>> observe() {
        return subject;
    }

    @Override
    public Observable<T> getAll() {
        return Observable.from(models.values());
    }

    protected Map<String, T> getModels() {
        return models;
    }

}
