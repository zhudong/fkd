package com.fuexpress.kr.ui.activity.choose_category;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.ClassifyGroupAdapter;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsUser;

/**
 * Created by Longer on 2017/1/17.
 * 选择分类的父级Fragment
 */
public class CategoryFatherFragment extends BaseFragment<ChooseCategoryActivity> implements AdapterView.OnItemClickListener {

    @BindView(R.id.tv_in_parent_category)
    TitleBarView mTvInParentCategory;
    @BindView(R.id.lv_parent_category)
    ListView mLvParentCategory;
    private int mChooseID;
    private ClassifyGroupAdapter mClassifyGroupAdapter;
    private boolean mIsClick = false;

    @Override
    protected void initTitleBar() {
        mTvInParentCategory.getIv_in_title_back().setOnClickListener(this);
        TextView textView = mTvInParentCategory.getmTv_in_title_back_tv();
        String whereFrom = mContext.getWhereFrom();
        textView.setText(whereFrom);
        mTvInParentCategory.setTitleText(getString(R.string.choose_category_back_text));
        textView.setOnClickListener(this);
    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.parent_category_layout, null);
    }

    @Override
    public void initData() {
        List<CsUser.MatchItemCategory> dataList = CategoryDataManager.getInstance().getDataList(-1);
        //if (dataList == null) {
            CategoryDataManager.getInstance().getCategoryDataFromNet(-1, new CategoryDataManager.CategoryDataListener() {
                @Override
                public void onSuccess(List<CsUser.MatchItemCategory> list) {
                    initLvView(list);
                }

                @Override
                public void onFail(int ret, String errMsg) {
                    LogUtils.e(errMsg);
                }
            });
        /*} else
            initLvView(dataList);*/
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CsUser.MatchItemCategory itemAtPosition = (CsUser.MatchItemCategory) parent.getItemAtPosition(position);
        int matchItemCategoryId = itemAtPosition.getMatchItemCategoryId();
        setChoosePosition(matchItemCategoryId);
        mContext.setParentText(itemAtPosition.getMatchItemCategoryName());
        mIsClick = true;
        mContext.changeToSubFragment(matchItemCategoryId);
    }

    public boolean isClick() {
        return mIsClick;
    }

    public void initLvView(final List<CsUser.MatchItemCategory> list) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mClassifyGroupAdapter = new ClassifyGroupAdapter(mContext, list, mChooseID);
                mLvParentCategory.setAdapter(mClassifyGroupAdapter);
                mLvParentCategory.setOnItemClickListener(CategoryFatherFragment.this);
            }
        });
    }

    public void setChoosePosition(int position) {
        mChooseID = position;
        if (mClassifyGroupAdapter != null) {
            mClassifyGroupAdapter.setMatchItemId(position);
            mClassifyGroupAdapter.notifyDataSetChanged();
        }
    }

    public int getChooseID() {
        return mChooseID;
    }
}
