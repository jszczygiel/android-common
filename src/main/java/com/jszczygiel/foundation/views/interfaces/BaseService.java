package com.jszczygiel.foundation.views.interfaces;

import android.content.Intent;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

public interface BaseService<T extends BasePresenter> {

  void setUpPresenter(T presenter);

  T getPresenter();

  boolean isAvailable();

  boolean isTablet();

  void startActivity(Intent intent);
}
