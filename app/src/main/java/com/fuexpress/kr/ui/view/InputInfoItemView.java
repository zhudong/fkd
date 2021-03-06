package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andy on 2017/1/17.
 */
public class InputInfoItemView extends RelativeLayout {

    @BindView(R.id.tv_input_declare)
    TextView mTvInputDeclare;
    @BindView(R.id.edt_input)
    EditText mEdtInput;
    @BindView(R.id.tv_input_type)
    TextView mTvInputType;
    @BindView(R.id.img_arrow)
    ImageView mImgArrow;
    @BindView(R.id.img_start)
    ImageView mImgStart;
    private String mHint;
    private String mDeclare;
    private boolean mShowArrow;
    private boolean mShowText;
    private boolean mRequired;

    public InputInfoItemView(Context context) {
        this(context, null);
    }

    public InputInfoItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputInfoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.InputItemView, defStyleAttr, 0);

        mDeclare = a.getString(R.styleable.InputItemView_inputDdeclare);
        mHint = a.getString(R.styleable.InputItemView_inputHint);
        mShowArrow = a.getBoolean(R.styleable.InputItemView_inputShowArrow, false);
        mShowText = a.getBoolean(R.styleable.InputItemView_inputShowText, false);
        mRequired = a.getBoolean(R.styleable.InputItemView_inputRequired, false);
        a.recycle();

        initView();
    }

    public void setOnclickListener(View.OnClickListener listener) {
        mEdtInput.setFocusable(false);
        mEdtInput.setClickable(true);
        mEdtInput.setOnClickListener(listener);
    }


    private void initView() {
        inflate(getContext(), R.layout.view_input_info_item, this);
        ButterKnife.bind(this);
        mTvInputDeclare.setText(mDeclare);
        mEdtInput.setHint(mHint);
        if (mShowArrow) {
            mImgArrow.setVisibility(VISIBLE);
        } else {
            mImgArrow.setVisibility(GONE);
        }
        if (mShowText) {
            mTvInputType.setVisibility(VISIBLE);
        } else {
            mTvInputType.setVisibility(GONE);
        }
        if (mRequired) {
            mImgStart.setVisibility(VISIBLE);
        } else {
            mImgStart.setVisibility(GONE);
        }
    }

    public String getValue() {
        return mEdtInput.getText().toString();
    }

    public void setText(String text) {
        mEdtInput.setText(text);
    }

}
