package com.fuexpress.kr.ui.activity.package_detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.ShareFriendsBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.adapter.SendFuItemAdapter;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.CustomToast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fksproto.CsMy;
import fksproto.CsParcel;

public class SendFuActivity extends BaseActivity {
    public static String DATA = "data";
    public static String PARCEL_ID = "parcel_id";

    @BindView(R.id.title_iv_left)
    ImageView titleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView titleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView titleTvCenter;
    @BindView(R.id.title_tv_right)
    TextView titleTvRight;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.title_iv_right)
    ImageView titleIvRight;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.lv_item_body)
    CustomListView mLvItemBody;
    @BindView(R.id.et_count)
    EditText mEtCount;
    @BindView(R.id.et_message)
    EditText mEtMessage;
    @BindView(R.id.tv_item_total)
    TextView mTvItemTotal;
    @BindView(R.id.tv_bottom)
    TextView mTvBottom;
    private long mParcelID;
    private int mItemTotal;
    private ImageView mImageView;
    private List<CsParcel.ParcelItemList> mParcelItems;
    private SendMethodListFragment mSendMethodListFragment;

    @Override
    public View setInitView() {
        View inflate = View.inflate(this, R.layout.activity_append_item, null);
        FrameLayout viewById = (FrameLayout) inflate.findViewById(R.id.fl_container);
        mImageView = new ImageView(this);
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(getResources().getColor(R.color.default_bg));
        scrollView.addView(View.inflate(this, R.layout.activity_send_fu, null));
        viewById.addView(scrollView);
        return inflate;
    }

    @Override
    public void initView() {
        titleTvCenter.setText(R.string.string_give_fu_title);
        titleIvLeft.setVisibility(View.VISIBLE);
        titleTvLeft.setText(getString(R.string.String_goto_send));
        titleTvLeft.setVisibility(View.VISIBLE);
        titleTvRight.setText("");
        mTvBottom.setText(getString(R.string.string_baby_counts, "0"));
        initContent();
        initEvent();
    }


    private void getFuInfo() {
        CsMy.InitSendBagRequest.Builder builder = CsMy.InitSendBagRequest.newBuilder().setUserHead(AccountManager.getInstance().getBaseUserRequest())
                .setLocaleCode(AccountManager.getInstance().getLocaleCode())
                .setParcelId((int) mParcelID);

        NetEngine.postRequest(builder, new INetEngineListener<CsMy.InitSendBagResponse>() {

            @Override
            public void onSuccess(final CsMy.InitSendBagResponse response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mItemTotal = 0;
                        List<CsMy.salesOrderItemOfBag> itemsList = response.getItemsList();
                        for (CsMy.salesOrderItemOfBag itemOfBag : itemsList) {
                            String imagePath = itemOfBag.getImagePath();
                            if (imagePath.indexOf("!") > 0)
                                imagePath = imagePath.substring(0, imagePath.indexOf("!"));
                            CsParcel.ParcelItemList.Builder parcelItem = CsParcel.ParcelItemList.newBuilder()
                                    .setName(itemOfBag.getCaption())
                                    .setImageurl(imagePath)
                                    .setQtyPack(itemOfBag.getQty()).setQty(itemOfBag.getQty());
                            mParcelItems.add(parcelItem.build());
                            mItemTotal += itemOfBag.getQty();
                        }
                        mTvItemTotal.setText(getString(R.string.string_parcel_item_total) + mItemTotal);
                        SendFuItemAdapter adapter = new SendFuItemAdapter(SendFuActivity.this, mParcelItems);
                        mLvItemBody.setAdapter(adapter);

                        if (response.getBagStatus() == 1) {
                            int count = 1;
                            if (response.getBagQty() > count) count = response.getBagQty();
                            mEtCount.setText(count + "");
                            mEtMessage.setText(response.getMessage());
                            mEtCount.setEnabled(false);
                            mEtMessage.setEnabled(false);
                        }
                        if (response.getIsGrab() == 1) {
                            mTvBottom.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.VISIBLE);
                        } else {
                            mTvBottom.setVisibility(View.GONE);
                            btnSave.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(SendFuActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }


    private void initEvent() {
        mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer value = Integer.valueOf(TextUtils.isEmpty(s.toString()) ? "0" : s.toString());
                if (s.toString().equals("0")) {
                    mEtCount.setText("1");
                    mEtCount.setSelection(mEtCount.getText().length());
                } else if (mItemTotal != 0 && value > mItemTotal) {
                    mEtCount.setText(mItemTotal + "");
                    mEtCount.setSelection(mEtCount.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Integer value = getValue(s.toString());
                int number = mItemTotal / value;
                if (mItemTotal % value > 0) {
                    mTvBottom.setText(getString(R.string.string_baby_counts, number + "-" + (number + 1)));
                } else {
                    mTvBottom.setText(getString(R.string.string_baby_counts, number + ""));
                }
            }
        });

        mEtCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String s = mEtCount.getText().toString();
                    Integer value = getValue(s);
                    mEtCount.setText(value + "");
                }
            }
        });

    }

    @NonNull
    private Integer getValue(String s) {
        Integer value = Integer.valueOf(TextUtils.isEmpty(s) ? "0" : s);
        if (value > mItemTotal) value = mItemTotal;
        if (value <= 0) value = 1;
        return value;
    }

    private void initContent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mParcelItems = new ArrayList<>();
        CsParcel.Parcel parcel = (CsParcel.Parcel) extras.get(PARCEL_ID);
        if (parcel != null)
            mParcelID = parcel.getParcelId();
        getFuInfo();
    }


    private void sendShare() {
        String str = mEtCount.getText().toString();
        if (TextUtils.isEmpty(str)) {
            CustomToast.makeText(this, getString(R.string.please_input_fu_count), Toast.LENGTH_SHORT).show();
            return;
        }
        String string = mEtMessage.getText().toString();
        if (TextUtils.isEmpty(string)) string = getString(R.string.string_send_fu_message);
        mSendMethodListFragment = SendMethodListFragment.newInstance((int) mParcelID, getValue(str), string);
        mSendMethodListFragment.showRemixer(getSupportFragmentManager(), "");
    }


    @OnClick({R.id.title_iv_left, R.id.title_tv_left, R.id.title_tv_right, R.id.btn_save})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                onBackPressed();
                break;
            case R.id.btn_save:
                sendShare();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mSendMethodListFragment != null && !mSendMethodListFragment.isHidden()) {
            initContent();
            mSendMethodListFragment.dismiss();
        }
    }

    public void startShare(final CsMy.DoShareBagResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShareFriendsBean shareFriendsBean = new ShareFriendsBean();
                shareFriendsBean.setTitle(response.getMsg());
                shareFriendsBean.setInfo(response.getMsg());
                shareFriendsBean.setUrl(response.getBagUrl());
                shareFriendsBean.setBitmap(response.getIconUrl());
//                SendMethodListFragment.newInstance(shareFriendsBean).showRemixer(getSupportFragmentManager(), "");
            }
        });

        /*ImageLoader.getInstance().displayImage(response.getIconUrl(), mImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShareFriendsBean shareFriendsBean = new ShareFriendsBean();
                        shareFriendsBean.setTitle(response.getMsg());
                        shareFriendsBean.setInfo(response.getMsg());
                        shareFriendsBean.setUrl(response.getBagUrl());
                        shareFriendsBean.setBitmap(null);
                        SendMethodListFragment.newInstance(shareFriendsBean).showRemixer(getSupportFragmentManager(), "");
                    }
                });
            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShareFriendsBean shareFriendsBean = new ShareFriendsBean();
                        shareFriendsBean.setTitle(response.getMsg());
                        shareFriendsBean.setInfo(response.getMsg());
                        shareFriendsBean.setUrl(response.getBagUrl());
//                        Bitmap bitmap1 = resizeBitmap(bitmap, 300, 300);
                        shareFriendsBean.setBitmap(bit2ByteArray(bitmap));
                        bitmap.recycle();
                        SendMethodListFragment.newInstance(shareFriendsBean).showRemixer(getSupportFragmentManager(), "");
                    }
                });

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });*/
    }

    public Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    private byte[] bit2ByteArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
        bmp.recycle();//自由选择是否进行回收
        byte[] result = output.toByteArray();//转换成功了
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
