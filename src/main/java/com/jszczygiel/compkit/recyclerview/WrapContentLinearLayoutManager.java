package com.jszczygiel.compkit.recyclerview;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapContentLinearLayoutManager extends LinearLayoutManager {

  public WrapContentLinearLayoutManager(Context context, boolean reverseLayout) {
    super(context, VERTICAL, reverseLayout);
    setItemPrefetchEnabled(false);
  }

  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    try {
      super.onLayoutChildren(recycler, state);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
