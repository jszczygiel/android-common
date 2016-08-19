package com.jszczygiel.foundation.presenters;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

import java.util.HashSet;
import java.util.Set;

import rx.Subscription;

public abstract class BasePresenterImpl<T> implements BasePresenter<T> {

    protected boolean isTablet;
    protected int orientation;
    T view;
    Set<Subscription> subscriptionList;

    @CallSuper
    @Override
    public void onAttach(T view) {
        subscriptionList = new HashSet<>();
        this.view = view;
    }

    @CallSuper
    @Override
    public void onDetach() {
        this.view = null;
        for (Subscription subscription : subscriptionList) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    @Override
    public final boolean isViewAvailable() {
        return view != null;
    }

    @Override
    public final T getView() {
        return view;
    }

    @Override
    public void setIsTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    @Override
    public void addSubscriptionToLifeCycle(Subscription subscription) {
        subscriptionList.add(subscription);
    }

    @Override
    public void removeSubscriptionFromLifeCycle(Subscription subscription) {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscriptionList.remove(subscription);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isTablet() {
        return isTablet;
    }
}