package com.jszczygiel.foundation.views;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jszczygiel.foundation.presenters.interfaces.BaseListPresenter;
import com.jszczygiel.foundation.views.interfaces.BaseListFragment;

public abstract class BaseBindedListFragmentImpl<
        T extends BaseListPresenter, K extends ViewDataBinding>
    extends BaseListFragmentImpl<T> implements BaseListFragment<T> {

  private K binding;

  public K getBinding() {
    return binding;
  }

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
    recyclerView = getRecyclerView();
    emptyView = getEmptyView();
    return binding.getRoot();
  }

  protected abstract RecyclerView getRecyclerView();

  protected abstract ViewGroup getEmptyView();
}
