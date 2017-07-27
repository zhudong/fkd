package com.fuexpress.kr.ui.activity.login_register;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
/**
 * Created by kevin.xie on 2016/10/31.
 */

public class ThirdPlatformLoginActivity extends BaseActivity {
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    @BindView(R.id.profile_image)
    CircleImageView mProfileImage;
    @BindView(R.id.userNameText)
    TextView mUserNameText;
    @BindView(R.id.tv_login)
    Button mTvLogin;
    @BindView(R.id.bindBtn)
    Button mBindBtn;

    private final DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).displayer(new FadeInBitmapDisplayer(100)).cacheInMemory(true
    ).cacheOnDisk(true).build();
    final ImageLoader loader = ImageLoader.getInstance();

    @Override
    public View setInitView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_third_platform_login, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTitleTvLeft.setText(getString(R.string.cancel));
        mTitleTvLeft.setVisibility(View.VISIBLE);
        mTitleTvCenter.setText(getString(R.string.third_platform_login));
        if(!TextUtils.isEmpty(LoginByPhoneActivity.TITLE)){
            mTitleTvCenter.setText(LoginByPhoneActivity.TITLE+" "+getString(R.string.login));
        }
        if(!TextUtils.isEmpty(AccountManager.getInstance().thridUserIcon)){
            loader.displayImage(AccountManager.getInstance().thridUserIcon,mProfileImage, options);
            mUserNameText.setText(AccountManager.getInstance().thridNickname);
        }
    }

    @OnClick({R.id.title_tv_left, R.id.profile_image, R.id.tv_login, R.id.bindBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_tv_left:
                onBackPressed();
                break;
            case R.id.profile_image:

                break;
            case R.id.tv_login:
                RegisterByPhoneActivity.isNeedBind=true;
                toActivity(RegisterByPhoneActivity.class);
                break;
            case R.id.bindBtn:
                toActivity(BindThirdPlatformActivity.class);
                break;
        }
    }
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if(event.getType()==BusEvent.GET_THIRD_ACCOUNT_INFO){
            loader.displayImage(AccountManager.getInstance().thridUserIcon,mProfileImage, options);
            mUserNameText.setText(AccountManager.getInstance().thridNickname);
        }
    }
}
