package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andy on 2017/1/17.
 */
public class MaterialItemLayout extends RelativeLayout {
    @BindView(R.id.img_arrow)
    ImageView mImgArrow;
    @BindView(R.id.tv_material_name)
    TextView mTvMaterialName;

    public MaterialItemLayout(Context context) {
        this(context, null);
    }

    public MaterialItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_material_item, this);
        ButterKnife.bind(this);
    }

    public void setText(String text) {
        mTvMaterialName.setText(text);
    }

    public void setSelect(boolean select) {
        if (select) {
            mImgArrow.setVisibility(VISIBLE);
        } else {
            mImgArrow.setVisibility(INVISIBLE);
        }

    }
}
