package com.jszczygiel.compkit.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

public abstract class BaseBindedViewHolder<T, K extends ViewDataBinding> extends BaseViewHolder<T> {
    private final K binding;

    public BaseBindedViewHolder(K binding, Context context) {
        super(binding.getRoot(), context);
        this.binding=binding;
    }

    public K getBinding() {
        return binding;
    }
}
