package com.jszczygiel.foundation.views.interfaces;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

import rx.Subscription;

public interface BaseFragment<T extends BasePresenter> {
    void setUpPresenter(T presenter);

    T getPresenter();

    boolean isAvailable();

    Context getContext();

    boolean isTablet();

    void finish();

    void setResult(int resultCode, Intent data);

    void setResult(int resultCode);

    FragmentActivity getActivity();

    void showToast(@StringRes int resId, String... formatArgs);

    void showToast(@PluralsRes int id, int quantity, String... formatArgs);

    void addSubscriptionToLifeCycle(Subscription subscription);

    void removeSubscriptionFromLifeCycle(Subscription subscription);
}
