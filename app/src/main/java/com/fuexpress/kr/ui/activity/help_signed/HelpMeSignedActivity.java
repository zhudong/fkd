package com.fuexpress.kr.ui.activity.help_signed;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.activity.choose_category.ChooseCategoryActivity;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSignedLocalDataSource;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSignedRemoteDataSource;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpSingedDataRepository;
import com.fuexpress.kr.utils.UpLoadImageUtils;

public class HelpMeSignedActivity extends BaseActivity {

    public static final String HMS_STATE_KEY = "hms_state_key";
    public static final String ADD_DEMAND_AGAIN = "add_demand_again";
    private FragmentManager mSupportFragmentManager;
    private final static String HS_PR_TAG = "hs_pr_tag";
    private final static String HS_ITEM_TAG = "hs_item_tag";
    private HelpSignedContract.PreViewPresenter mPresenter;


    private boolean mIsClickSub = false;

    private ArrayMap<Integer, Boolean> mIndexIsClickConfMap;

    private int mCurrentIndex = -1;
    private HelpSignedLocalDataSource mHelpSignedLocalDataSource;
    private String mState;


    @Override
    public View setInitView() {
        //mFrameLayout = (FrameLayout) findViewById(R.id.fl_help_signed_contair);
        mHelpSignedLocalDataSource = HelpSignedLocalDataSource.getInstance();
        return View.inflate(this, R.layout.layout_for_help_signed, null);
    }

    public void setIsClickSub(boolean isClickSub) {
        mIsClickSub = isClickSub;
    }

    public boolean getIsClickSub() {
        return mIsClickSub;
    }

    @Override
    public void initView() {
        mSupportFragmentManager = getSupportFragmentManager();
        /*HelpSignedFragment helpSignedFragment = (HelpSignedFragment) mSupportFragmentManager.findFragmentById(R.id.fl_help_signed_contair);
        if (helpSignedFragment == null) {
            helpSignedFragment = new HelpSignedFragment();
            HelpSignedContract.Presenter presenter = new HelpSignedPresenter(HelpSingedDataRepository.getInstance(new HelpSignedLocalDataSource(), this), helpSignedFragment);
            helpSignedFragment.setPresenter(presenter);
            ActivityUtils.addFragmentToActivity(mSupportFragmentManager, helpSignedFragment, R.id.fl_help_signed_contair);
        }*/
        HSPreviewFragment hsPreviewFragment = new HSPreviewFragment();
        mPresenter = new HelpSignedPVPresenter(HelpSingedDataRepository.getInstance(mHelpSignedLocalDataSource), hsPreviewFragment);
        hsPreviewFragment.setPresenter(mPresenter);
        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fl_help_signed_contair, hsPreviewFragment, HS_PR_TAG);
        fragmentTransaction.commit();
        mState = getIntent().getStringExtra(HMS_STATE_KEY);
        //LogUtils.e("状态:" + stringExtra);
        if (mState != null && ADD_DEMAND_AGAIN.equals(mState))
            changeDetailFragmentShow(HelpSingedDataRepository.getInstance(mHelpSignedLocalDataSource).getHelpSingleBeanList().size());//假如是从详情进来的,我们给个-2
    }

    /**
     * 让列表Fragment和详情(添加)Fragment之间进行切换的方法
     */
    public void changeDetailFragmentShow(int position) {//-1的情况下,我们跳转到成功界面
        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        if (mState == null)
            fragmentTransaction.setCustomAnimations(
                    R.anim.push_left_in,
                    R.anim.push_left_out,
                    R.anim.push_right_in,
                    R.anim.push_right_out);
        else {
            if (position != -1) {
                fragmentTransaction.setCustomAnimations(
                        0,
                        0,
                        R.anim.push_right_in,
                        R.anim.push_right_out);
                HelpSingedDataRepository.getInstance(mHelpSignedLocalDataSource)
                        .setHelpSignedRemoteDataSource(HelpSignedRemoteDataSource.getInstance());//如果是再下一单状态,设置好远程数据
                HelpSingedDataRepository.getInstance(mHelpSignedLocalDataSource).getIsIndexCompleteUpLoadMap().put(position, true);//如果是再下一单状态,初始化好上传图片状态
                mState = null;//同时重置状态，防止其再添加远程需求单
            }
        }
        if (position != -1) {
            HSItemDetailFragment hsItemDetailFragment = new HSItemDetailFragment();
            HelpSignedNewPresenter helpSignedNewPresenter = new HelpSignedNewPresenter(
                    HelpSingedDataRepository.getInstance(mHelpSignedLocalDataSource)
                    , hsItemDetailFragment, position);
            hsItemDetailFragment.setPresenter(helpSignedNewPresenter);
            fragmentTransaction.replace(R.id.fl_help_signed_contair, hsItemDetailFragment, HS_ITEM_TAG);
        } else {
            fragmentTransaction.replace(R.id.fl_help_signed_contair, new HSSuccessFragment());
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public Context getHelpSignedContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        HSItemDetailFragment helpSignedFragment = (HSItemDetailFragment) supportFragmentManager.findFragmentByTag(HS_ITEM_TAG);
        helpSignedFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentIndex != -1) {
            Boolean isClickConf = mIndexIsClickConfMap.get(mCurrentIndex);
            if (!isClickConf) {//假如点击了确认键之后,无论其回退多少次,我们都不阻塞其上传图片
                UpLoadImageUtils.getInstance().setContinue(mCurrentIndex, false);//假如没有点击,我们就阻塞其上传图片
            }
        }
        // Pop fragment stack on back button
        if (mSupportFragmentManager.getBackStackEntryCount() > 0) {
            mSupportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //return;
        } else {
            super.onBackPressed();
        }
    }

    public void setIndexIsClickConf(int index, boolean isClickConf) {//设置该Index下的Item是否点击了确认键
        if (mIndexIsClickConfMap == null)
            mIndexIsClickConfMap = new ArrayMap<>();
        mIndexIsClickConfMap.put(index, isClickConf);
    }

    public void setCurrentIndex(int index) {//设置当前是那个Index下的Item
        mCurrentIndex = index;
        if (mIndexIsClickConfMap == null)
            mIndexIsClickConfMap = new ArrayMap<>();
        if (!mIndexIsClickConfMap.keySet().contains(index))//当该index没有记录有是否点击确认状态时,我们就给一个默认值(没有点击确认按钮)
            mIndexIsClickConfMap.put(index, false);
    }

    public void startIntentActivity() {
        Intent intent = new Intent(this, ChooseCategoryActivity.class);
        startActivity(intent);
    }
}