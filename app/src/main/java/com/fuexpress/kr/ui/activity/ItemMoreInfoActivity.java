package com.fuexpress.kr.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;


public class ItemMoreInfoActivity extends BaseActivity {

    public static final String TAG = "CROWD";
    public static String URL = "url";
    public static String TITLE = "title";
    public static String NAVIGATION = "navigation";
    private TextView mTvRight;
    private String mUrl;
    private TextView mTitleName;
    private TextView mTvLeft;
    private ImageView mIvContent;
    private LinearLayout mLinearLayout;

    @Override
    public View setInitView() {
        View rootView = View.inflate(this, R.layout.activity_item_more_info_detail, null);

        mTvRight = (TextView) rootView.findViewById(R.id.title_tv_right);
        mTvLeft = (TextView) rootView.findViewById(R.id.title_tv_left);
        rootView.findViewById(R.id.title_iv_left).setOnClickListener(this);
        mTvRight.setText(getResources().getString(R.string.String_refresh));
        mTvLeft.setOnClickListener(this);

        mTitleName = (TextView) rootView.findViewById(R.id.title_tv_center);
        mIvContent = (ImageView) rootView.findViewById(R.id.iv_content);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.ll_container);
        boolean isCrowd = getIntent().getBooleanExtra(TAG, false);
        String title = getIntent().getStringExtra(TITLE);
        String nav = getIntent().getStringExtra(NAVIGATION);
        mTitleName.setText(getResources().getString(R.string.String_crowd_detail));

        mUrl = getIntent().getStringExtra(URL);
//        LogUtils.d("web", mUrl);
        return rootView;
    }


    @Override
    public void initView() {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(mUrl, mIvContent, ImageLoaderHelper.getInstance(this).getDisplayOptions());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.title_tv_right:
                break;
        }

    }
}
