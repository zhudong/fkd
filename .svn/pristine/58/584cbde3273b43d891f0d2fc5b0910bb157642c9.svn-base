package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

import fksproto.CsBase;

/**
 * Created by Longer on 2016/10/27.
 * 自定义的横向输入布局
 * 附带显示仓库的布局
 */
public class InputBoxView extends LinearLayout {
    public static final int WARE_HOUSE_TYPE = 0;
    public static final int BELOW_SHOW_TYPE = 1;
    public static final int NORMAL_TYPE = 3;
    public static final int PRICE_TYPE = 4;
    public int mType = NORMAL_TYPE;

    private EditText mEd_in_ip;
    private TextView mTv_in_ip_right;
    private TextView mTv_in_ip_below;
    private ImageView mIv_in_ip_right;
    private TextView mTv_in_ip;
    private LinearLayout mLl_ware_house_container;
    private TextView mTv_ip_address;
    private TextView mTv_ip_receiver;
    private TextView mTv_ip_phone;
    private TextView mTv_ip_post_code;
    private TextView mTv_ip_center;
    private LinearLayout mIp_ll_container;

    public InputBoxView(Context context) {
        this(context, null);
    }

    public InputBoxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.inputbox_layout, this);
        mTv_in_ip = (TextView) findViewById(R.id.tv_in_ip);
        TextView star_in_ip = (TextView) findViewById(R.id.star_in_ip);
        mTv_in_ip_below = (TextView) findViewById(R.id.tv_in_ip_below);
        mEd_in_ip = (EditText) findViewById(R.id.ed_in_ip);
        mTv_in_ip_right = (TextView) findViewById(R.id.tv_in_ip_right);
        mIv_in_ip_right = (ImageView) findViewById(R.id.iv_in_ip_right);
        mTv_ip_center = (TextView) findViewById(R.id.tv_ip_center);
        mIp_ll_container = (LinearLayout) findViewById(R.id.ip_ll_container);
        findWareHouseView();

        //取出定义好在arrt文件中的自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ipboxtext, defStyleAttr, 0);
        String ipText = typedArray.getString(R.styleable.ipboxtext_ipText);
        String hintText = typedArray.getString(R.styleable.ipboxtext_hintText);
        boolean isShow = typedArray.getBoolean(R.styleable.ipboxtext_isShowStar, false);
        star_in_ip.setVisibility(isShow ? VISIBLE : GONE);
        boolean showRight = typedArray.getBoolean(R.styleable.ipboxtext_showRight, false);
        mTv_in_ip_right.setVisibility(showRight ? VISIBLE : GONE);
        boolean showBelow = typedArray.getBoolean(R.styleable.ipboxtext_showBelow, false);
        mTv_in_ip_below.setVisibility(showBelow ? VISIBLE : GONE);
        boolean showRightImageView = typedArray.getBoolean(R.styleable.ipboxtext_showRightImageView, false);
        mIv_in_ip_right.setVisibility(showRightImageView ? VISIBLE : GONE);
        if (showRight)
            mTv_in_ip_right.setText(typedArray.getString(R.styleable.ipboxtext_rightText));
        if (showBelow)
            mTv_in_ip_below.setText(typedArray.getString(R.styleable.ipboxtext_belowText));
        typedArray.recycle();//取出完毕之后要回收资源

        mTv_in_ip.setText(ipText);
        mEd_in_ip.setHint(hintText);

    }

    private void findWareHouseView() {
        mLl_ware_house_container = (LinearLayout) findViewById(R.id.ll_ware_house_container);
        mTv_ip_address = (TextView) findViewById(R.id.tv_ip_address);
        mTv_ip_receiver = (TextView) findViewById(R.id.tv_ip_receiver);
        mTv_ip_phone = (TextView) findViewById(R.id.tv_ip_phone);
        mTv_ip_post_code = (TextView) findViewById(R.id.tv_ip_post_code);
    }

    public boolean edIsEmpty() {
        return TextUtils.isEmpty(mEd_in_ip.getText().toString().trim());
    }


    public String getEdStringText() {
        return mEd_in_ip.getText().toString().trim();
    }

    /**
     * 设置其输入方式的方法，具体类型参见EditInfo
     *
     * @param inputType EditText的输入类型
     */
    public void setEditTextInputType(int inputType) {
        mEd_in_ip.setInputType(inputType);
    }

    /**
     * 获得输入框的EditText控件
     *
     * @return 输入框的EditText控件
     */
    public EditText getEditTextView() {
        return mEd_in_ip;
    }

    /**
     * 获得中间的TextView控件
     *
     * @return 中间的TextView控件
     */
    public TextView getCenterTextView() {
        return mTv_ip_center;
    }

    /**
     * 获得右边的TextView方法
     *
     * @return 右边的TextView
     */
    public TextView getRightTextView() {
        return mTv_in_ip_right;
    }

    /**
     * 获得下边TextView的方法
     *
     * @return 下边TextView
     */
    public TextView getBelowTextView() {
        return mTv_in_ip_below;
    }

    /**
     * 获得右边ImageView的方法
     * 注意这里默认是下箭头图片,如要修改请自己指定
     *
     * @return 右边的ImageView 控件
     */
    public ImageView getIvRight() {
        return mIv_in_ip_right;
    }

    /**
     * 设置输入框的文字
     *
     * @param ipText     输入框左侧文字
     * @param hintString 输入框的底纹文字
     */
    public void setIpTextAndHint(String ipText, String hintString) {
        mTv_in_ip.setText(ipText);
        mEd_in_ip.setHint(hintString);
    }


    /**
     * 设置输入框的右边文字
     *
     * @param textRight 右边的文字
     */
    public void setIpTextRight(String textRight) {
        //mTv_in_ip_right.setVisibility(VISIBLE);
        mTv_in_ip_right.setText(textRight);
    }


    /**
     * 设置位于左侧底部文字的方法
     *
     * @param textBelow 左侧底部文字
     */
    public void setIpTextBelow(String textBelow) {
        mTv_in_ip_below.setVisibility(VISIBLE);
        mTv_in_ip_below.setText(textBelow);
    }

    /**
     * 显示仓库视图
     *
     * @param wareHouseContainerShow 仓库的bean类
     */
    public void setWareHouseContainerShow(@NonNull CsBase.Warehouse wareHouseContainerShow) {
        mLl_ware_house_container.setVisibility(VISIBLE);
        mTv_ip_address.setText(wareHouseContainerShow.getFulladdress());
        mTv_ip_receiver.setText(wareHouseContainerShow.getReceiver());
        mTv_ip_post_code.setText(wareHouseContainerShow.getPostcode());
        mTv_ip_phone.setText(wareHouseContainerShow.getPhone());
    }

    /**
     * 切换IpPutBox的类型
     *
     * @param type 类型,详见本类中的静态方法
     */
    public void changeIpType(int type) {
        mType = type;
        switch (type) {
            case WARE_HOUSE_TYPE:
                mEd_in_ip.setVisibility(GONE);
                mLl_ware_house_container.setVisibility(VISIBLE);
                mTv_ip_center.setVisibility(VISIBLE);
                mIv_in_ip_right.setVisibility(VISIBLE);
                break;
            case BELOW_SHOW_TYPE:
                mTv_in_ip_below.setVisibility(VISIBLE);
                break;
            case PRICE_TYPE:
                mTv_in_ip_right.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 返回当前的InputBox的类型
     *
     * @return 返回当前的类型
     */
    public int getIpBoxType() {
        return mType;
    }


    /**
     * 设置中间的TextView的文字
     *
     * @param centertext 需要设置的文字
     */
    public void setCenterTextView(String centertext) {
        mTv_ip_center.setText(centertext);
    }

    /**
     * 返回整个InputBoxView的容器View对象
     */
    public LinearLayout getContainerView() {
        return mIp_ll_container;
    }

    /**
     * 设置左边的TextView的方法
     *
     * @param leftText 左边TextView的String
     */
    public void setIpTextLeft(String leftText) {
        mTv_in_ip.setText(leftText);
    }


}
