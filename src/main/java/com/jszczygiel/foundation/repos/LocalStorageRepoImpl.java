package com.jszczygiel.foundation.repos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.jszczygiel.foundation.containers.Tuple;
import com.jszczygiel.foundation.enums.SubjectAction;
import com.jszczygiel.foundation.helpers.LoggerHelper;
import com.jszczygiel.foundation.json.JsonMapper;
import com.jszczygiel.foundation.repos.interfaces.BaseModel;
import com.jszczygiel.foundation.repos.interfaces.Repo;
import com.jszczygiel.foundation.rx.PublishSubject;
import com.jszczygiel.foundation.rx.schedulers.SchedulerHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.json.JSONException;

import java.util.List;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class LocalStorageRepoImpl<T extends BaseModel> implements Repo<T> {

    private static final int DATA_COLUMN = 1;
    protected static final String ID = "ID";
    protected static final String DATA = "DATA";
    private final PublishSubject<Tuple<Integer, T>> subject;
    private final PublishSubject<List<T>> collectionSubject;
    private final BriteDatabase database;

    public LocalStorageRepoImpl(SQLiteOpenHelper sqliteHelper) {

        database = SqlBrite.create().wrapDatabaseHelper(sqliteHelper, Schedulers.io());

        collectionSubject = PublishSubject.createWith(PublishSubject.BUFFER);
        subject = PublishSubject.createWith(PublishSubject.BUFFER);
    }

    public abstract Class<T> getType();

    public abstract String getTableName();

    @Override
    public Observable<T> get(String id) {
        LoggerHelper.logDebug("local:" + this.getClass().toString() + " get:" + id);
        return database.createQuery(getTableName(), "SELECT * FROM " + getTableName() + " WHERE id = ?", id)
                .take(1)
                .switchMap(new Func1<SqlBrite.Query, Observable<? extends T>>() {
                    @Override
                    public Observable<? extends T> call(final SqlBrite.Query map) {
                        return Observable.fromEmitter(new Action1<AsyncEmitter<T>>() {
                            @Override
                            public void call(AsyncEmitter<T> emitter) {
                                Cursor cursor = map.run();
                                if (cursor.moveToFirst()) {
                                    try {
                                        emitter.onNext(JsonMapper.INSTANCE.fromJson(cursor.getString(DATA_COLUMN), LocalStorageRepoImpl.this.getType()));
                                    } catch (JSONException e) {
                                        emitter.onError(e);
                                        return;
                                    }
                                }
                                emitter.onCompleted();
                            }
                        }, AsyncEmitter.BackpressureMode.LATEST);
                    }
                }).observeOn(Schedulers.newThread());
    }

    @Override
    public Observable<T> getAll() {
        LoggerHelper.logDebug("local:" + this.getClass().toString() + " getAll");
        return database.createQuery(getTableName(), "SELECT * FROM " + getTableName())
                .take(1)
                .switchMap(new Func1<SqlBrite.Query, Observable<? extends T>>() {
                    @Override
                    public Observable<? extends T> call(final SqlBrite.Query map) {
                        return Observable.fromEmitter(new Action1<AsyncEmitter<T>>() {
                            @Override
                            public void call(AsyncEmitter<T> emitter) {
                                Cursor cursor = map.run();
                                while (cursor.moveToNext()) {
                                    try {
                                        emitter.onNext(JsonMapper.INSTANCE.fromJson(cursor.getString(DATA_COLUMN), LocalStorageRepoImpl.this.getType()));
                                    } catch (JSONException e) {
                                        emitter.onError(e);
                                        return;
                                    }
                                }
                                emitter.onCompleted();
                            }
                        }, AsyncEmitter.BackpressureMode.BUFFER);
                    }
                }).observeOn(Schedulers.newThread());
    }

    @Override
    public synchronized void add(T model) {
        LoggerHelper.logDebug("local:" + this.getClass().toString() + " add:" + model);
        ContentValues values = new ContentValues();
        values.put(ID, model.getId());
        values.put(DATA, JsonMapper.INSTANCE.toJson(model));
        database.insert(getTableName(), values);
        subject.onNext(new Tuple<>(SubjectAction.ADDED, model));

    }

    @Override
    public Observable<T> remove(final String id) {
        LoggerHelper.logDebug("local:" + this.getClass().toString() + " remove:" + id);
        return get(id).observeOn(SchedulerHelper.getDatabaseWriterScheduler()).map(new Func1<T, T>() {
            @Override
            public T call(T map) {
                database.delete(getTableName(), "id = ?", id);
                subject.onNext(new Tuple<>(SubjectAction.REMOVED, map));
                return map;
            }
        }).observeOn(Schedulers.newThread());
    }

    @Override
    public synchronized void update(T model) {
        LoggerHelper.logDebug("local:" + this.getClass().toString() + " update:" + model);
        ContentValues values = new ContentValues();
        values.put(ID, model.getId());
        values.put(DATA, JsonMapper.INSTANCE.toJson(model));
        database.update(getTableName(), values, "id = ?", model.getId());
        subject.onNext(new Tuple<>(SubjectAction.CHANGED, model));
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
        database.delete(getTableName(), "");
    }

    @Override
    public void notify(T model) {
        subject.onNext(new Tuple<>(SubjectAction.CHANGED, model));
    }
}