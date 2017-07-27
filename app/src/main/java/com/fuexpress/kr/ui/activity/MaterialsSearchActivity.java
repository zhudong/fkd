package com.fuexpress.kr.ui.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.model.MaterialsManager;
import com.fuexpress.kr.ui.adapter.MaterialsAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.MaterialItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MaterialsSearchActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView mImgBack;
    @BindView(R.id.img_delete_input)
    ImageView mImgDeleteInput;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.ed_search_input)
    EditText mEdSearchInput;
    @BindView(R.id.lv_body)
    ListView mLvBody;
    @BindView(R.id.rl_title_root)
    RelativeLayout mRlTitleRoot;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    private ArrayList<MaterialsBean> mData;
    private MaterialsAdapter mAdapter;

    private static iMaterialsSelectListener mListener;
    private SpannableString mSs;


    public interface iMaterialsSelectListener {
        void select(boolean select, MaterialsBean bean);
    }


    @Override
    public View setInitView() {
        return View.inflate(this, R.layout.activity_materials_search, null);
    }


    @Override
    public void initView() {
//        super.initView();
        Drawable d = getResources().getDrawable(R.mipmap.search_small);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        mSs = new SpannableString(getString(R.string.search_materials));
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        mSs.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mEdSearchInput.setHint(mSs);
        mData = new ArrayList<>();
        mAdapter = new MaterialsAdapter(this, mData, mCheckedID);
        mLvBody.setAdapter(mAdapter);
        getMaterials("", 0);
        initEvent();
    }

    private void initEvent() {

        mLvBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MaterialsBean item = mAdapter.getItem(position);
                MaterialsManager.getInstance().update(item);
                if (mListener != null) {
                    mListener.select(true, item);
                }
                ((MaterialItemLayout) view).setSelect(true);
                MaterialsSearchActivity.this.finish();
            }
        });


        mEdSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String key = s.toString();
                if (TextUtils.isEmpty(key)) {
                    mImgDeleteInput.setVisibility(View.GONE);
                } else {
                    mImgDeleteInput.setVisibility(View.VISIBLE);
                }
                if ("all".equals(key)) {
                    getMaterials("", 0);
                } else {
                    getMaterials(key, 1);
                }

            }
        });

        mEdSearchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scalEdit(false);
                }
            }
        });
    }


    private void scalEdit(boolean open) {
//        12 52dp
        int start = UIUtils.dip2px(12);
        int stop = UIUtils.dip2px(52);
        final int right = UIUtils.dip2px(36);
        final int top = UIUtils.dip2px(4);
        ValueAnimator animator;

        if (open) {
            mEdSearchInput.clearFocus();
            animator = ValueAnimator.ofInt(stop, start);
            mEdSearchInput.setGravity(Gravity.CENTER);
            mEdSearchInput.setHint(mSs);
            mEdSearchInput.setText("");
        } else {
            animator = ValueAnimator.ofInt(start, stop);
            mEdSearchInput.setGravity(Gravity.LEFT);
            mEdSearchInput.setHint("");
        }
        animator.setCurrentPlayTime(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mEdSearchInput.getLayoutParams());
                params.setMargins(right, top, value, top);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                mRlTitleRoot.updateViewLayout(mEdSearchInput, params);
            }
        });
        animator.start();
    }


    @OnClick({R.id.tv_cancel, R.id.img_delete_input, R.id.img_back})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                scalEdit(true);
                break;
            case R.id.img_delete_input:
                mEdSearchInput.setText("");
                break;
            case R.id.img_back:
//                mEdSearchInput.setText("");
                onBackPressed();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mListener != null) {
            mListener.select(false, null);
        }
    }


    public void getMaterials(String key, int type) {
        List<MaterialsBean> search;
        if (type == 0) {
            search = MaterialsManager.getInstance().search(key, true);
        } else {
            search = MaterialsManager.getInstance().search(key, false);
        }

        if (type == 1) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            if (search.size() > 0) mProgressBar.setVisibility(View.GONE);
        }

        mData.clear();
        mData.addAll(search);
        mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.GET_MATERIALS_RESULT) {
            getMaterials("", 1);
        }
    }


    static private int mCheckedID;

    public static class IntentBuilder {
        Bundle arguments = new Bundle();

        public IntentBuilder(int checkedID) {
            mCheckedID = checkedID;
        }

        public Intent build(Context context, iMaterialsSelectListener listener) {
            Intent intent = new Intent(context, MaterialsSearchActivity.class);
//            context.startActivity(intent);
            mListener = listener;
            return intent;
        }
    }
}
