package com.jszczygiel.foundation.repos;

import android.database.sqlite.SQLiteOpenHelper;

import com.jakewharton.rxrelay.PublishRelay;
import com.jszczygiel.foundation.containers.Tuple;
import com.jszczygiel.foundation.enums.SubjectAction;
import com.jszczygiel.foundation.repos.interfaces.BaseModel;
import com.jszczygiel.foundation.repos.interfaces.Repo;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public abstract class LocalStorageRepoImpl<T extends BaseModel> implements Repo<T> {

    private final DefaultStorIOSQLite storIOSQLite;
    private final PublishRelay<List<T>> collectionSubject;
    private final PublishRelay<Tuple<Integer, T>> subject;

    public LocalStorageRepoImpl(SQLiteOpenHelper sqliteHelper) {
        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqliteHelper)
                .addTypeMapping(getType(), getTypeMapping()) // required for object mapping
                .build();

        collectionSubject = PublishRelay.create();
        subject = PublishRelay.create();
    }

    protected abstract SQLiteTypeMapping<T> getTypeMapping();

    public abstract Class<T> getType();

    public abstract String getTableName();

    @Override
    public Observable<T> get(String id) {
        return storIOSQLite.get().object(getType())
                .withQuery(Query.builder()
                        .table(getTableName())
                        .where("id = ?")
                        .whereArgs(id)
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<T> getAll() {
        return storIOSQLite.get().object(getType())
                .withQuery(Query.builder()
                        .table(getTableName())
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public void add(T model) {
        storIOSQLite.put()
                .object(model)
                .prepare()
                .executeAsBlocking();
        subject.call(new Tuple<>(SubjectAction.ADDED, model));

    }

    @Override
    public Observable<T> remove(String id) {
        return get(id).map(new Func1<T, T>() {
            @Override
            public T call(T map) {
                storIOSQLite.delete().object(map).prepare().executeAsBlocking();
                subject.call(new Tuple<>(SubjectAction.REMOVED, map));
                return map;
            }
        });
    }

    @Override
    public void update(T model) {
        add(model);
        subject.call(new Tuple<>(SubjectAction.CHANGED, model));
    }

    @Override
    public Observable<Tuple<Integer, T>> observe() {
        return subject;
    }

    @Override
    public Observable<List<T>> observeAll() {
        return collectionSubject;
    }

    @Override
    public void clear() {
        storIOSQLite.delete()
                .byQuery(DeleteQuery.builder()
                        .table(getTableName())
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Override
    public void notify(T model) {
        subject.call(new Tuple<>(SubjectAction.CHANGED, model));
    }
}
