package com.jszczygiel.foundation.views.interfaces;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import com.jszczygiel.compkit.adapter.BaseRecyclerAdapter;
import com.jszczygiel.compkit.adapter.BaseViewModel;
import com.jszczygiel.foundation.presenters.interfaces.BaseListPresenter;
import java.util.List;

public interface BaseListFragment<T extends BaseListPresenter> extends BaseFragment<T> {
  void showList();

  void showEmpty();

  void onLoadMore();

  void onScrolledToBeginning();

  boolean isLoading();

  void addOrUpdate(BaseViewModel model);

  int getItemCount();

  void remove(BaseViewModel model);

  void removeById(String id);

  void scrollToTop();

  void scrollToPosition(int position);

  int getCurrentPosition();

  Bundle getParcelableList();

  void setParcelableList(Bundle bundle);

  void triggerItemsVisibilityUpdate();

  void setSelectedItem(String selectedId);

  String getSelectedId();

  BaseRecyclerAdapter newAdapterInstance();

  RecyclerView.ItemAnimator createItemAnimatorInstance();

  RecyclerView.ItemDecoration[] createItemDecoratorsInstances();

  boolean isReverse();

  List<BaseViewModel> getViewModels();
}
