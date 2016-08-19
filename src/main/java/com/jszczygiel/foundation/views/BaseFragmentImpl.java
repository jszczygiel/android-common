package com.jszczygiel.foundation.views;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jszczygiel.foundation.helpers.SystemHelper;
import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;
import com.jszczygiel.foundation.views.interfaces.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragmentImpl<T extends BasePresenter> extends Fragment implements BaseFragment<T> {

    /**
     * instance of presenter
     */
    private T presenter;
    private boolean isTablet;
    private Unbinder unbinder;
    private TransitionDrawable transition;

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPresenter();
        setUpPresenter(presenter);
        getPresenter().onAttach(this);
    }

    private void setPresenter() {
        if (presenter == null) {
            this.presenter = initializePresenter();
        }
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
        isTablet = SystemHelper.isTablet(getActivity());
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
        return !isDetached() && !isRemoving() && getPresenter() != null;
    }

    @Override
    public boolean isTablet() {
        return isTablet;
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void setResult(int resultCode, Intent data) {
        getActivity().setResult(resultCode, data);
    }

    @Override
    public void setResult(int resultCode) {
        getActivity().setResult(resultCode);
    }

    @Override
    public void showToast(int stringRes, String... formattedArgs) {
        Toast.makeText(getContext(), getString(stringRes, formattedArgs), Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            getPresenter().onRestoreInstanceState(savedInstanceState);
        } else {
            getPresenter().onLoad(getArguments());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        if (!isStaticView()) {
            clear();
        }

    }

    public boolean isStaticView() {
        return false;
    }

    public void clear() {
        getPresenter().onDetach();
        presenter = null;
    }

    protected abstract int getLayoutId();

    public void onBackPressed() {
        SystemHelper.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
        getActivity().onBackPressed();
    }

    public void setIntent(Intent intent) {
        getActivity().setIntent(intent);
    }

    @ColorInt
    public int getColor(@ColorRes int colorId) {
        return getContext().getResources().getColor(colorId);
    }

    public void finishWithResult(int result, Intent intent) {
        getActivity().setResult(result, intent);
        finish();
    }

    public String getTitle() {
        return (String) getActivity().getTitle();
    }
}