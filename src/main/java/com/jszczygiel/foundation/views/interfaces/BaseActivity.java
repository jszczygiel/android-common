package com.jszczygiel.foundation.views.interfaces;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

public interface BaseActivity<T extends BasePresenter>{
    void setUpPresenter(T presenter);

    T getPresenter();

    boolean isAvailable();

    boolean isTablet();
}
