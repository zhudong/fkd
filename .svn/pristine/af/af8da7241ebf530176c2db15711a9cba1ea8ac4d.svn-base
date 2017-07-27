package com.fuexpress.kr.ui.activity.choose_category;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.conf.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChooseCategoryActivity extends BaseActivity {


    CategoryFatherFragment mCategoryFatherFragment;

    CategorySubFragment mCategorySubFragment;

    private static final String KEY_F_TAG = "F_TAG";

    private static final String KEY_SUB_TAG = "SUB_TAG";

    private String mParentText = "";

    private int mParentID = -1;

    private int mSubID = -1;

    private static ChooseCategoryListener mListener;
    private String mWhereFrom;

    public interface ChooseCategoryListener {
        void select(boolean select, CategoryBean bean);
    }

    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_choose_category, null);
    }


    @Override
    public void initView() {
        setWhereFrom(getIntent().getStringExtra(Constants.KEY_WHERE_FROM));
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (mCategoryFatherFragment == null) {
            mCategoryFatherFragment = new CategoryFatherFragment();
        }
        mCategoryFatherFragment.setChoosePosition(getIntent().getIntExtra(Constants.KEY_PARENTID, -1));
        fragmentTransaction.add(R.id.fl_container_in_category, mCategoryFatherFragment, KEY_F_TAG);
        fragmentTransaction.commit();
    }

    public void setParentText(String parentText) {
        mParentText = parentText;
    }

    public String getParentText() {
        return mParentText;
    }

    public int getParentID() {
        return mParentID;
    }

    public void setParentID(int parentID) {
        mParentID = parentID;
    }

    public String getWhereFrom() {
        return mWhereFrom;
    }

    public void setWhereFrom(String whereFrom) {
        mWhereFrom = whereFrom;
    }

    /**
     * 切换到子分类Fragment
     *
     * @param parentID 父分类的ID
     */
    public void changeToSubFragment(int parentID) {
        setParentID(parentID);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out,
                R.anim.push_right_in,
                R.anim.push_right_out);
        if (mCategorySubFragment == null)
            mCategorySubFragment = new CategorySubFragment();
        mCategorySubFragment.setChooseID(getIntent().getIntExtra(Constants.KEY_SUBID, -1));
        fragmentTransaction.replace(R.id.fl_container_in_category, mCategorySubFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static ChooseCategoryListener getmListener() {
        return mListener;
    }

    public static void setmListener(ChooseCategoryListener mListener) {
        ChooseCategoryActivity.mListener = mListener;
    }

    @Override
    public void finish() {
        if (mCategoryFatherFragment != null && mCategorySubFragment != null) {
            int pID = mCategoryFatherFragment.getChooseID();
            int subID = mCategorySubFragment.getSubID();
            if (mCategoryFatherFragment.isClick() && mCategorySubFragment.isClick()) {
                CategoryBean categoryBean = new CategoryBean();
                categoryBean.setSubName(mCategorySubFragment.getSubName());
                categoryBean.setSubID(subID);
                categoryBean.setParentID(pID);
                mListener.select(true, categoryBean);
            } else
                mListener.select(false, null);
        } else
            mListener.select(false, null);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        // Pop fragment stack on back button
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //return;
        } else {
            super.onBackPressed();
        }
    }

    public static class IntentBuilder extends Intent {
        static IntentBuilder thisIntent;

        private IntentBuilder(Context packageContext, Class<?> cls) {
            super(packageContext, cls);
        }

        public IntentBuilder setListener(ChooseCategoryListener listener) {
            checkNotNull(thisIntent, "please build first!");
            mListener = listener;
            return thisIntent;
        }

        public IntentBuilder setParentID(int parentID) {
            checkNotNull(thisIntent, "please build first!");
            thisIntent.putExtra(Constants.KEY_PARENTID, parentID);
            return thisIntent;
        }

        public IntentBuilder setSubID(int subID) {
            checkNotNull(thisIntent, "please build first!");
            thisIntent.putExtra(Constants.KEY_SUBID, subID);
            return thisIntent;
        }

        public IntentBuilder setWhereFrom(String whereFrom) {
            checkNotNull(thisIntent, "please build first!");
            thisIntent.putExtra(Constants.KEY_WHERE_FROM, whereFrom);
            return thisIntent;
        }

        public static IntentBuilder build(Context context) {
            if (thisIntent == null)
                thisIntent = new IntentBuilder(context, ChooseCategoryActivity.class);
            return thisIntent;
        }
    }

}
