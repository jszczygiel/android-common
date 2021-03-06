package com.jszczygiel.foundation.repos;

import android.content.ContentValues;
import android.database.Cursor;
import com.jszczygiel.foundation.containers.Tuple;
import com.jszczygiel.foundation.enums.SubjectAction;
import com.jszczygiel.foundation.helpers.L;
import com.jszczygiel.foundation.json.JsonMapper;
import com.jszczygiel.foundation.repos.interfaces.BaseModel;
import com.jszczygiel.foundation.repos.interfaces.Repo;
import com.jszczygiel.foundation.rx.schedulers.SchedulerHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import dagger.Lazy;
import java.io.IOException;
import java.util.List;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Cancellable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public abstract class LocalStorageRepoImpl<T extends BaseModel> implements Repo<T> {

  protected static final String ID = "ID";
  protected static final String DATA = "DATA";
  private static final int DATA_COLUMN = 1;
  private final PublishSubject<Tuple<Integer, T>> subject;
  private final PublishSubject<List<T>> collectionSubject;
  private final Lazy<BriteDatabase> database;

  public LocalStorageRepoImpl(Lazy<BriteDatabase> database) {
    this.database = database;

    this.collectionSubject = PublishSubject.create();
    this.subject = PublishSubject.create();
  }

  public abstract Class<T> getType();

  public abstract String getTableName();

  @Override
  public Observable<T> get(String id) {
    L.d("local:" + this.getClass().toString() + " get:" + id);
    return database
        .get()
        .createQuery(getTableName(), "SELECT * FROM " + getTableName() + " WHERE id = ?", id)
        .take(1)
        .switchMap(
            new Func1<SqlBrite.Query, Observable<? extends T>>() {
              @Override
              public Observable<? extends T> call(final SqlBrite.Query map) {
                return Observable.create(
                    new Action1<Emitter<T>>() {
                      @Override
                      public void call(Emitter<T> emitter) {
                        final Cursor cursor = map.run();
                        emitter.setCancellation(
                            new Cancellable() {
                              @Override
                              public void cancel() throws Exception {
                                cursor.close();
                              }
                            });
                        if (!cursor.isClosed() && cursor.moveToFirst()) {
                          try {
                            emitter.onNext(
                                JsonMapper.INSTANCE.fromJson(
                                    cursor.getString(DATA_COLUMN),
                                    LocalStorageRepoImpl.this.getType()));
                          } catch (IOException e) {
                            emitter.onError(e);
                            return;
                          }
                        }
                        emitter.onCompleted();
                      }
                    },
                    Emitter.BackpressureMode.LATEST);
              }
            });
  }

  @Override
  public Observable<T> getAll() {
    L.d("local:" + this.getClass().toString() + " getAll");
    return database
        .get()
        .createQuery(getTableName(), "SELECT * FROM " + getTableName())
        .take(1)
        .switchMap(
            new Func1<SqlBrite.Query, Observable<? extends T>>() {
              @Override
              public Observable<? extends T> call(final SqlBrite.Query map) {
                return Observable.create(
                    new Action1<Emitter<T>>() {
                      @Override
                      public void call(Emitter<T> emitter) {
                        final Cursor cursor = map.run();
                        emitter.setCancellation(
                            new Cancellable() {
                              @Override
                              public void cancel() throws Exception {
                                cursor.close();
                              }
                            });
                        while (!cursor.isClosed() && cursor.moveToNext()) {
                          try {
                            emitter.onNext(
                                JsonMapper.INSTANCE.fromJson(
                                    cursor.getString(DATA_COLUMN),
                                    LocalStorageRepoImpl.this.getType()));
                          } catch (IOException e) {
                            emitter.onError(e);
                            return;
                          }
                        }
                        emitter.onCompleted();
                      }
                    },
                    Emitter.BackpressureMode.BUFFER);
              }
            });
  }

  protected synchronized void add(T model) {
    L.d("local:" + this.getClass().toString() + " add:" + model);
    ContentValues values = new ContentValues();
    values.put(ID, model.id());
    values.put(DATA, JsonMapper.INSTANCE.toJson(model));
    database.get().insert(getTableName(), values);
    subject.onNext(new Tuple<>(SubjectAction.ADDED, model));
  }

  @Override
  public Observable<T> remove(final String id) {
    L.d("local:" + this.getClass().toString() + " remove:" + id);
    return get(id)
        .observeOn(SchedulerHelper.databaseWriterScheduler())
        .map(
            new Func1<T, T>() {
              @Override
              public T call(T map) {
                database.get().delete(getTableName(), "id = ?", id);
                subject.onNext(new Tuple<>(SubjectAction.REMOVED, map));
                return map;
              }
            });
  }

  protected synchronized void update(T model) {
    L.d("local:" + this.getClass().toString() + " update:" + model);
    ContentValues values = new ContentValues();
    values.put(ID, model.id());
    values.put(DATA, JsonMapper.INSTANCE.toJson(model));
    database.get().update(getTableName(), values, "id = ?", model.id());
    subject.onNext(new Tuple<>(SubjectAction.CHANGED, model));
  }

  @Override
  public void put(final T model) {
    L.d("local:" + this.getClass().toString() + " put:" + model);
    database
        .get()
        .createQuery(
            getTableName(), "SELECT * FROM " + getTableName() + " WHERE id" + " = ?", model.id())
        .take(1)
        .map(
            new Func1<SqlBrite.Query, Integer>() {
              @Override
              public Integer call(SqlBrite.Query map) {
                Cursor cursor = map.run();
                int count = cursor.getCount();
                cursor.close();
                return count;
              }
            })
        .subscribe(
            new Action1<Integer>() {
              @Override
              public void call(Integer count) {
                if (count == 0) {
                  add(model);
                } else {
                  update(model);
                }
              }
            });
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
    database.get().delete(getTableName(), "");
  }

  @Override
  public void notify(T model) {
    subject.onNext(new Tuple<>(SubjectAction.CHANGED, model));
  }
}
