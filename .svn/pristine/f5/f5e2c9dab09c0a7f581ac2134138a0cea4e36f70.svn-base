package com.fuexpress.kr.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.ProgressCallback;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.HeaderModel;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.ChooseCityActivity;
import com.fuexpress.kr.ui.activity.PickUpActivity;
import com.fuexpress.kr.ui.activity.ReplenishActivity;
import com.fuexpress.kr.ui.activity.crowd.CrowdListActivity;
import com.fuexpress.kr.ui.activity.help_send.ToHelpSendActivity;
import com.fuexpress.kr.ui.activity.help_signed.HelpMeSignedActivity;
import com.fuexpress.kr.ui.activity.shopping_cart.PaymentSuccessActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import fksproto.CsAddress;

/**
 * Created by Longer on 2016/10/26.
 * home页面的Fragment
 */
public class HomeFragment extends BaseFragment<MainActivity> {

    @BindView(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    private View mRootView;
    private TextView mTv_in_title_left;

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        mRootView = View.inflate(mContext, R.layout.home_fragment_layout, null);
        TitleBarView titleBarView = (TitleBarView) mRootView.findViewById(R.id.title_in_home);
        mTv_in_title_left = titleBarView.getTv_in_title_left();
        mTv_in_title_left.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void initData() {
        HeaderModel instance = HeaderModel.getInstance();
        instance.cleanData();
        instance.initDta(mContext, convenientBanner);
        getUserCity();
    }

    private void getUserCity() {
        CsAddress.GetWarehouseRegionRequest.Builder builder = CsAddress.GetWarehouseRegionRequest.newBuilder();
        builder.setBaseuser(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        builder.setType(1);
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.GetWarehouseRegionResponse>() {
            @Override
            public void onSuccess(final CsAddress.GetWarehouseRegionResponse response) {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        String city = response.getRegionName();
                        if (!TextUtils.isEmpty(city)) {
                            mTv_in_title_left.setText(city);
                            SPUtils.putString(getActivity(), ChooseCityActivity.F_RECENT_CITY, city);
                            SPUtils.putString(getActivity(), ChooseCityActivity.F_RECENT_CITY_ID, response.getWarehouseRegionId() + "");
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
            }
        });
    }


    @OnClick({R.id.rl_in_hf_help_get, R.id.rl_in_hf_help_take, R.id.rl_in_hf_help_buy, R.id.rl_in_hf_help_send, R.id.tv_in_title_left, R.id.btn_crowd, R.id.btn_crowd_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_in_hf_help_get:
                mContext.startDDMActivity(PickUpActivity.class, true);
                break;
            case R.id.rl_in_hf_help_take:
                /*try {
                    testMethod();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                mContext.startDDMActivity(HelpMeSignedActivity.class, true);
                break;
            case R.id.rl_in_hf_help_buy:
                mContext.startDDMActivity(ReplenishActivity.class, true);
                break;
            case R.id.rl_in_hf_help_send:
                mContext.startDDMActivity(ToHelpSendActivity.class, true);
                break;
            case R.id.tv_in_title_left:
                // TODO: 2017/3/28 开启城市选择功能
                mContext.startDDMActivity(ChooseCityActivity.class, true);
                break;
            case R.id.btn_crowd:
                mContext.startDDMActivity(CrowdListActivity.class, true);
                break;
            case R.id.btn_crowd_detail:
                mContext.startDDMActivity(PaymentSuccessActivity.class, true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String string = SPUtils.getString(mContext, ChooseCityActivity.F_RECENT_CITY, "");
        if (TextUtils.isEmpty(string)) string = getString(R.string.string_seoul);
        mTv_in_title_left.setText(string);
    }

    public void testMethod() throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "yiss");
        config.put("api_key", "654276367494388");
        config.put("api_secret", "FtzCxMPuUZVxIFaVTLqzDHdNS7U");
        Cloudinary cloudinary = new Cloudinary(config);
        final Uploader uploader = cloudinary.uploader();
        final File file = new File("storage/emulated/0/Download/dced6968be7d36becd6e1d0ca1a825fd.jpg");
        ImageView iv_in_home_02 = (ImageView) mRootView.findViewById(R.id.iv_in_home_02);
        Glide.with(mContext).load(file).into(iv_in_home_02);
        String generate = cloudinary.url().generate("yiss_test_2017_080110.jpg");
        LogUtils.e(generate);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    uploader.upload(file, ObjectUtils.asMap("public_id", "yiss_test_2017_080110"), new ProgressCallback() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            LogUtils.e(String.valueOf(bytesUploaded));
                            LogUtils.e(String.valueOf(totalBytes));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
