package com.jszczygiel.foundation.views.interfaces;

import android.content.Context;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

public interface BaseFragment<T extends BasePresenter> {
    void setUpPresenter(T presenter);

    T getPresenter();

    boolean isAvailable();

    Context getContext();

    boolean isTablet();

    void finish();
}
