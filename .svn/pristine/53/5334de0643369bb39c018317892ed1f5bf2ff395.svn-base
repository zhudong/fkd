package com.fuexpress.kr.ui.activity.bind_module;


import android.content.DialogInterface;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsLogin;

public class BindOperatingActivity extends BaseActivity {

    private static final int KEY_UNBIND_TYPE = 1;
    private static final int KEY_BIND_PHONE_TYPE = 2;
    private View rootView;
    private TextView tv_phone;
    private TextView tv_wechat;
    private TextView tv_qq;
    private TextView tv_facebook;
    private ArrayMap<Integer, Boolean> bindInfoMap;
    private boolean isBindphone;
    private String phoneNumer;
    private boolean isCanUnBind = false;
    private int paltNum = -1;
    private String paltName = "";


    RequestNetListenerWithMsg<CsLogin.AccountResponse> requestNetListenerWithMsg = new RequestNetListenerWithMsg<CsLogin.AccountResponse>() {
        @Override
        public void onSuccess(CsLogin.AccountResponse response) {
            if (paltNum != -1) {
                final Boolean aBoolean = bindInfoMap.get(paltNum);
                bindInfoMap.put(paltNum, !aBoolean);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        switchData(paltNum, !aBoolean);
                        if (!aBoolean)
                            showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.bind_successful));
                        else {
                            initData();
                            showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.unbind_successful));
                        }

                        dissMissProgressDiaLog(1000);
                    }
                });

            }
        }

        @Override
        public void onFailure(final int ret, final String msg) {
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    showProgressDiaLog(SweetAlertDialog.WARNING_TYPE, msg);
                    dissMissProgressDiaLog(1000);
                    //mViewUtils.toast("fail:" + ret + msg);
                }
            });
        }
    };

    ThirdLoginListener thirdLoginListener = new ThirdLoginListener() {
        @Override
        public void onRequestSuccess(String openID, String token) {
            if (paltNum != -1)
                ThirdBindManager.getInstance().bindThirdPlatformInfo(paltNum, openID, token, requestNetListenerWithMsg);
        }

        @Override
        public void onRequestFail(final String errMsg) {
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    mViewUtils.toast("error:" + errMsg);
                }
            });

        }
    };
    private ImageView iv_phone_arrow;


    @Override
    public View setInitView() {
        rootView = View.inflate(this, R.layout.activity_bind_operating, null);
        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.title_in_bind);
        titleBarView.getIv_in_title_back().setOnClickListener(this);
        TextView back_tv = titleBarView.getmTv_in_title_back_tv();
        back_tv.setText(getString(R.string.account_setting));
        back_tv.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        LinearLayout ll_bind_phone = (LinearLayout) rootView.findViewById(R.id.ll_bind_phone);
        tv_phone = (TextView) ll_bind_phone.findViewById(R.id.tv_phone);
        iv_phone_arrow = (ImageView) ll_bind_phone.findViewById(R.id.iv_phone_arrow);
        ll_bind_phone.setOnClickListener(this);
        LinearLayout ll_bind_wechat = (LinearLayout) rootView.findViewById(R.id.ll_bind_wechat);
        tv_wechat = (TextView) ll_bind_wechat.findViewById(R.id.tv_wechat);
        ll_bind_wechat.setOnClickListener(this);
        LinearLayout ll_bind_qq = (LinearLayout) rootView.findViewById(R.id.ll_bind_qq);
        tv_qq = (TextView) ll_bind_qq.findViewById(R.id.tv_qq);
        ll_bind_qq.setOnClickListener(this);
        LinearLayout ll_bind_facebook = (LinearLayout) rootView.findViewById(R.id.ll_bind_facebook);
        tv_facebook = (TextView) ll_bind_facebook.findViewById(R.id.tv_facebook);
        ll_bind_facebook.setOnClickListener(this);

        //initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        bindInfoMap = new ArrayMap<>();
        for (int i = 1; i < 5; i++) {
            bindInfoMap.put(i, false);
        }
        ThirdBindManager.getInstance().getBindInfoRequest(new RequestNetListenerWithMsg<CsLogin.GetBindInfoResponse>() {

            @Override
            public void onSuccess(CsLogin.GetBindInfoResponse response) {
                LogUtils.e("成功：" + response.toString());
                isBindphone = response.getIsBindphone();
                phoneNumer = response.getPhoneNumer();
                final List<Integer> platformList = response.getPlatformList();
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        iv_phone_arrow.setImageResource(isBindphone ? R.color.white : R.mipmap.fill_5);
                        if (!isBindphone) tv_phone.setText(getString(R.string.is_not_bind));
                        setViewWithDataList(isBindphone, phoneNumer, platformList);
                    }
                });
            }

            @Override
            public void onFailure(final int ret, final String msg) {
                LogUtils.e("fail:" + ret + msg);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast("fail:" + ret + msg);
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_bind_phone:
                if (!isBindphone) showTheDialog(KEY_BIND_PHONE_TYPE, 101010);
                //startDDMActivity(BindPhoneActivity.class, true);
                break;
            case R.id.ll_bind_wechat:
                paltName = "微信";
                bindMethod(1);
                break;
            case R.id.ll_bind_qq:
                paltName = "QQ";
                bindMethod(2);
                break;
            case R.id.ll_bind_facebook:
                paltName = "Facebook";
                bindMethod(4);
                break;
            case R.id.iv_in_title_back:
                /*finish();
                break;*/
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }

    public void setViewWithDataList(boolean isBindPhone, String phoneNumber, List<Integer> platformList) {
        if (isBindPhone) tv_phone.setText(phoneNumber);

        Set<Integer> integers = bindInfoMap.keySet();
        for (int key : integers) {
            if (platformList.contains(key)) {
                switchData(key, true);
                bindInfoMap.put(key, true);
            }
        }
        isCanUnBind = platformList.size() >= 2 || isBindPhone;
    }

    public void switchData(int paltNum, boolean isBind) {
        String bindString = isBind ? getString(R.string.is_bind) : getString(R.string.is_not_bind);
        TextView textView = null;
        int color = isBind ? getResources().getColor(R.color.gray_666) : getResources().getColor(R.color.gray_999);
        switch (paltNum) {
            case 1:
                textView = tv_wechat;
                break;
            case 2:
                textView = tv_qq;
                break;
            case 3:
                LogUtils.e("目前福快递不支持新浪登录");
                break;
            case 4:
                textView = tv_facebook;
                break;
        }
        if (null != textView) {
            textView.setText(bindString);
            textView.setTextColor(color);
        }
        //iv_phone_arrow.setVisibility(isBindphone ? View.GONE : View.VISIBLE);
        int i = -1;
        if (isBindphone)
            i = R.color.white;
        else
            i = R.mipmap.fill_5;

    }


    public void bindMethod(int paltNum) {
        this.paltNum = paltNum;
        if (paltNum != -1)
            if (bindInfoMap.get(paltNum)) {
                if (isCanUnBind)
                    showTheDialog(KEY_UNBIND_TYPE, paltNum);
                else
                    showTheDialog(KEY_BIND_PHONE_TYPE, 101010);
                //ThirdBindManager.getInstance().removeBindInfo(paltNum, phoneNumer, requestNetListenerWithMsg);
            } else {
                // TODO: 2017/10/30 绑定操作
                switch (paltNum) {
                    case 2:
                        ThirdLoginManager.getInstance().loginByQQ(this, thirdLoginListener);
                        break;
                    case 1:
                        // TODO: 2017/10/31 微信绑定操作
                        ThirdLoginManager.getInstance().loginByWX(this);
                        break;
                    case 4:
                        // TODO: 2017/10/31 Facebook绑定操作
                        ThirdLoginManager.getInstance().loginByFaceBook(this, thirdLoginListener);
                        break;
                }
            }
    }

    private void unBindMethod(int paltNum) {
        ThirdBindManager.getInstance().removeBindInfo(paltNum, phoneNumer, requestNetListenerWithMsg);
    }

    private CustomDialog.Builder dialog;

    public void showTheDialog(final int type, final int paltNum) {
        String dialogConfig = KEY_UNBIND_TYPE == type ? getString(R.string.string_unbind) : getString(R.string.String_bind_phone);
        String dialogcancle = getString(R.string.cancel);
        dialog = new CustomDialog.Builder(this);
        //dialog.setTitle("绑定手机号");
        dialog.setMessage(KEY_UNBIND_TYPE == type ? getString(R.string.string_unbind_02, paltName) : getString(R.string.unbind_messg));
        dialog.setPositiveButton(dialogConfig, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (KEY_UNBIND_TYPE == type)
                    unBindMethod(paltNum);
                else startDDMActivity(BindPhoneActivity.class, true);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(dialogcancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }


    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.BIND_WX_REQUEST:
                ThirdBindManager.getInstance().bindThirdPlatformInfo(1, event.getStrParam(), event.getStrParam02(), requestNetListenerWithMsg);
                break;
        }
    }
}
