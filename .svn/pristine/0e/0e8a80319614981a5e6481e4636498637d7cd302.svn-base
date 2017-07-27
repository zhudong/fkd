package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

/**
 * Created by Longer on 2017/5/24.
 */
public class SwitcherText extends LinearLayout implements View.OnClickListener {

    private TextView mProduct;
    private TextView mAlbumColl;
    private OnSwitchListener mListener;
    private View mAlbumLine;
    private View mItemLine;

    public interface OnSwitchListener {
        public void switchLeft(SwitcherText view);

        public void switchRight(SwitcherText view);
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        this.mListener = listener;
    }

    public SwitcherText(Context context) {
        this(context, null);
    }

    public SwitcherText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setText(String left, String right) {
        mAlbumColl.setText(right);
        mProduct.setText(left);
    }

    private void initView(Context context) {
        LinearLayout rootView = (LinearLayout) View.inflate(context, R.layout.view_switcher_layout, this);
        mProduct = (TextView) rootView.findViewById(R.id.tv_single_product);
        mAlbumColl = (TextView) rootView.findViewById(R.id.tv_album_coll);

        mAlbumLine = rootView.findViewById(R.id.view_line_album);
        mItemLine = rootView.findViewById(R.id.view_line_item);
        mProduct.setOnClickListener(this);
        mAlbumColl.setOnClickListener(this);
        mProduct.setEnabled(false);
        mAlbumColl.setEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_single_product:
                mProduct.setEnabled(false);
                mAlbumColl.setEnabled(true);
                mAlbumLine.setVisibility(View.INVISIBLE);
                mItemLine.setVisibility(View.VISIBLE);
                if (mListener != null) {
                    mListener.switchLeft(this);
                }
                break;
            case R.id.tv_album_coll:
                mProduct.setEnabled(true);
                mAlbumColl.setEnabled(false);
                mAlbumLine.setVisibility(View.VISIBLE);
                mItemLine.setVisibility(View.INVISIBLE);
                if (mListener != null) {
                    mListener.switchRight(this);
                }
                break;
        }
    }

    public void setLeftSelected() {
        mProduct.setEnabled(false);
        mAlbumColl.setEnabled(true);
        mAlbumLine.setVisibility(View.INVISIBLE);
        mItemLine.setVisibility(View.VISIBLE);
    }

    /*图集在右*/
    public void setRightSelected() {
        mProduct.setEnabled(true);
        mAlbumColl.setEnabled(false);
        mAlbumLine.setVisibility(View.VISIBLE);
        mItemLine.setVisibility(View.INVISIBLE);
    }


    public void setRightTextcolorStyle(int colorID) {
        mProduct.setTextColor(getResources().getColor(colorID));
    }

    public void setLeftTextcolorStyle(int colorID) {
        mAlbumColl.setTextColor(getResources().getColor(colorID));
    }
}
