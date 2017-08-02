package com.fuexpress.kr;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.BottomStateBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.conf.PermissionCode;
import com.fuexpress.kr.model.ConfigManager;
import com.fuexpress.kr.model.MaterialsManager;
import com.fuexpress.kr.model.RedPointCountManager;
import com.fuexpress.kr.model.UserManager;
import com.fuexpress.kr.model.download.UpdateManager;
import com.fuexpress.kr.ui.activity.AccountSettingActivity;
import com.fuexpress.kr.ui.activity.help_send.ToHelpSendActivity;
import com.fuexpress.kr.ui.activity.produ_source.ProduSrcMainFragment;
import com.fuexpress.kr.ui.fragment.HomeFragment;
import com.fuexpress.kr.ui.fragment.MeFragment;
import com.fuexpress.kr.ui.fragment.MyNeedFragment;
import com.fuexpress.kr.ui.fragment.MyPackageFragment;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.NoteRadioGroup;
import com.fuexpress.kr.utils.SPUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragmentLists;
    private int mFrragmentIndex = 0;
    static Handler handler = new Handler();
    public NoteRadioGroup mRbgHomeFragment;
    private ImageView mIv_demand_red_point;
    private ImageView mIv_package_red_point;
    FrameLayout mFlResource;
    private FrameLayout flGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View setInitView() {
        View mainView = View.inflate(this, R.layout.activity_main, null);
        mRbgHomeFragment = (NoteRadioGroup) mainView.findViewById(R.id.rbg_home_fragment);
        mIv_demand_red_point = (ImageView) mainView.findViewById(R.id.iv_demand_red_point);
        mIv_package_red_point = (ImageView) mainView.findViewById(R.id.iv_package_red_point);
        mFlResource = (FrameLayout) mainView.findViewById(R.id.fl_resource);
        flGroup = (FrameLayout) mainView.findViewById(R.id.fl_group);
        //KLog.i("YDK ,run");
        //    AccountManager.getInstance().login(this);
        //AccountManager.getInstance().getRedPoint();
        //        AccountManager.getInstance().loginByEmail("feicui@qq.com", "111111", null);
        boolean isShowTips = (boolean) SPUtils.get(this, Constants.KEY_IS_SHOW_HM_MASK, true);
        //isShowTips = true;
        if (isShowTips) {
            final FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
            final View tipsView = View.inflate(this, R.layout.home_fragment_mask, null);
            decorView.addView(tipsView);
            tipsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decorView.removeView(tipsView);
                    SPUtils.put(MainActivity.this, Constants.KEY_IS_SHOW_HM_MASK, false);
                }
            });
        }
        return mainView;
    }

    @Override
    public void initView() {
        initData();
        initListener();
        //默认主页第一个rb是被选中:
        mRbgHomeFragment.check(R.id.rb_home);
        MPermissions.requestPermissions(this, PermissionCode.readSDcard, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean booleanExtra = getIntent().getBooleanExtra(ConfigManager.KEY_JUMP_SETTING, false);
        if (booleanExtra) {
            mRbgHomeFragment.check(R.id.rb_me);
            Intent intent = new Intent(this, AccountSettingActivity.class);
            intent.putExtra(AccountSettingActivity.KEY_JUMP_PREFERENCE, true);
            startActivity(intent);
        }
    }

    private void initListener() {

        mRbgHomeFragment.setOnCheckedChangeListener(new NoteRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(NoteRadioGroup group, int checkedId) {
                // 需要根据回调的checkedId,进行ViewPager的切换
                switch (checkedId) {
                    case R.id.rb_home:// 首页
                        mFrragmentIndex = 0;
                        break;
                    case R.id.rb_need:// 商品管理
                        mFrragmentIndex = 1;
                        break;
                    case R.id.rb_package:// 添加商品
                        mFrragmentIndex = 2;
                        break;
                    case R.id.rb_me:// 我的订单
                        mFrragmentIndex = 3;
                        break;
                    case R.id.rb_source:
                        mFrragmentIndex = 4;
                    case R.id.rb_group:
                        // TODO: 01/08/2017 拼单

                        break;
                    default:
                        break;
                }
                switchFrag(mFrragmentIndex);
            }
        });

    }

    boolean isInSwithch;

    public synchronized void switchFrag(final int index) {
        UserManager.getInstance().getAllRedPointNum();
        mFrragmentIndex = index;
        if (isInSwithch)
            return;
        isInSwithch = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //LogUtils.d("switch", "switch fragmet");
                mFragmentManager = getSupportFragmentManager();
                Fragment fragmentByTag = mFragmentManager.findFragmentByTag(mFrragmentIndex + "");
                if (fragmentByTag == null) {
                    fragmentByTag = mFragmentLists.get(mFrragmentIndex);
                }/* else {
                每次切换到第一页的时候 判断一下状态
                    if (index == 0 || index == 4) {
                        OrderStateManager.getInstance().init();
                    }
                }*/

                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                if (!fragmentByTag.isAdded()) {    // 先判断是否被add过
                    transaction.add(R.id.container, fragmentByTag, mFrragmentIndex + "");
                    for (int i = 0; i < mFragmentLists.size(); i++) {
                        if (i == mFrragmentIndex) {
                            transaction.show(mFragmentLists.get(i));
                        } else {
                            transaction.hide(mFragmentLists.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < mFragmentLists.size(); i++) {
                        if (i == mFrragmentIndex) {
                            transaction.show(mFragmentLists.get(i));
                        } else {
                            transaction.hide(mFragmentLists.get(i));
                        }
                    }
                }
                transaction.commitAllowingStateLoss();
                isInSwithch = false;
            }
        }, 0);

    }


    private void initData() {
        mFragmentLists = new ArrayList<>();
        mFragmentLists.add(new HomeFragment());
        mFragmentLists.add(new MyNeedFragment());
        mFragmentLists.add(new MyPackageFragment());
        mFragmentLists.add(new MeFragment());
        mFragmentLists.add(new ProduSrcMainFragment());
        RedPointCountManager.getOrderCount();
        //HelpSignedLocalDataSource.getInstance().initLoaclData();
        MaterialsManager.getInstance().getMaterials();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    int mBackPressedCount;

    @Override
    public void onBackPressed() {
        Log.d("MainActivity", "------------mFrragmentIndex: " + mFrragmentIndex);
        //closeLoading();

        if (mBackPressedCount >= 1) {
            super.onBackPressed();
        } else {
            CustomToast.makeText(this, getString(R.string.String_back_toast), Toast.LENGTH_SHORT).show();
            mBackPressedCount++;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackPressedCount = 0;
            }
        }, 2000);
    }

    public int getFrragmentIndex() {
        return mFrragmentIndex;
    }

    @Override
    public void onResume() {
        super.onResume();
        RedPointCountManager.getOrderCount();
        UserManager.getInstance().getAllRedPointNum();
    }

    //  @Subscribe
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.GO_HOME_PAGE) {
            mFrragmentIndex = 0;
            switchFrag(mFrragmentIndex);
            RadioButton child = (RadioButton) mRbgHomeFragment.getChildAt(mFrragmentIndex);
            child.setChecked(true);
        }
        if (event.getType() == BusEvent.GO_HOME_CODE) {
            mFrragmentIndex = 0;
            switchFrag(mFrragmentIndex);
            RadioButton child = (RadioButton) mRbgHomeFragment.getChildAt(mFrragmentIndex);
            child.setChecked(true);
        }
        if (event.getType() == BusEvent.GO_SUBMIT_PARCEL) {
            mFrragmentIndex = 2;
            switchFrag(mFrragmentIndex);
            RadioButton child = (RadioButton) mRbgHomeFragment.findViewById(R.id.rb_package);
            child.setChecked(true);
        }

        if (event.getType() == BusEvent.GO_PRODUSRC_PAGE) {
            mFrragmentIndex = 4;
            switchFrag(mFrragmentIndex);
            RadioButton child = (RadioButton) mRbgHomeFragment.findViewById(R.id.rb_source);
            child.setChecked(true);
        }

        if (event.getType() == BusEvent.PARCEL_APPEND) {
            startDDMActivity(ToHelpSendActivity.class, true);
        }
        if (event.getType() == BusEvent.GET_DEMAND_AND_PACKAGE_COUNT_COMPLETE) {
            boolean isSuccess = event.getBooleanParam();
            if (isSuccess) {
                BottomStateBean stateBean = (BottomStateBean) event.getParam();
                showDAndPRedPoint(stateBean.getRequirecount(), stateBean.getParcelcount());
                mFlResource.setVisibility(stateBean.isShowResource() ? View.VISIBLE : View.GONE);
                flGroup.setVisibility(stateBean.isShowResource() ? View.VISIBLE : View.GONE);
            }
            //CustomToast.makeText(this, "请求需求和包裹数量失败", Toast.LENGTH_SHORT).show();
        }
        if (event.getType() == BusEvent.GO_MY_NEED) {
            mFrragmentIndex = 1;
            switchFrag(mFrragmentIndex);
            RadioButton child = (RadioButton) mRbgHomeFragment.findViewById(R.id.rb_need);
            child.setChecked(true);
        }


        if (event.getType() == BusEvent.SHOP_CART_COMMODITY_COUNT) {
            int count = event.getIntParam();
            SysApplication app = (SysApplication) getApplication();
            app.setQtyCount(count);
        }

    }


    /**
     * 需求和包裹的小红点方法
     *
     * @param demandCount  需求单数量
     * @param packageCount 包裹数量
     */
    public void showDAndPRedPoint(int demandCount, int packageCount) {
        mIv_demand_red_point.setVisibility(demandCount > 0 ? View.VISIBLE : View.GONE);
        mIv_package_red_point.setVisibility(packageCount > 0 ? View.VISIBLE : View.GONE);
    }

    /*适配6.0 权限检查*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(PermissionCode.readSDcard)
    public void requestContactSuccess() {
        new UpdateManager().getNewVersion(this, 0);
    }

    @PermissionDenied(PermissionCode.readSDcard)
    public void requestContactFailed() {
    }
}
