package com.fuexpress.kr.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.RedPointCountManager;
import com.fuexpress.kr.model.SalesOrderManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.AccountSettingActivity;
import com.fuexpress.kr.ui.activity.AddressManagerActivity;
import com.fuexpress.kr.ui.activity.ChooseCurrencyActivity;
import com.fuexpress.kr.ui.activity.ContractServiceActivity;
import com.fuexpress.kr.ui.activity.GiftCardActivity;
import com.fuexpress.kr.ui.activity.HelpCenterActivity;
import com.fuexpress.kr.ui.activity.IntegralManagementActivity;
import com.fuexpress.kr.ui.activity.MyBalanceActivity;
import com.fuexpress.kr.ui.activity.coupon.CouponsActivity;
import com.fuexpress.kr.ui.activity.my_order.MyOrderActivity;
import com.fuexpress.kr.ui.activity.notice.NoticeListActivity;
import com.fuexpress.kr.ui.activity.transport_address.TranSportAddressActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.DataCleanManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import fksproto.CsUser;

/**
 * Created by Longer on 2016/10/26.
 */
public class MeFragment extends BaseFragment<MainActivity> {

    @BindView(R.id.title_in_me)
    TitleBarView mTitleInMe;
    @BindView(R.id.profile_image)
    CircleImageView mProfileImage;
    @BindView(R.id.tv_login)
    TextView mTvLogin;
    @BindView(R.id.myMoneyLayout)
    RelativeLayout mMyMoneyLayout;
    @BindView(R.id.couponLayout)
    RelativeLayout mCouponLayout;
    @BindView(R.id.addressManagerLayout)
    RelativeLayout mAddressManagerLayout;
    @BindView(R.id.connectServiceLayout)
    RelativeLayout mConnectServiceLayout;
    @BindView(R.id.helpCenterLayout)
    RelativeLayout mHelpCenterLayout;
    @BindView(R.id.noticesRedPoint)
    ImageView mNoticesRedPoint;
    @BindView(R.id.noticeLayout)
    RelativeLayout mNoticeLayout;
    @BindView(R.id.cleanCacheLayout)
    RelativeLayout mCleanCacheLayout;
    @BindView(R.id.tv_versionName)
    TextView mTvVersionName;
    @BindView(R.id.my_balance_tv)
    TextView mMyBalanceTv;
    @BindView(R.id.my_balance_layout)
    RelativeLayout mMyBalanceLayout;
    @BindView(R.id.myItemsNumberText)
    TextView mMyItemsNumberText;
    @BindView(R.id.coupon_layout)
    RelativeLayout mCouponsLayout;
    @BindView(R.id.likesNumberText)
    TextView mLikesNumberText;
    @BindView(R.id.reward_score_layout)
    RelativeLayout mRewardScoreLayout;
    @BindView(R.id.frequently_used_currency_layout)
    RelativeLayout mFrequentlyUsedCurrencyLayout;
    @BindView(R.id.account_setting_layout)
    RelativeLayout mAccountSettingLayout;
    @BindView(R.id.tranSportManagerLayout)
    RelativeLayout mTranSoprtManagerLayout;
    @BindView(R.id.rl_my_order)
    RelativeLayout mRl_my_order;
    @BindView(R.id.orderRedPoint)
    ImageView mOrderRedPoint;

    private View mRootView;

