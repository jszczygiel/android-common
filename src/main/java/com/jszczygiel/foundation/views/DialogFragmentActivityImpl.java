package com.jszczygiel.foundation.views;

import com.jszczygiel.R;

public abstract class DialogFragmentActivityImpl<T extends BaseFragmentImpl> extends SimpleFragmentActivityImpl<T> {

    public int getLayoutId() {
        return R.layout.activity_dialog;
    }

}
