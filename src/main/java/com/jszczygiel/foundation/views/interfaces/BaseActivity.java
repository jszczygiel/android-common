package com.jszczygiel.foundation.views.interfaces;


public interface BaseActivity<T>{
    void setUpPresenter(T presenter);

    T getPresenter();

    boolean isAvailable();

    boolean isTablet();
}
