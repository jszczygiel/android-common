package com.jszczygiel.foundation.presenters;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenterImpl<T> implements BasePresenter<T> {

  protected boolean isTablet;
  protected int orientation;
  T view;
  CompositeSubscription subscriptionList;

  @CallSuper
  @Override
  public void onAttach(T view, Bundle arguments) {
    subscriptionList = new CompositeSubscription();
    this.view = view;
  }

  @CallSuper
  @Override
  public void onDetach() {
    this.view = null;
    subscriptionList.unsubscribe();
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
      subscriptionList.remove(subscription);
    }
  }

  @Override
  public void clearSubscriptions() {
    subscriptionList.clear();
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