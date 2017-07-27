package com.fuexpress.kr.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by baixiaokang on 16/4/22.
 */
public abstract class BasePresenter<E, T> {
    public BaseActivity context;
    public E mModel;
    public T mView;

    public void setVM(T v, E m) {
        if (v instanceof Activity) {
            context = (BaseActivity) v;
        }
        if (v instanceof Fragment) {
            context = (BaseActivity)((Fragment) v).getActivity();
        }

        this.mView = v;
        this.mModel = m;
        this.onStart();
    }

    public abstract void onStart();

    public void onDestroy() {
        mView = null;
    }
}
