package com.jszczygiel.foundation.views;

import android.support.v4.app.Fragment;

import com.jszczygiel.R;

public abstract class DialogFragmentActivityImpl<T extends Fragment> extends SimpleFragmentActivityImpl<T> {

    public int getLayoutId() {
        return R.layout.activity_dialog;
    }

}
