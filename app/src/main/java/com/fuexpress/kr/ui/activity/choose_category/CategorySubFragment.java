package com.fuexpress.kr.ui.activity.choose_category;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.ClassifyChildAdapter;
import com.fuexpress.kr.ui.adapter.ClassifyGroupAdapter;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsUser;

/**
 * Created by Longer on 2017/1/17.
 * 选择分类的子级Fragment
 */
public class CategorySubFragment extends BaseFragment<ChooseCategoryActivity> implements AdapterView.OnItemClickListener {

    @BindView(R.id.tv_in_sub_category)
    TitleBarView mTvInSubCategory;
    @BindView(R.id.lv_sub_category)
    ListView mLvSubCategory;
    private int mSubID;
    private String mSubName = "";
    private ClassifyChildAdapter mClassifyChildAdapter;
    private List<CsUser.MatchItemCategory> mMatchItemCategoryListList;
    private boolean mIsClick = false;

    @Override
    protected void initTitleBar() {
        mTvInSubCategory.getIv_in_title_back().setOnClickListener(this);
        TextView textView = mTvInSubCategory.getmTv_in_title_back_tv();
        textView.setText(getString(R.string.choose_category_back_text));
        textView.setOnClickListener(this);
        mTvInSubCategory.setTitleText(mContext.getParentText());
    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.sub_category_layout, null);
    }

    @Override
    public void initData() {

        List<CsUser.MatchItemCategory> dataList = CategoryDataManager.getInstance().getDataList(mContext.getParentID());
        if (dataList == null) {
            CategoryDataManager.getInstance().getCategoryDataFromNet(mContext.getParentID(), new CategoryDataManager.CategoryDataListener() {
                @Override
                public void onSuccess(List<CsUser.MatchItemCategory> list) {
                    initLvView(list);
                }

                @Override
                public void onFail(int ret, String errMsg) {
                    LogUtils.e(errMsg);
                }
            });
        } else
            initLvView(dataList);
    }

    public void initLvView(final List<CsUser.MatchItemCategory> list) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mClassifyChildAdapter = new ClassifyChildAdapter(mContext, list, mSubID);
                mLvSubCategory.setAdapter(mClassifyChildAdapter);
                mLvSubCategory.setOnItemClickListener(CategorySubFragment.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                mContext.onBackPressed();
                break;
        }
    }

    /**
     * 设置子分类被选中的ID
     *
     * @param subID 子分类ID
     */
    public void setChooseID(int subID) {
        mSubID = subID;
    }

    public int getSubID() {
        return mSubID;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CsUser.MatchItemCategory itemAtPosition = (CsUser.MatchItemCategory) parent.getItemAtPosition(position);
        setChooseID(itemAtPosition.getMatchItemCategoryId());
        //mContext.getmListener();
        setSubName(itemAtPosition.getMatchItemCategoryName());
        mIsClick = true;
        mContext.finish();
    }

    public boolean isClick() {
        return mIsClick;
    }

    public String getSubName() {
        if (mSubID != -1)
            if (TextUtils.isEmpty(mSubName)) {
                for (CsUser.MatchItemCategory category : mMatchItemCategoryListList) {
                    if (category.getMatchItemCategoryId() == mSubID)
                        mSubName = category.getMatchItemCategoryName();
                }
            }
        return mSubName;
    }

    public void setSubName(String subName) {
        mSubName = subName;
    }
}
