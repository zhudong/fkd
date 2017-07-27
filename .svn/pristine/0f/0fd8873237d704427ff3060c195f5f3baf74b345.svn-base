package com.fuexpress.kr.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.ShareManager;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.NetworkImageHolderView;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by k550 on 2016/6/24.
 */
public class ShareActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.cancelTv)
    TextView mCancelTv;
    @BindView(R.id.convenientBanner)
    ConvenientBanner mConvenientBanner;
    public static final String ITEM_ID = "itemId";
    public static final String IS_WECHAT = "is_wechat";
    public static final String WECHAT = "Wechat";
    public static final String QQ = "QQ";
    @BindView(R.id.size_tv)
    TextView mSizeTv;
    @BindView(R.id.shareLayout)
    RelativeLayout mShareLayout;
    @BindView(R.id.wxShareFriendLayout)
    RelativeLayout mWxShareFriendLayout;
    @BindView(R.id.wxShareTimeLayout)
    RelativeLayout mWxShareTimeLayout;
    @BindView(R.id.wxAddFavLayout)
    RelativeLayout mWxAddFavLayout;
    @BindView(R.id.qqShareFriendLayout)
    RelativeLayout mQqShareFriendLayout;
    @BindView(R.id.qqShareQzoneLayout)
    RelativeLayout mQqShareQzoneLayout;
    private long mItemId = 0;
    private String wechatStr = IS_WECHAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册EventBus
        EventBus.getDefault().register(this);
      /*  getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        // Note that flag changes must happen *before* the content view is set.*/
        View view = View.inflate(this, R.layout.actiivty_dialog_share, null);
        setFinishOnTouchOutside(true);
        setContentView(view);
        ButterKnife.bind(this);
        hideShareLayout();
        mItemId = getIntent().getLongExtra(ITEM_ID, 0);
        wechatStr = getIntent().getStringExtra(IS_WECHAT);
        if (WECHAT.equals(wechatStr)) {
            mWxAddFavLayout.setVisibility(View.VISIBLE);
            mWxShareFriendLayout.setVisibility(View.VISIBLE);
            mWxShareTimeLayout.setVisibility(View.VISIBLE);
            mCancelTv.setTextColor(UIUtils.getColor(R.color.share_green));
        }
        if (QQ.equals(wechatStr)) {
            mShareLayout.setVisibility(View.VISIBLE);
            mQqShareQzoneLayout.setVisibility(View.VISIBLE);
            mQqShareFriendLayout.setVisibility(View.VISIBLE);
            mCancelTv.setTextColor(UIUtils.getColor(R.color.share_blue));
        }
        setUrlList();
        KLog.i("id = " + mItemId);
       /* if (mItemId != 0) {
            ShareManager.init(mItemId);
        }*/

    }

    private void setUrlList() {
        mConvenientBanner.refreshDrawableState();

          /*  int screenWidthPixels = UIUtils.getScreenWidthPixels(this);
            int height = (int) (screenWidthPixels / 2.4545);
            mConvenientBanner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));*/
        mSizeTv.setText("共" + ShareManager.urls.size() + "张");
        if (ShareManager.urls.size() <= 1) {
            mConvenientBanner.stopTurning();
            mConvenientBanner.setCanLoop(false);
        } else {
            //    mConvenientBanner.startTurning(5000);
            mConvenientBanner.setCanLoop(false);
            mConvenientBanner.setPageIndicator(new int[]{R.drawable.graypoint, R.drawable.redpoint});
        }
        List<String> url = new ArrayList<>();
        for (String str : ShareManager.urls) {
            if (!str.contains("!dplist")) {
                str = str + "!dplist";
            }
            url.add(str);
        }

        mConvenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, url)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

    }

    public void onEventMainThread(BusEvent event) {
        if (event.getType() == BusEvent.GET_SHARE_IMAGE_LIST_SUCCESS) {
            mConvenientBanner.refreshDrawableState();

          /*  int screenWidthPixels = UIUtils.getScreenWidthPixels(this);
            int height = (int) (screenWidthPixels / 2.4545);
            mConvenientBanner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));*/
            mSizeTv.setText("共" + ShareManager.urls.size() + "张");
            if (ShareManager.urls.size() <= 1) {
                mConvenientBanner.stopTurning();
                mConvenientBanner.setCanLoop(false);
            } else {
                //    mConvenientBanner.startTurning(5000);
                mConvenientBanner.setCanLoop(false);
                mConvenientBanner.setPageIndicator(new int[]{R.drawable.graypoint, R.drawable.redpoint});
            }
            List<String> url = new ArrayList<>();
            for (String str : ShareManager.urls) {
                if (!str.contains("!dplist")) {
                    str = str + "!dplist";
                }
                url.add(str);
            }

            mConvenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            }, url)
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

            //    mConvenientBanner.startTurning(5000);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ShareManager.downloadComplete = false;
        ShareManager.shareNow = false;
        EventBus.getDefault().unregister(this);
    }

    public static boolean isClintAvailable(Context context, String pageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(pageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @OnClick({R.id.cancelTv, R.id.shareLayout, R.id.wxShareFriendLayout, R.id.wxShareTimeLayout, R.id.wxAddFavLayout, R.id.qqShareFriendLayout, R.id.qqShareQzoneLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelTv:
                finish();
                break;
            case R.id.shareLayout:
                //添加到qq收藏
                Share(Constants.SHARE_CLEINT_NAME.QQ_ADD_SHARE_PACKAGE_NAME, Constants.SHARE_CLEINT_NAME.QQ_ADD_SHARE_ACTIVITY_NAME);
                break;
            case R.id.wxShareFriendLayout:
                //分享给微信好友
                Share(Constants.SHARE_CLEINT_NAME.WX_FRIEND_SHARE_PACKAGE_NAME, Constants.SHARE_CLEINT_NAME.WX_FRIEND_SHARE_ACTIVITY_NAME);
                break;
            case R.id.wxShareTimeLayout:
                //分享到微信朋友圈
                Share(Constants.SHARE_CLEINT_NAME.WX_TIMELINE_SHARE_PACKAGE_NAME, Constants.SHARE_CLEINT_NAME.WX_TIMELINE_SHARE_ACTIVITY_NAME);
                break;
            case R.id.wxAddFavLayout:
                //添加到微信收藏
                Share(Constants.SHARE_CLEINT_NAME.WX_ADD_SHARE_PACKAGE_NAME, Constants.SHARE_CLEINT_NAME.WX_ADD_SHARE_ACTIVITY_NAME);
                break;
            case R.id.qqShareFriendLayout:
                //分享给qq好友
                Share(Constants.SHARE_CLEINT_NAME.QQ_SHARE_PACKAGE_NAME, Constants.SHARE_CLEINT_NAME.QQ_SHARE_ACTIVITY_NAME);
                break;
            case R.id.qqShareQzoneLayout:
                //分享到qq空间
                Share(Constants.SHARE_CLEINT_NAME.QQ_ZONE_SHARE_PACKAGE_NAME, Constants.SHARE_CLEINT_NAME.QQ_ZONE_SHARE_ACTIVITY_NAME);
                break;
        }
    }

    private void Share(String packageName, String activityName) {
        if (!isClintAvailable(ShareActivity.this, packageName)) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.share_error_info), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Intent intent = new Intent();
        ComponentName comp = new ComponentName(packageName,
                activityName);
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putExtra("Kdescription", ShareManager.desc);
        intent.putExtra(Intent.EXTRA_TEXT, ShareManager.desc);
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (File f : getFiles()) {
            KLog.i(f.getName().toString());
            imageUris.add(Uri.fromFile(f));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        KLog.i("shareToTimeLine");
        startActivity(intent);
        finish();
    }

    public File[] getFiles() {
        File root = this.getExternalFilesDir("Download");
        KLog.i("root.name = " + root.getAbsolutePath());
        File[] files = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".png") || pathname.getName().endsWith(".jpg"))
                    return true;
                return false;
            }
        });
        for (File file : files) {
            KLog.i("file.name=  " + file.getName());
        }
        return files;
    }

    private void hideShareLayout() {
        mShareLayout.setVisibility(View.GONE);
        mWxShareFriendLayout.setVisibility(View.GONE);
        mWxShareTimeLayout.setVisibility(View.GONE);
        mWxAddFavLayout.setVisibility(View.GONE);
        mQqShareFriendLayout.setVisibility(View.GONE);
        mQqShareQzoneLayout.setVisibility(View.GONE);
    }
}
