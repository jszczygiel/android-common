package com.jszczygiel.foundation.views;

import android.app.Service;

import com.jszczygiel.foundation.helpers.SystemHelper;
import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;
import com.jszczygiel.foundation.views.interfaces.BaseService;

public abstract class BaseServiceImpl<T extends BasePresenter> extends Service implements BaseService<T> {

    /**
     * instance of presenter
     */
    private T presenter;
    private boolean isTablet;

    /**
     * @return provides new instance of presenter
     */
    public abstract T initializePresenter();


    @Override
    public void setUpPresenter(T presenter) {
        isTablet = SystemHelper.isTablet(this);
        presenter.setIsTablet(isTablet);
        presenter.setOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setPresenter();
        setUpPresenter(presenter);
        presenter.onAttach(this);
    }

    @Override
    public void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @Override
    public T getPresenter() {
        return presenter;
    }

    private void setPresenter() {
        this.presenter = initializePresenter();
    }

    @Override
    public boolean isAvailable() {
        return presenter != null;
    }

    @Override
    public boolean isTablet() {
        return isTablet;
    }
}
