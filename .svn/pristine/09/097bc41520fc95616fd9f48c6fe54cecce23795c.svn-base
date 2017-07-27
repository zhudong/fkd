package com.fuexpress.kr.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.AccessTokenKeeper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.protobuf.GeneratedMessage;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.socks.library.KLog;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fksproto.CsUser;

/**
 * Created by Administrator on 2017-2-27.
 */

public class InviteFriendsActivity extends BaseActivity implements IWXAPIEventHandler {
    public static final int SEND_SMS_TYPE = 100;
    public IWXAPI iwxapi;//微信分享
    private Tencent mTencent;//qq分享
    private IWeiboShareAPI mWeiboShareApi;//微博分享
    public static String Text_Info = "我想邀请你加入 付快递";
    public static String Text_Url = "https://www.baidu.com";
    private final int[] RESOURCES_NAME = {
            R.string.invite_resources_1,
            R.string.invite_resources_2,
            R.string.invite_resources_8,
            R.string.invite_resources_3,
            R.string.invite_resources_4,
            R.string.invite_resources_5,
            R.string.invite_resources_6,
            R.string.invite_resources_7
    };
    private byte[] image2WX;
    private final int[] RESOURCES_LOGO = {
            R.mipmap.invite_phonebook,
            R.mipmap.invite_sms,
            R.mipmap.invite_facebook,
            R.mipmap.invite_wechat_friend,
            R.mipmap.invite_wechat,
            R.mipmap.invite_qzone,
            R.mipmap.invite_qq,
            R.mipmap.invite_weibo
    };

    private static final int POSITION_INVITE_PHONEBOOK = 0;
    private static final int POSITION_INVITE_SMS = 1;
    private static final int POSITION_INVITE_FACEBOOK = 2;
    private static final int POSITION_INVITE_WECHAT_FRIEND = 3;
    private static final int POSITION_INVITE_WECHAT = 4;
    private static final int POSITION_INVITE_QZONE = 5;
    private static final int POSITION_INVITE_QQ = 6;
    private static final int POSITION_INVITE_WEIBO = 7;

    private static final String MSG_CONTACT = "inviteDesc";
    private static final String MSG_SMS = "inviteDesc";
    private static final String MSG_FACEBOOK = "inviteFacebook";
    private static final String MSG_WX_SESSION = "inviteMoments";
    private static final String MSG_WEIXIN = "inviteWechat";
    private static final String MSG_QQ_ZONE = "inviteQQS";
    private static final String MSG_QQ_FRIEND = "inviteQQF";
    private static final String MSG_WEIBO = "inviteWeibo";

    private View rootView;
    private TitleBarView titleBarView;
    private TextView backTv;
    private ImageView backIv;
    private ListView listView;
    private TextView titleTv;
    private TextView infoTv;
    public static CsUser.GetInviteConfigResponse response;

    private static final int REQUEST_CODE_READ_CONTACT = 2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //    private static final int REQUEST_CODE_WRITE_CONTACT = 0x10002;
//    private static final int REQUEST_CODE_WRITE_CONTACT = 0x10002;

    //    private static final int REQUEST_CODE_WRITE_CONTACT = 0x10002;
//    private static final int REQUEST_CODE_READ_SMS = 0x10003;
//    private static final int REQUEST_CODE_WIRTE_SMS = 0x10004;
    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_invite_friends, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.invite_title_bar_view);
        backIv = titleBarView.getIv_in_title_back();
        backTv = titleBarView.getmTv_in_title_back_tv();
        // TODO: 2017/5/9 从首页幻灯片跳入的话,需要改变其返回的文字,因此改成需要上级页面传递返回标题并使用 --edit by longer
        String backTitleExtra = getIntent().getStringExtra(Constants.RETURN_TITLE);
        backTv.setText(TextUtils.isEmpty(backTitleExtra) ? getString(R.string.integral_management_msg) : backTitleExtra);

        titleTv = (TextView) rootView.findViewById(R.id.invite_title_tv);
        infoTv = (TextView) rootView.findViewById(R.id.invite_info_tv);
        listView = (ListView) rootView.findViewById(R.id.invite_list_view);
        MyAdapter adapter = new MyAdapter(this, RESOURCES_NAME);
        listView.setAdapter(adapter);
        creditsInviteInfoRequest();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            } else {
                MPermissions.requestPermissions(InviteFriendsActivity.this, REQUEST_CODE_READ_CONTACT, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS});
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        REQUEST_CODE_READ_CONTACT);
            }
