package com.jszczygiel.foundation.views.interfaces;

import android.support.annotation.CallSuper;

public interface BaseActivity<T>{
    void setUpPresenter(T presenter);

    T getPresenter();

    boolean isAvailable();

    boolean isTablet();
}
