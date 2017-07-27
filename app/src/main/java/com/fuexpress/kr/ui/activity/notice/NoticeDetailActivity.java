package com.fuexpress.kr.ui.activity.notice;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * Created by kevin.xie on 2016/10/31.
 */

public class NoticeDetailActivity extends BaseActivity {
    public static String CSID="itemId";
    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    @BindView(R.id.webView)
    WebView mWebView;

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_help_center, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTitleIvLeft.setVisibility(View.VISIBLE);
        mTitleTvCenter.setText(getString(R.string.system_notice));
        long   id=getIntent().getLongExtra(CSID,0);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains("decorator")) {
                    url = url + "&decorator=empty";
                }
                view.loadUrl(url);
                KLog.i("url = "+url);
                return true;
            }
        });
        mWebView.loadUrl(getUrl(id));
    }

    @OnClick(R.id.title_iv_left)
    public void onClick() {
        onBackPressed();
    }
    public String getUrl(long id){
        String url= Constants.WebWiewUrl.getH5Url()+"/app/noticeView?decorator=empty&isAppH5=1&"+"coreMsgId="+id+"&uin="+ AccountManager.getInstance().mUin
                +"&ticket="+ AccountManager.getInstance().mH5Ticket+"&localeCode="+
                AccountManager.getInstance().getLocaleCode();
        KLog.i("url = "+url);
        return url;
    }
}
