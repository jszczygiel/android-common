package com.jszczygiel.foundation.views;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.jszczygiel.R;

public abstract class DialogFragmentActivityImpl<T extends BaseFragmentImpl> extends SimpleFragmentActivityImpl<T> {

    public int getLayoutId() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View root = findViewById(R.id.root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAfterTransition(DialogFragmentActivityImpl.this);
            }
        });
    }
}
