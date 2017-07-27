package com.fuexpress.kr.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.fuexpress.kr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Longer on 2016/12/6.
 */
public class HelpItemHeadView extends FrameLayout {
    private List<String> mImageUrls;
    private boolean mIsAddImageData = false;

    public HelpItemHeadView(Context context) {
        this(context, null);
    }

    public HelpItemHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HelpItemHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.help_item_head_view, this);



    }

    public void setImageUrls(List<String> imageUrls) {
        mImageUrls = new ArrayList<>();
        mImageUrls.addAll(imageUrls);
        mIsAddImageData = true;
    }


}
