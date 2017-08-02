package com.fuexpress.kr.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

/**
 * Created by Longer on 2016/10/26.
 */
public abstract class BaseFragment<ACTIVITY_TYPE> extends Fragment implements View.OnClickListener {
    private final static String TAG = "BaseFragment";
    protected ACTIVITY_TYPE mContext;
    //public TitleBarView mTitleBarView;
    private SweetAlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();// 可以接受别人传递过来的参数
        mContext = (ACTIVITY_TYPE) getActivity();
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        ButterKnife.bind(this, view);
        //mTitleBarView = new TitleBarView(view);
        initTitleBar();
        return view;
    }

    protected abstract void initTitleBar();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initListener();
        initData();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化的时候,可以接收别人传递进来的参数
     */
    public void init() {

    }

    /**
     * 初始化Fragment应有的视图 ,为抽象
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化Fragment应有的数据,必须实现
     */
    public abstract void initData();

    /**
     * 初始化监听器
     */
    public void initListener() {

    }

    //接收EventBus
    public void onEventMainThread(BusEvent event) {
        //    Log.d(TAG, "onEventMainThread收到了消息：" + event.getType());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void closeLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (this.isHidden()) return;
        BaseActivity activity = (BaseActivity) getActivity();
        //activity.closeLoading();
    }

    protected void showLoading() {
        if (dialog == null)
            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#ffffff"));
        dialog.setCancelable(false);
        dialog.show();
        if (this.isHidden()) return;
        BaseActivity activity = (BaseActivity) getActivity();
        //activity.showLoading();
    }

    protected void showLoading(int time) {
       /* dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setCancelable(false);
        dialog.show();*/
        if (this.isHidden()) return;
        BaseActivity activity = (BaseActivity) getActivity();
        //activity.showLoading(time);
    }

    @Override
    public void onResume() {
        super.onResume();
        //LogUtils.d(this.getClass().getSimpleName() + "onResume");
    }

    @Override
    public void onPause() {
        //LogUtils.d(this.getClass().getSimpleName() + "onPause");
        super.onPause();
    }


}
