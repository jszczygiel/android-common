package com.jszczygiel.compkit.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
  public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
  int firstVisibleItem, visibleItemCount, totalItemCount;
  private int visibleThreshold = 5; // The minimum amount of items to have below your current
  // scroll position before loading more.
  private LinearLayoutManager mLinearLayoutManager;
  private int firstCompletlyVisible;

  public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
    this.mLinearLayoutManager = linearLayoutManager;
  }

  @Override
  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    visibleItemCount = recyclerView.getChildCount();
    totalItemCount = mLinearLayoutManager.getItemCount();
    firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
    firstCompletlyVisible = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

    if (!isLoading()
        && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
      onLoadMore();
    }

    if (firstVisibleItem < 3) {
      onScrolledToBeginning();
    }

    onItemsVisibilityChanged(firstCompletlyVisible, firstCompletlyVisible + visibleItemCount - 1);
  }

  public abstract boolean isLoading();

  public abstract void onLoadMore();

  public abstract void onScrolledToBeginning();

  public abstract void onItemsVisibilityChanged(int firstVisibleItem, int lastVisibleItem);
}
