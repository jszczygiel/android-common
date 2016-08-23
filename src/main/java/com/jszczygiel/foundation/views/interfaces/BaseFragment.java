package com.jszczygiel.foundation.views.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

public interface BaseFragment<T extends BasePresenter> {
    void setUpPresenter(T presenter);

    T getPresenter();

    boolean isAvailable();

    Context getContext();

    boolean isTablet();

    void finish();

    void setResult(int resultCode, Intent data);

    void setResult(int resultCode);

    void showToast(int stringRes, String... formattedArgs);

    Activity getActivity();
}
