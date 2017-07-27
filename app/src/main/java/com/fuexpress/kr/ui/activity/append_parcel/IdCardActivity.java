package com.fuexpress.kr.ui.activity.append_parcel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.IDinfoBean;
import com.fuexpress.kr.model.ItemUploadManager;
import com.fuexpress.kr.ui.activity.Down2UpDialogActivity;
import com.fuexpress.kr.ui.activity.append_item.JsonSerializer;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdCardActivity extends BaseActivity {

    public static final String ID_CARD_FRONT = "id_card_front";
    public static final String ID_CARD_BACK = "id_card_back";
    public static final String ID_CARD_NUMBER = "id_card_number";
    public static final String ID_CARD_BEAN = "id_card_bean";

    @BindView(R.id.edt_input_id_info)
    EditText mEdtInputIdInfo;
    @BindView(R.id.btn_save_id_card)
    Button mBtnSaveIdCard;
    @BindView(R.id.img_uping_card_front)
    ImageView mImgUpingCardFront;
    @BindView(R.id.view_card_front_cover)
    View mViewCardFrontCover;
    @BindView(R.id.tv_card_front)
    TextView mTvCardFront;
    @BindView(R.id.img_uping_card_back)
    ImageView mImgUpingCardBack;
    @BindView(R.id.view_card_back_cover)
    View mViewCardBackCover;
    @BindView(R.id.tv_card_back)
    TextView mTvCardBack;
    @BindView(R.id.fl_front)
    FrameLayout mFlFront;
    @BindView(R.id.fl_back)
    FrameLayout mFlBack;
    private View mRootView;
    @BindView(R.id.title_iv_left)
    ImageView titleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView titleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView titleTvCenter;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.title_iv_right)
    ImageView titleIvRight;
    private ItemUploadManager mFrontuploadManager;
    private ItemUploadManager mBackuploadManager;
    private DisplayImageOptions mDisplayOptions;
    private boolean mForntUpEnd;
    private boolean mBackUpEnd;
    private String mId_card_number;
    private String mFrontUrl;
    private String mBackUrl;
    private IDinfoBean mInfo;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_id_card, null);
        return mRootView;
    }

    @Override
    public void initView() {
        titleTvCenter.setText(R.string.parcel_id_string);
        titleIvLeft.setVisibility(View.VISIBLE);
        titleTvLeft.setText(" ");
        titleTvRight.setText(R.string.String_parcel_delete);
        mDisplayOptions = ImageLoaderHelper.getInstance(this).getDisplayOptions();
        JsonSerializer jsonSerializer = new JsonSerializer();
        mInfo = jsonSerializer.deserializeIDinfo(getIntent().getStringExtra(ID_CARD_BEAN));
        initEvent();
        retore();
    }

    private void retore() {

        if (!TextUtils.isEmpty(mInfo.getIdNumber())) {
            mId_card_number = mInfo.getIdNumber();
            mBackUrl = mInfo.getUrlBack();
            mFrontUrl = mInfo.getUrlFront();
        } else {
            mId_card_number = mInfo.getServerIDNumber();
            mBackUrl = mInfo.getServerUrlBack();
            mFrontUrl = mInfo.getServerUrlFront();
        }
        mBackUpEnd = !"".equals(mBackUrl);
        mForntUpEnd = !"".equals(mFrontUrl);
        showIdFrontImg(mFrontUrl);
        showIdBackImg(mBackUrl);
        mEdtInputIdInfo.setText(mId_card_number);
        check();
    }


    @OnClick({R.id.rl_append_idcard_front, R.id.rl_append_idcard_back, R.id.btn_save_id_card, R.id.title_iv_left, R.id.title_tv_left})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_append_idcard_front:
                intent = new Intent(this, Down2UpDialogActivity.class);
                intent.putExtra(Down2UpDialogActivity.GO_THIS_ACTIVITY_INTENT_KEY, Down2UpDialogActivity.CHOOSE_ID_CARD_TYPE);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_append_idcard_back:
                intent = new Intent(this, Down2UpDialogActivity.class);
                intent.putExtra(Down2UpDialogActivity.GO_THIS_ACTIVITY_INTENT_KEY, Down2UpDialogActivity.CHOOSE_ID_CARD_TYPE);
                startActivityForResult(intent, 2);
                break;
            case R.id.btn_save_id_card:
                save();
                break;
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
        }
    }

    private void save() {
        mId_card_number = mEdtInputIdInfo.getText().toString();
        boolean inputEmpty = "".equals(mId_card_number);
        if (inputEmpty) {
            Toast.makeText(this, "请输入身份证号码！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(ID_CARD_FRONT, mFrontUrl);
        intent.putExtra(ID_CARD_BACK, mBackUrl);
        intent.putExtra(ID_CARD_NUMBER, mId_card_number);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 100) {
            final String uri = data.getStringExtra("image");
            final String img = Uri.parse(uri).getPath();
            mTvCardFront.setVisibility(View.GONE);
            mFlFront.setBackground(null);
            ImageLoader.getInstance().displayImage(uri, mImgUpingCardFront, mDisplayOptions);
            mViewCardFrontCover.setVisibility(View.VISIBLE);
            mFrontuploadManager = new ItemUploadManager("care_front");
            mFrontuploadManager.addTask(img);
            mFrontuploadManager.getAllComplete(new ItemUploadManager.UpLoadeCompleteListener() {
                @Override
                public void complete(float prgress, final ArrayMap map) {
                    if (prgress == 100) {
                        UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mFrontUrl = (String) map.get(img);
                                mViewCardFrontCover.setVisibility(View.GONE);
                                Toast.makeText(IdCardActivity.this, R.string.card_front_uplode_success, Toast.LENGTH_SHORT).show();
                                mForntUpEnd = true;
                                check();
                            }
                        }, 500);
                    }
                }

                @Override
                public void error(List<String> urls) {

                }
            });
        }
        if (requestCode == 2 && resultCode == 100) {
            final String uri = data.getStringExtra("image");
            final String img = Uri.parse(uri).getPath();
            mTvCardBack.setVisibility(View.GONE);
            mFlBack.setBackground(null);
            ImageLoader.getInstance().displayImage(uri, mImgUpingCardBack, mDisplayOptions);
            mViewCardBackCover.setVisibility(View.VISIBLE);
            mBackuploadManager = new ItemUploadManager("care_front");
            mBackuploadManager.addTask(img);
            mBackuploadManager.getAllComplete(new ItemUploadManager.UpLoadeCompleteListener() {
                @Override
                public void complete(final float prgress, final ArrayMap map) {

                    UIUtils.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (prgress == 100) {
                                mBackUrl = (String) map.get(img);
                                mViewCardBackCover.setVisibility(View.GONE);
                                Toast.makeText(IdCardActivity.this, R.string.card_back_uplode_success, Toast.LENGTH_SHORT).show();
                                mBackUpEnd = true;
                                check();
                            }
                        }
                    }, 500);
                }

                @Override
                public void error(List<String> urls) {

                }
            });
        }
    }


    void showIdFrontImg(String url) {
        if (TextUtils.isEmpty(url)) return;
        mTvCardFront.setVisibility(View.GONE);
        mFlFront.setBackground(null);
        ImageLoader.getInstance().displayImage(url, mImgUpingCardFront, mDisplayOptions);
//        .setVisibility(View.VISIBLE);
        mViewCardFrontCover.setVisibility(View.GONE);
    }

    void showIdBackImg(String url) {
        if (TextUtils.isEmpty(url)) return;
        mTvCardBack.setVisibility(View.GONE);
        mFlBack.setBackground(null);
        ImageLoader.getInstance().displayImage(url, mImgUpingCardBack, mDisplayOptions);
//        mViewCardFBackCover.setVisibility(View.VISIBLE);
        mViewCardBackCover.setVisibility(View.GONE);
    }

/*
    private void check() {
        if (mBackUpEnd && mForntUpEnd) {
            mBtnSaveIdCard.setEnabled(true);
        }
    }*/

    boolean notNeedIdImg = true;

    private void check() {
        String s = mEdtInputIdInfo.getText().toString();
        boolean trueIDnum = !TextUtils.isEmpty(s) && s.length() > 5;
        mBtnSaveIdCard.setEnabled((mBackUpEnd | notNeedIdImg) && (mForntUpEnd | notNeedIdImg) && trueIDnum);
    }


    private void initEvent() {
        mEdtInputIdInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() > 5) {
                    check();
                }
            }
        });
    }

}
