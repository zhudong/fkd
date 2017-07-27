package com.fuexpress.kr.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by Longer on 2017/6/26.
 */
public abstract class BaseHolder<T> {
    protected Activity mContext;
    protected View mRootView;

    public BaseHolder(Activity context) {
        mContext = context;
        mRootView = initView();
//        initEvent();
    }

    public  void initEvent(){};
    public  void initEvent(T t){};

    public View getRootView() {
        return mRootView;
    }

    public void initData(T data){};
    public void initData(){};

    public abstract View initView();

}
