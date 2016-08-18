package com.jszczygiel.foundation.views;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jszczygiel.R;
import com.jszczygiel.compkit.adapter.BaseRecyclerAdapter;
import com.jszczygiel.compkit.adapter.BaseViewModel;
import com.jszczygiel.compkit.recyclerview.EndlessRecyclerOnScrollListener;
import com.jszczygiel.compkit.recyclerview.WrapContentLinearLayoutManager;
import com.jszczygiel.foundation.presenters.interfaces.BaseListPresenter;
import com.jszczygiel.foundation.views.interfaces.BaseListFragment;

public abstract class BaseListFragmentImpl<T extends BaseListPresenter> extends BaseFragmentImpl<T> implements BaseListFragment<T> {

    protected BaseRecyclerAdapter adapter;
    RecyclerView recyclerView;
    FrameLayout emptyView;
    private int firstVisibleItem, lastVisibleItem;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Override
    public RecyclerView.ItemAnimator createItemAnimatorInstance() {
        return new DefaultItemAnimator();
    }

    @Override
    public RecyclerView.ItemDecoration[] createItemDecoratorsInstances() {
        return new RecyclerView.ItemDecoration[0];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        emptyView = (FrameLayout) view.findViewById(R.id.empty);

        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(getContext(), isReverse());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = newAdapterInstance();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper touchHelper = createTouchHelperInstance();
        if (touchHelper != null) {
            touchHelper.attachToRecyclerView(recyclerView);
        }
        recyclerView.setItemAnimator(createItemAnimatorInstance());
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onScrolledToBeginning() {
                BaseListFragmentImpl.this.onScrolledToBeginning();
            }

            @Override
            public void onLoadMore() {
                BaseListFragmentImpl.this.onLoadMore();
            }

            @Override
            public void onItemsVisibilityChanged(int firstVisibleItem, int lastVisibleItem) {
                BaseListFragmentImpl.this.onItemsVisibilityChanged(firstVisibleItem, lastVisibleItem);
            }

            @Override
            public boolean isLoading() {
                return BaseListFragmentImpl.this.isLoading();
            }
        });

        for (RecyclerView.ItemDecoration decorator : createItemDecoratorsInstances()) {
            recyclerView.addItemDecoration(decorator);
        }
        return view;
    }


    public ItemTouchHelper createTouchHelperInstance() {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.onDetachedFromRecyclerView(null);
    }

    @Override
    public boolean isReverse() {
        return false;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onScrolledToBeginning() {

    }

    @Override
    public boolean isLoading() {
        return false;
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
    public void addOrUpdate(BaseViewModel model) {
        adapter.addOrUpdate(model);
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

    @CallSuper
    protected void onItemsVisibilityChanged(int firstVisibleItem, int lastVisibleItem) {
        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = lastVisibleItem;
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

}
