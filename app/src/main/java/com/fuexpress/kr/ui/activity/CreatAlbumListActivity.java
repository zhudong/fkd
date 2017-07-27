package com.fuexpress.kr.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.AlbumManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import de.greenrobot.event.EventBus;
import fksproto.CsAlbum;


public class CreatAlbumListActivity extends Activity implements View.OnClickListener {

    private static final int CREATE_ALBUM_SUCCESS = 1;
    private static final int FAIL_TO_CREAT_ALBUM = 2;
    private ImageView mIv_close_for_creat_albums_dialog;
    private EditText mEd_for_creat_album;
    private Button mBtn_for_creat_album_save;
    private Button mBtn_for_creat_album_cancle;
    private TextView mTv_for_creat_album_note_entername;
    String mAlbumName;
    private RelativeLayout mRl_for_fragment_home_02_creatalbum;
    private TextView mTv_for_fragment_home_02_creatalbum;
    private ProgressBar mPb_for_fragment_home_02_creatalbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        // Note that flag changes must happen *before* the content view is set.
        View view = View.inflate(this, R.layout.activity_creat_album_list, null);

        mIv_close_for_creat_albums_dialog = (ImageView) view.findViewById(R.id.iv_close_for_creat_albums_dialog);

        mIv_close_for_creat_albums_dialog.setOnClickListener(this);

        mEd_for_creat_album = (EditText) view.findViewById(R.id.ed_for_creat_album);

        mEd_for_creat_album.addTextChangedListener(mTextWatcher);

        mBtn_for_creat_album_save = (Button) view.findViewById(R.id.btn_for_creat_album_save);

        mBtn_for_creat_album_save.setOnClickListener(this);

        mBtn_for_creat_album_cancle = (Button) view.findViewById(R.id.btn_for_creat_album_cancle);

        mBtn_for_creat_album_cancle.setOnClickListener(this);

        mTv_for_creat_album_note_entername = (TextView) view.findViewById(R.id.tv_for_creat_album_note_entername);

        mRl_for_fragment_home_02_creatalbum = (RelativeLayout) view.findViewById(R.id.rl_for_fragment_home_02_creatalbum);

        mTv_for_fragment_home_02_creatalbum = (TextView) view.findViewById(R.id.tv_for_fragment_home_02_creatalbum);

        mPb_for_fragment_home_02_creatalbum = (ProgressBar) view.findViewById(R.id.pb_for_fragment_home_02_creatalbum);

        setContentView(view);

