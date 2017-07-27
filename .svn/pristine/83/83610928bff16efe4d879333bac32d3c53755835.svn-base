package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import java.util.List;

import fksproto.CsBase;


/**
 * Created by andy on 2017/3/20.
 */
public class ProductExtendView extends LinearLayout {

    private TextView mCategory;
    private FlowLayout mContainer;
    private int mMargin;
    private int mCurrentOption;

    public ProductExtendView(Context context) {
        super(context);
        initView();
    }


    public ProductExtendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ProductExtendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        View.inflate(getContext(), R.layout.view_product_extend, this);
        mCategory = (TextView) findViewById(R.id.tv_category);
        mContainer = (FlowLayout) findViewById(R.id.fl_op_container);
        mMargin = UIUtils.dip2px(8);
    }

    CsBase.ItemExtend extendValue;

    public void showOption(CsBase.ItemExtend extendValue) {
        mCategory.setText(extendValue.getAttr().getAttrName() + ":");

        this.extendValue = extendValue;
        mContainer.setHorizontalSpacing(mMargin);
        mContainer.setVerticalSpacing(mMargin);
        if (extendValue != null) {
            List<CsBase.ItemExtendOption> optionsList = extendValue.getOptionsList();
            if (optionsList.size() > 0) mCategory.setVisibility(View.VISIBLE);
            for (CsBase.ItemExtendOption option : optionsList) {
                if (!"".equals(option.getOptionName())) {
                    TextView sizeButton = new TextView(getContext());
                    sizeButton.setGravity(Gravity.CENTER);
                    String optionName = option.getOptionName();
                    sizeButton.setText(optionName.replaceAll("\n","").trim());
                    sizeButton.setTextSize((float) (mMargin / 1.6));
                    sizeButton.setPadding((int) (mMargin * 1.5), 0, (int) (mMargin * 1.5), 0);
                    int height = UIUtils.dip2px(28);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
                    mContainer.addView(sizeButton, params);
                    sizeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < mContainer.getChildCount(); i++) {
                                if (v.equals(mContainer.getChildAt(i))) {
                                    mCurrentOption = i;
                                }
                            }
                            switchCurrentOption();
                        }
                    });
                }
                switchCurrentOption();
            }
        }
    }

    public  CsBase.ItemExtend getExtendValue() {
        return extendValue;
    }



    public CsBase.PairIntInt getCurrentOptionValue() {
        if (extendValue != null&&extendValue.getOptionsList().size()>0) {
            int attrId = extendValue.getAttr().getAttrId();
            int optionId = extendValue.getOptions(mCurrentOption).getOptionId();
            return CsBase.PairIntInt.newBuilder().setK(attrId).setV(optionId).build();
        }
        return null;
    }


    private void switchCurrentOption() {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            TextView button = (TextView) mContainer.getChildAt(i);
            if (mCurrentOption == i) {
                button.setBackground(getResources().getDrawable(R.drawable.shape_for_price_selected));
                button.setTextColor(Color.RED);
            } else {
                button.setBackground(getResources().getDrawable(R.drawable.shape_for_price_normal));
                button.setTextColor(getResources().getColor(R.color.text_gray));
            }
        }
    }

}
