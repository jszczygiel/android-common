package com.jszczygiel.foundation.presenters.interfaces;

import android.os.Bundle;

public interface BaseListPresenter<T> extends BasePresenter<T>{

    void onLoad(Bundle arguments);
}
