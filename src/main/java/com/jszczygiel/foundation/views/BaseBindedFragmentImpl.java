package com.jszczygiel.foundation.views;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jszczygiel.foundation.presenters.interfaces.BasePresenter;
import com.jszczygiel.foundation.views.interfaces.BaseFragment;

public abstract class BaseBindedFragmentImpl<T extends BasePresenter, K extends ViewDataBinding>
    extends BaseFragmentImpl<T> implements BaseFragment<T> {

  private K binding;

  public K getBinding() {
    return binding;
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
    return binding.getRoot();
  }
}