        //Showkeyboard(mEd_for_creat_album);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finishAndNull();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_for_creat_albums_dialog:
                finishAndNull();
                break;
            case R.id.btn_for_creat_album_cancle:
                finishAndNull();
                break;
            case R.id.btn_for_creat_album_save:
                checkEditext();
                break;
        }
    }

    /**
     * 检查看用户的输入情况
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkEditext() {
        //拿到ed中的文字:
        String albumName = mEd_for_creat_album.getText().toString().trim();
        if (TextUtils.isEmpty(albumName)) {
            //说明是空的,我们改变颜色且提醒:
            mEd_for_creat_album.setBackground(getResources().getDrawable(R.drawable.shape_bg_for_home_head_red));
            int px = UIUtils.dip2px(5);
            mEd_for_creat_album.setPadding(px, px, px, px);
            mTv_for_creat_album_note_entername.setVisibility(View.VISIBLE);
        } else {
            //说明不是空的,我们就进行网络请求增加图集,并且把返回的图集id传过去给Fragment02:
            mAlbumName = albumName;
            sendCreatAlbumRequest(albumName);
        }
    }

    /**
     * 这里是发送请求去创建图集的方法:
     *
     * @param albumName 图集名
     */
    private void sendCreatAlbumRequest(String albumName) {

        mRl_for_fragment_home_02_creatalbum.setVisibility(View.VISIBLE);

        CsAlbum.CreateAlbumRequest.Builder builder = CsAlbum.CreateAlbumRequest.newBuilder();

        builder.setCategory(1);

        builder.setTitle(albumName);

        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());

        NetEngine.postRequest(builder, new INetEngineListener<CsAlbum.CreateAlbumResponse>() {

            @Override
            public void onSuccess(CsAlbum.CreateAlbumResponse response) {
                //LogUtils.e("这是创建图集成功的请求应答!" + response.toString());
                long albumId = response.getAlbumId();
                //AlbumManager.getInstance().getSimpleAlbumList();
                Message message = Message.obtain();
                message.what = CREATE_ALBUM_SUCCESS;
                message.obj = albumId;
                mHandler.sendMessage(message);
                //AccountManager.getInstance().getUserCount();
                EventBus.getDefault().post(new BusEvent(BusEvent.USER_COUNT_UPDATE, null));
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                //LogUtils.e("这是创建图集失败了!" + errMsg);
                Message obtain = Message.obtain();
                obtain.what = FAIL_TO_CREAT_ALBUM;
                mHandler.sendMessage(obtain);
            }
        });

    }

    /**
     * 关闭界面且没返回值的方法:
     */
    public void finishAndNull() {
        Intent intent = new Intent();
        //设置返回值
        intent.putExtra("isCreat", false);
        setResult(1, intent);
        finish();
        overridePendingTransition(R.anim.activity_translate_out_alpha, R.anim.activity_translate_in_alpha);
    }

    /**
     * Handler;
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case CREATE_ALBUM_SUCCESS:
                    //这是网络请求添加图集成功发送过来的Handler:
                    final long albumId = (long) msg.obj;
                    mPb_for_fragment_home_02_creatalbum.setVisibility(View.GONE);
                    mTv_for_fragment_home_02_creatalbum.setText(getString(R.string.String_create_album_success));
                    mRl_for_fragment_home_02_creatalbum.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            creatAlbumScuess(albumId);
                        }
                    }, 1000);
                    break;
                case FAIL_TO_CREAT_ALBUM:
                    mPb_for_fragment_home_02_creatalbum.setVisibility(View.GONE);
                    mTv_for_fragment_home_02_creatalbum.setText(getString(R.string.String_create_album_failure));
                    mRl_for_fragment_home_02_creatalbum.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRl_for_fragment_home_02_creatalbum.setVisibility(View.GONE);
                        }
                    }, 1000);
                    break;
            }
        }
    };


    /**
     * 这是请求创建图集成功的方法:
     *
     * @param albumId
     */
    public void creatAlbumScuess(long albumId) {
        /*Intent intent = new Intent();
        //设置返回值
        intent.putExtra("isCreat", true);
        intent.putExtra("albumId", albumId);
        intent.putExtra("albumName", mAlbumName);
        setResult(MeActivity.CREATE_ALBUM_SUCCESS, intent);*/
        AlbumManager.getInstance().addSimpleAlbumBean(albumId, mAlbumName);
        Hidekeyboard(mEd_for_creat_album);
        EventBus.getDefault().post(new BusEvent(BusEvent.CREAT_ALBUM_SUCCESS, null));
        finish();
        overridePendingTransition(R.anim.activity_translate_out_alpha, R.anim.activity_translate_in_alpha);
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 1) {
                mEd_for_creat_album.setBackground(getResources().getDrawable(R.drawable.shape_bg_for_home_head));
                int px = UIUtils.dip2px(5);
                mEd_for_creat_album.setPadding(px, px, px, px);
                mTv_for_creat_album_note_entername.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    /**
     * @param et 隐藏软键盘
     * @nama yl
     */
    public void Hidekeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
        if (isOpen) {
            ((InputMethodManager) et.getContext().getSystemService(
                    INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    /**
     * @param et 显示软键盘
     * @nama yl
     */
    public void Showkeyboard(EditText et) {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .showSoftInput(et, 0);
    }

}