//            MPermissions.requestPermissions(InviteFriendsActivity.this, 2, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.CAMERA});
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE_READ_CONTACT);
        } else {
        }
        listView.setOnItemClickListener(this);
        backTv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        //微信分享初始化
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WeiXin.WX_APPID_SHARE, true);
        iwxapi.registerApp(Constants.WeiXin.WX_APPID_SHARE);
        iwxapi.handleIntent(getIntent(), this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case POSITION_INVITE_PHONEBOOK:
                getInviteConfigRequest(MSG_CONTACT, position);
                //电话联系人
                break;
            case POSITION_INVITE_SMS:
                getInviteConfigRequest(MSG_SMS, position);
                //短信
                break;
            case POSITION_INVITE_WECHAT_FRIEND:
                getInviteConfigRequest(MSG_WX_SESSION, position);
                //微信朋友圈
                break;
            case POSITION_INVITE_WECHAT:
                getInviteConfigRequest(MSG_WEIXIN, position);
                //微信
                break;
            case POSITION_INVITE_QZONE:
                //QQ空间
                getInviteConfigRequest(MSG_QQ_ZONE, position);
                break;
            case POSITION_INVITE_QQ:
                //QQ
                getInviteConfigRequest(MSG_QQ_FRIEND, position);
                break;
            case POSITION_INVITE_WEIBO:
                //新浪微博
                getInviteConfigRequest(MSG_WEIBO, position);
                break;
            case POSITION_INVITE_FACEBOOK:
                getInviteConfigRequest(MSG_FACEBOOK, position);
                //Facebook
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case SEND_SMS_TYPE:
                        if (data == null) {
                            return;
                        }
                        ArrayList<String> strs = getPhoneData(data);
                        if (strs != null) {
                            if (strs.size() == 0) {
                                sendSms("");
                            } else if (strs.size() == 1) {
                                sendSms(strs.get(0));
                            } else {
                                Intent intent = new Intent(InviteFriendsActivity.this, ChooseUserPhoneActivity.class);
                                intent.putStringArrayListExtra(ChooseUserPhoneActivity.PHONE_LIST, strs);
                                InviteFriendsActivity.this.startActivity(intent);
                            }
                        }
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ArrayList<String> getPhoneData(Intent data) {
        String phoneNumber = null;
        Uri contactData = data.getData();
        if (contactData == null) {
            return null;
        }
        ArrayList<String> phoneList = new ArrayList<>();
        Cursor cursor = managedQuery(contactData, null, null, null, null);
        if (cursor.moveToFirst()) {
            String hasPhone = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            if (hasPhone.equalsIgnoreCase("1")) {
                hasPhone = "true";
            } else {
                hasPhone = "false";
            }
            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + id, null, null);
                while (phones.moveToNext()) {
                    phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    KLog.i("phone = " + phoneNumber);
                    phoneList.add(phoneNumber);
                    setTitle(phoneNumber);
                }
                phones.close();
            }
        }
        return phoneList;
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        System.out.println("resp=" + baseResp.errCode);

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(this, getString(R.string.share_success), Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this, getString(R.string.share_cancel), Toast.LENGTH_LONG).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, getString(R.string.share_failure), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, getString(R.string.share_failure), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (iwxapi != null) {
            iwxapi.handleIntent(intent, this);
        }
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("InviteFriends Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class MyAdapter extends BaseAdapter {

        private int[] data;
        private Context context;

        public MyAdapter(Context context, int[] data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.invite_item, null);
                holder = new ViewHolder();
                holder.logoIv = (ImageView) convertView.findViewById(R.id.invite_item_logo_iv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.invite_item_name_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.logoIv.setImageResource(RESOURCES_LOGO[position]);
            holder.nameTv.setText(getString(RESOURCES_NAME[position]));
            return convertView;
        }

        class ViewHolder {
            ImageView logoIv;
            TextView nameTv;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @PermissionGrant(REQUEST_CODE_READ_CONTACT)
    public void requestSdcardSuccess() {

        //    Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(REQUEST_CODE_READ_CONTACT)
    public void requestSdcardFailed() {
        //    Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    private void getContacts() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_PICK);
        i.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, SEND_SMS_TYPE);
    }

    public void sendSms(String toBody) {
        if (response == null) {
            return;
        }
        Uri smsToUri = Uri.parse("smsto:" + toBody);

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", response.getTitile() + response.getInviteurl());

        startActivity(intent);
    }

    //分享到微信好友或者朋友圈
    public void share2WX(int scene) {
        if (response == null) {
            return;
        }
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = response.getInviteurl();

        WXMediaMessage msg = new WXMediaMessage(wxWebpageObject);
        if (null != image2WX) {
            msg.thumbData = image2WX;
        }
        msg.title = response.getTitile();
        msg.description = response.getTitile();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
    }

    //分享到QQ好友
    public void share2QQF() {
        if (response == null) {
            return;
        }
        mTencent = Tencent.createInstance(Constants.QQ.QQ_APPID, this.getApplicationContext());
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, response.getTitile());             //QQ空间分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "福快递专注中韩物流");       //QQ分享摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, response.getInviteurl());   //QQ分享目标地址
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "福快递");     //QQ分享指定应用名称
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,response.getImageurl());
        //     params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        mTencent.shareToQQ(this, params, new BaseUiListener());
    }

    //分享到QQ空间
    public void share2QQZ() {
        if (response == null) {
            return;
        }
        mTencent = Tencent.createInstance(Constants.QQ.QQ_APPID, this.getApplicationContext());
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, response.getTitile());             //QQ空间分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, response.getTitile());        //QQ空间分享摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, response.getInviteurl());   //QQ空间分享目标链接
        ArrayList<String> urls = new ArrayList<>();
        urls.add(response.getImageurl());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);          //QQ空间分享图片集
        mTencent.shareToQzone(this, params, new BaseUiListener());
    }

    /**********************
     * QQ分享重写的方法
     ********************************/
    public class BaseUiListener implements IUiListener {
        protected void doComplete(JSONObject values) {
            Log.i("InviteFriend", "onComplete " + values.toString());
        }

        @Override
        public void onComplete(Object response) {
            Log.e("InviteFriend", "---------------111111");
            Toast.makeText(getApplicationContext(), getString(R.string.share_success), Toast.LENGTH_LONG).show();
            Log.i("InviteFriend", "onComplete " + response.toString());
            doComplete((JSONObject) response);
        }

        @Override
        public void onError(UiError e) {
            Log.i("InviteFriend", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            Log.i("InviteFriend", "cancel");
        }
    }

    //分享到FB好友
    public void share2FB() {
        if (response == null) {
            return;
        }
        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                .setImageUrl(Uri.parse(response.getImageurl()))
                .setContentUrl(Uri.parse(response.getInviteurl()))
                .setContentTitle(response.getTitile())
                .build();
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog.show((Activity) this, shareLinkContent);
        }
    }

    //获取分享的连接
    public void getInviteConfigRequest(String messageCode, final int position) {
        showLoading();
        CsUser.GetInviteConfigRequest.Builder builder = CsUser.GetInviteConfigRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetInviteConfigResponse>() {

            @Override
            public void onSuccess(final CsUser.GetInviteConfigResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();

                    }
                });
                LogUtils.d(response.toString());
                InviteFriendsActivity.this.response = response;
                switch (position) {
                    case POSITION_INVITE_PHONEBOOK:
                        getContacts();
                        //电话联系人
                        break;
                    case POSITION_INVITE_SMS:
                        sendSms("");
                        //短信
                        break;
                    case POSITION_INVITE_WECHAT_FRIEND:
                        share2WX(1);
                        //微信朋友圈
                        break;
                    case POSITION_INVITE_WECHAT:
                        share2WX(0);
                        //微信
                        break;
                    case POSITION_INVITE_QZONE:
                        //QQ空间
                        share2QQZ();
                        break;
                    case POSITION_INVITE_QQ:
                        //QQ
                        share2QQF();
                        break;
                    case POSITION_INVITE_WEIBO:
                        //新浪微博
                        share2WB();
                        break;
                    case POSITION_INVITE_FACEBOOK:
                        share2FB();
                        //Facebook
                        break;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        image2WX = loadRawDataFromURL(response.getImageurl());
                    }
                }).start();
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                    }
                });
            }
        });
    }

    //分享到微博
    public void share2WB() {
        mWeiboShareApi = WeiboShareSDK.createWeiboAPI(this, Constants.Weibo.APP_KEY);
        mWeiboShareApi.registerApp();
        AuthInfo authInfo = new AuthInfo(this, Constants.Weibo.APP_KEY, Constants.Weibo.REDIRECT_URL, Constants.Weibo.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        WeiboMessage weiboMessage = new WeiboMessage();
        TextObject textObject = new TextObject();
        textObject.text = response.getTitile() + response.getInviteurl();
        weiboMessage.mediaObject = textObject;
        request.message = weiboMessage;
        mWeiboShareApi.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onWeiboException(WeiboException arg0) {
            }

            @Override
            public void onComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                //   Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mViewUtils.toast(getString(R.string.share_cancel));
                    }
                });
            }
        });
    }

    public void share2WBImage() {

    }

    public static String formatString(String url) {
        return url;
    }

    public static byte[] loadRawDataFromURL(String u) {
        try {
            URL url = new URL(formatString(u));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final int BUFFER_SIZE = 2048;
            final int EOF = -1;

            int c;
            byte[] buf = new byte[BUFFER_SIZE];

            while (true) {
                c = bis.read(buf);
                if (c == EOF)
                    break;

                baos.write(buf, 0, c);
            }

            conn.disconnect();
            is.close();

            byte[] data = baos.toByteArray();
            baos.flush();

            return data;
        } catch (Exception e) {
            return null;
        }

    }

    public void creditsInviteInfoRequest() {
        showLoading();
        CsUser.CreditsInviteInfoRequest.Builder builder = CsUser.CreditsInviteInfoRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.CreditsInviteInfoResponse>() {

            @Override
            public void onSuccess(final CsUser.CreditsInviteInfoResponse response) {
                LogUtils.d(response.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        titleTv.setText(response.getInviteTitle());
                        infoTv.setText(response.getInviteInfo());
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                    }
                });
            }
        });
    }
}
