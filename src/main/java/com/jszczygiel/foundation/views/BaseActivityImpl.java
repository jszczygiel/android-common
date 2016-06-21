package com.jszczygiel.foundation.views;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;

import com.jszczygiel.foundation.helpers.SystemHelper;
import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;
import com.jszczygiel.foundation.views.interfaces.BaseActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivityImpl<T extends BasePresenter> extends AppCompatActivity implements BaseActivity<T> {

    /**
     * instance of presenter
     */
    private T presenter;
    private boolean isTablet;
    private Unbinder unbinder;

    /**
     * @return provides new instance of presenter
     */
    public abstract T initializePresenter();

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

    @Override
    public T getPresenter() {
        return presenter;
    }

    private void setPresenter() {
        this.presenter = initializePresenter();
    }

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder=ButterKnife.bind(this);
        setPresenter();
        setUpPresenter(presenter);
        getPresenter().onAttach(this);
    }

    protected abstract int getLayoutId();

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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

    @Override
    public boolean isAvailable() {
        return !isFinishing() && getPresenter() != null;
    }

    @Override
    public boolean isTablet() {
        return isTablet;
    }

}
