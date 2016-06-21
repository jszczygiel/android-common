package com.jszczygiel.foundation.repos;

import android.text.TextUtils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jszczygiel.foundation.containers.Tuple;
import com.jszczygiel.foundation.enums.SubjectAction;
import com.jszczygiel.foundation.repos.interfaces.BaseModel;
import com.jszczygiel.foundation.repos.interfaces.Repo;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public abstract class FirebaseRepoImpl<T extends BaseModel> implements Repo<T> {

    private final DatabaseReference table;
    private String userId;

    public FirebaseRepoImpl() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        table = database.getReference(getTableName());

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public abstract String getTableName();

    public abstract Class<T> getType();

    private void checkPreConditions() {
        if (TextUtils.isEmpty(userId)) {
            throw new DatabaseException("no valid userId");
        }
    }

    @Override
    public Observable<T> get(final String id) {
        checkPreConditions();
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                table.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(dataSnapshot.getValue(getType()));
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(new DatabaseException(databaseError.getMessage()));
                    }
                });
            }
        });
    }

    @Override
    public Observable<T> getAll() {
        checkPreConditions();
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                table.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            subscriber.onNext(snapshot.getValue(getType()));
                        }
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        subscriber.onError(new DatabaseException(databaseError.getMessage()));
                    }
                });
            }
        });
    }

    @Override
    public void add(T model) {
        checkPreConditions();
        table.child(userId).child(model.getId()).setValue(model);
    }

    @Override
    public Observable<T> remove(final String id) {
        checkPreConditions();
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                table.child(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        subscriber.onNext(dataSnapshot.getValue(getType()));
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                table.child(userId).child(id).removeValue();
            }
        });
    }

    @Override
    public void update(T model) {
        checkPreConditions();
        table.child(userId).child(model.getId()).setValue(model);
    }

    @Override
    public Observable<Tuple<Integer, T>> observe() {
        checkPreConditions();
        return Observable.create(new Observable.OnSubscribe<Tuple<Integer, T>>() {
            @Override
            public void call(final Subscriber<? super Tuple<Integer, T>> subscriber) {
                table.child(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        subscriber.onNext(new Tuple<>(SubjectAction.ADDED, dataSnapshot.getValue(getType())));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        subscriber.onNext(new Tuple<>(SubjectAction.CHANGED, dataSnapshot.getValue(getType())));
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        subscriber.onNext(new Tuple<>(SubjectAction.REMOVED, dataSnapshot.getValue(getType())));
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public Observable<List<T>> observeAll() {
        checkPreConditions();
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(final Subscriber<? super List<T>> subscriber) {
                table.child(userId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        subscriber.onNext(getAll().toList().toBlocking().first());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        subscriber.onNext(getAll().toList().toBlocking().first());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void clear() {
        checkPreConditions();
        table.child(userId).removeValue();
    }
}
