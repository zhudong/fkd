package com.fuexpress.kr.ui.activity;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.utils.LogUtils;


public class HomeWeblActivity extends BaseActivity {

    public static final String TAG = "CROWD";

    public static String URL = "url";
    public static String TITLE = "title";
    public static String NAVIGATION = "navigation";
    private WebView mWebwiew;
    private ProgressBar mPb;
    private TextView mTvRight;
    private String mUrl;
    private TextView mTitleName;
    private TextView mTvLeft;

    @Override
    public View setInitView() {
        View rootView = View.inflate(this, R.layout.activity_item_detail, null);
        rootView.findViewById(R.id.title_iv_left).setOnClickListener(this);
        mTvLeft = (TextView) rootView.findViewById(R.id.title_tv_left);
        mTitleName = (TextView) rootView.findViewById(R.id.title_tv_center);
        mTvRight = (TextView) rootView.findViewById(R.id.title_tv_right);
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setOnClickListener(this);
        mTvLeft.setOnClickListener(this);
        mTvRight.setText(getResources().getString(R.string.String_refresh));

        boolean isCrowd = getIntent().getBooleanExtra(TAG, false);
        String title = getIntent().getStringExtra(TITLE);
        String nav = getIntent().getStringExtra(NAVIGATION);

        if (title != null && !"".equals(title)) {
            mTitleName.setText(title);
            mTvRight.setVisibility(View.GONE);
        }

        if (!"".equals(nav)) {
            mTvLeft.setVisibility(View.VISIBLE);
            mTvLeft.setText(nav);
        }

        mWebwiew = (WebView) rootView.findViewById(R.id.wv_item_detail);
        WebSettings webSettings = mWebwiew.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);
        mPb = (ProgressBar) rootView.findViewById(R.id.activity_item_detail_pb);

        mUrl = getIntent().getStringExtra(URL);
        LogUtils.d("web", mUrl);
        initEvent();
        mWebwiew.loadUrl(mUrl);
        return rootView;
    }

    private void initEvent() {
        mWebwiew.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPb.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });


        mWebwiew.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mPb.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mPb.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }
        });

        mWebwiew.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebwiew.canGoBack()) {  //表示按返回键
                        mWebwiew.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
            case R.id.title_tv_right:
                mWebwiew.loadUrl(mUrl);
                break;
        }

    }
}
