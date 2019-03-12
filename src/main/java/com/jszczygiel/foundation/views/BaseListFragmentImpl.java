package com.jszczygiel.foundation.views;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jszczygiel.R;
import com.jszczygiel.compkit.adapter.BaseRecyclerAdapter;
import com.jszczygiel.compkit.adapter.BaseViewModel;
import com.jszczygiel.compkit.recyclerview.EndlessRecyclerOnScrollListener;
import com.jszczygiel.compkit.recyclerview.WrapContentLinearLayoutManager;
import com.jszczygiel.foundation.presenters.interfaces.BaseListPresenter;
import com.jszczygiel.foundation.views.interfaces.BaseListFragment;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class BaseListFragmentImpl<T extends BaseListPresenter> extends BaseFragmentImpl<T>
    implements BaseListFragment<T> {

  protected BaseRecyclerAdapter adapter;
  RecyclerView recyclerView;
  ViewGroup emptyView;
  private int firstVisibleItem, lastVisibleItem;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    recyclerView = (RecyclerView) view.findViewById(R.id.list);
    emptyView = (ViewGroup) view.findViewById(R.id.empty);

    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    WrapContentLinearLayoutManager linearLayoutManager =
        new WrapContentLinearLayoutManager(getContext(), isReverse());
    recyclerView.setLayoutManager(linearLayoutManager);
    adapter = newAdapterInstance();
    recyclerView.setAdapter(adapter);
    ItemTouchHelper touchHelper = createTouchHelperInstance();
    if (touchHelper != null) {
      touchHelper.attachToRecyclerView(recyclerView);
    }
    recyclerView.setItemAnimator(createItemAnimatorInstance());
    recyclerView.addOnScrollListener(
        new EndlessRecyclerOnScrollListener(linearLayoutManager) {
          @Override
          public boolean isLoading() {
            return BaseListFragmentImpl.this.isLoading();
          }

          @Override
          public void onLoadMore() {
            BaseListFragmentImpl.this.onLoadMore();
          }

          @Override
          public void onScrolledToBeginning() {
            BaseListFragmentImpl.this.onScrolledToBeginning();
          }

          @Override
          public void onItemsVisibilityChanged(int firstVisibleItem, int lastVisibleItem) {
            BaseListFragmentImpl.this.onItemsVisibilityChanged(firstVisibleItem, lastVisibleItem);
          }
        });

    for (RecyclerView.ItemDecoration decorator : createItemDecoratorsInstances()) {
      recyclerView.addItemDecoration(decorator);
    }
    super.onViewCreated(view, savedInstanceState);
  }

  public ItemTouchHelper createTouchHelperInstance() {
    return null;
  }

  @CallSuper
  protected void onItemsVisibilityChanged(int firstVisibleItem, int lastVisibleItem) {
    this.firstVisibleItem = firstVisibleItem;
    this.lastVisibleItem = lastVisibleItem;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    adapter.onDetachedFromRecyclerView(null);
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_base_list;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    getPresenter().onSaveInstanceState(outState);
  }

  @Override
  public void showList() {
    if (recyclerView != null) {
      recyclerView.setVisibility(View.VISIBLE);
    }
    if (emptyView != null) {
      emptyView.setVisibility(View.GONE);
    }
  }

  @Override
  public void showEmpty() {
    if (recyclerView != null) {
      recyclerView.setVisibility(View.GONE);
    }
    if (emptyView != null) {
      emptyView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onLoadMore() {}

  @Override
  public void onScrolledToBeginning() {}

  @Override
  public boolean isLoading() {
    return false;
  }

  @Override
  public void addOrUpdate(BaseViewModel model) {
    adapter.addOrUpdate(model);
    Observable.just(recyclerView.getChildCount() == 0)
        .filter(
            new Func1<Boolean, Boolean>() {
              @Override
              public Boolean call(Boolean aBoolean) {
                return aBoolean;
              }
            })
        .delay(500, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            new Action1<Boolean>() {
              @Override
              public void call(Boolean next) {
                if (recyclerView.getChildCount() == 0) {
                  recyclerView.requestLayout();
                }
              }
            });
  }

  @Override
  public int getItemCount() {
    return adapter.getItemCount();
  }

  @Override
  public void remove(BaseViewModel model) {
    adapter.remove(model);
  }

  @Override
  public void removeById(String id) {
    adapter.removeById(id);
  }

  @Override
  public void scrollToTop() {
    if (recyclerView != null) {
      recyclerView.smoothScrollToPosition(0);
    }
  }

  @Override
  public void scrollToPosition(int position) {
    recyclerView.smoothScrollToPosition(position);
  }

  @Override
  public int getCurrentPosition() {
    if (recyclerView == null) {
      return RecyclerView.NO_POSITION;
    }
    return ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
  }

  @Override
  public Bundle getParcelableList() {
    return adapter.getBundle();
  }

  @Override
  public void setParcelableList(Bundle bundle) {
    adapter.setBundle(bundle);
  }

  @Override
  public void triggerItemsVisibilityUpdate() {
    onItemsVisibilityChanged(firstVisibleItem, lastVisibleItem);
  }

  @Override
  public void setSelectedItem(String selectedId) {
    adapter.setSelectedId(selectedId);
  }

  @Override
  public String getSelectedId() {
    return adapter.getSelectedId();
  }

  @Override
  public RecyclerView.ItemAnimator createItemAnimatorInstance() {
    return new DefaultItemAnimator();
  }

  @Override
  public RecyclerView.ItemDecoration[] createItemDecoratorsInstances() {
    return new RecyclerView.ItemDecoration[0];
  }

  @Override
  public boolean isReverse() {
    return false;
  }

  @Override
  public List<BaseViewModel> getViewModels() {
    return adapter.getViewModels();
  }
}
