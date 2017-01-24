package com.jszczygiel.foundation.views;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;

import com.jszczygiel.foundation.helpers.SystemHelper;
import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;
import com.jszczygiel.foundation.views.interfaces.BaseActivity;

public abstract class BaseActivityImpl<T extends BasePresenter> extends AppCompatActivity
        implements BaseActivity<T> {

    /**
     * instance of presenter
     */
    private T presenter;
    private boolean isTablet;

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setPresenter();
        setUpPresenter(presenter);
        getPresenter().onAttach(this, getIntent().getExtras());
    }

    protected abstract int getLayoutId();

    private void setPresenter() {
        this.presenter = initializePresenter();
    }

    /**
     * This function can be overridden to setup presenter. It is being called in onCreate after
     * initializing presenter
     *
     * @param presenter presenter to setup
     */
    @CallSuper
    @Override
    public void setUpPresenter(T presenter) {
        isTablet = SystemHelper.isTablet(this);
        presenter.setIsTablet(isTablet);
        presenter.setOrientation(getResources().getConfiguration().orientation);
    }

    /**
     * @return provides new instance of presenter
     */
    public abstract T initializePresenter();

    @Override
    public T getPresenter() {
        return presenter;
    }

    @Override
    public boolean isAvailable() {
        return !isFinishing() && getPresenter() != null;
    }

    @Override
    public boolean isTablet() {
        return isTablet;
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        getPresenter().onDetach();
        presenter = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getPresenter().onRestoreInstanceState(savedInstanceState);
    }


}