    private final DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).displayer(new FadeInBitmapDisplayer(100)).cacheInMemory(true
    ).cacheOnDisk(true).build();
    final ImageLoader loader = ImageLoader.getInstance();

    @Override
    protected void initTitleBar() {
        TitleBarView titleBarView = (TitleBarView) mRootView.findViewById(R.id.title_in_me);
        ImageView iv_in_title_right = titleBarView.getIv_in_title_right();
        iv_in_title_right.setImageResource(R.mipmap.me_setting_white);
        iv_in_title_right.setVisibility(View.GONE);
        iv_in_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.toActivity(AccountSettingActivity.class);
            }
        });
    }

    @Override
    public View initView() {
        mRootView = View.inflate(mContext, R.layout.me_fragment_layout, null);
        return mRootView;
    }

    @Override
    public void initData() {
        //RedPointCountManager.getOrderCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAccountInfoRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        mTvVersionName.setText(mContext.getString(R.string.version) + "  " + getVersionName(mContext));

        mTvLogin.setText(AccountManager.getInstance().nikename);
        loader.displayImage(AccountManager.getInstance().avater, mProfileImage, options);
        //   CouponDataManager.getInstance().getCouponDataList(1);
        setRedPoint();
        return rootView;

    }

    private void testTicket() {
        AccountManager.getInstance().mTicket = "error";
    }

    @OnClick({R.id.title_in_me, R.id.profile_image, R.id.tv_login, R.id.myMoneyLayout, R.id.couponLayout,
            R.id.addressManagerLayout, R.id.connectServiceLayout, R.id.helpCenterLayout, R.id.noticeLayout,
            R.id.cleanCacheLayout, R.id.frequently_used_currency_layout, R.id.my_balance_layout, R.id.coupon_layout, R.id.reward_score_layout
            , R.id.account_setting_layout, R.id.card_car_layout, R.id.tranSportManagerLayout, R.id.rl_my_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_my_order:
                // TODO: 2017/7/6 跳转到我的订单页面
                Intent orderIntent = new Intent(mContext, MyOrderActivity.class);
                orderIntent.putExtra(MyOrderActivity.STATE, SalesOrderManager.STATUS_ALL);
                startActivity(orderIntent);
                break;
            case R.id.title_in_me:
                break;
            case R.id.profile_image:
                mContext.startDDMActivity(AccountSettingActivity.class, true);
                break;
            case R.id.tv_login:
                //  mContext.toActivity(KrBankInfoActivity.class);
                /**测试ticket失效方法**/
                // testTicket();
                break;
            case R.id.myMoneyLayout:
                Intent intent = new Intent(mContext, MyBalanceActivity.class);
                startActivity(intent);
                break;
            case R.id.couponLayout:
                mContext.toActivity(CouponsActivity.class);
                break;
            case R.id.addressManagerLayout:
                Intent intent1 = new Intent(mContext, AddressManagerActivity.class);
                intent1.putExtra(AddressManagerActivity.BACK_TITLE, getString(R.string.main_me_tab_string));
                startActivity(intent1);
                break;
            case R.id.connectServiceLayout:
                mContext.toActivity(ContractServiceActivity.class);

                //MeiqiaManager.getInstance(mContext).contactCustomerService();
//                // TODO: 2017/2/8 把在线客服换成网易七鱼
//                String title = getString(R.string.app_name);
//                // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
//                // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
//                ConsultSource source = new ConsultSource("123", "fkd", "custom information string");
//                // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
//                Unicorn.openServiceActivity(mContext, // 上下文
//                        title, // 聊天窗口的标题
//                        source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
//                );
                break;
            case R.id.helpCenterLayout:
                mContext.toActivity(HelpCenterActivity.class);
                break;
            case R.id.noticeLayout:
                mContext.toActivity(NoticeListActivity.class);
                break;
            case R.id.cleanCacheLayout:
                cleanCache();
                break;
            case R.id.frequently_used_currency_layout:
                mContext.toActivity(ChooseCurrencyActivity.class);
                break;
            case R.id.my_balance_layout:
                mContext.toActivity(MyBalanceActivity.class);
                break;
            case R.id.coupon_layout:
                mContext.toActivity(CouponsActivity.class);
                break;
            case R.id.reward_score_layout:
                mContext.toActivity(IntegralManagementActivity.class);
                break;
            case R.id.account_setting_layout:
                mContext.toActivity(AccountSettingActivity.class);
                break;
            case R.id.card_car_layout:
                mContext.toActivity(GiftCardActivity.class);
                break;
            case R.id.tranSportManagerLayout:
                mContext.toActivity(TranSportAddressActivity.class);
                break;
        }
    }

    private void cleanCache() {
        DataCleanManager.cleanApplicationData(mContext);
        mContext.showProgressDiaLog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.clean_success));
        /*Realm realm = DbConfig.getInstance(UIUtils.getContext()).getRealm();
        realm.beginTransaction();
        RealmResults<HelpSendParcelBean> all = realm.where(HelpSendParcelBean.class).findAll();
        all.deleteAllFromRealm();
        realm.commitTransaction();*/
        mContext.dissMissProgressDiaLog(1000);
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.HEAD_ICON_UPLOADE_COMPLETE_NOTE:

                Glide.with(this).load(AccountManager.getInstance().avater).error(R.mipmap.me_photo_white).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(mProfileImage);
                break;
            case BusEvent.GET_MY_RED_POINT_COUNT_SUCCESS:
                setRedPoint();
                break;
        }
    }

    public void setRedPoint() {
        if (RedPointCountManager.redPointNotifyCount != 0) {
            if (mNoticesRedPoint != null)
                mNoticesRedPoint.setVisibility(View.VISIBLE);
        } else {
            if (mNoticesRedPoint != null)
                mNoticesRedPoint.setVisibility(View.GONE);
        }
        if (RedPointCountManager.redPointOrderCount != 0)
            mOrderRedPoint.setVisibility(View.VISIBLE);
        else mOrderRedPoint.setVisibility(View.GONE);
    }

    public void GetAccountInfoRequest() {
        CsUser.GetAccountInfoRequest.Builder builder = CsUser.GetAccountInfoRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetAccountInfoResponse>() {

            @Override
            public void onSuccess(final CsUser.GetAccountInfoResponse response) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null) {
                            mMyBalanceTv.setText(UIUtils.getCurrency(mContext, response.getGiftcardaccount()));
                            mMyItemsNumberText.setText(response.getMyshippingcoupon() + "");
                            mLikesNumberText.setText(response.getCredits() + "");
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
