package com.fuexpress.kr.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by yuan on 2016-7-6.
 */
public abstract class MbaseFragment<P extends BasePresenter, M extends BaseModel> extends BaseFragment<Activity> implements BaseView {
    protected View mRootView;
    protected P mPresenter;
    protected M mModel;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void finishView() {
        mContext.finish();
    }

    @Override
    public void showLoadingView() {
    }

    @Override
    public void closeLoadingView() {
    }
}
