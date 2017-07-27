package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.fuexpress.kr.model.BrandManager;

import java.util.List;

/**
 * Created by Administrator on 2017/3/6.
 */

public class MyAutoCompleteView extends AutoCompleteTextView {
    public MyAutoCompleteView(Context context) {
        super(context);
    }

    public MyAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initdata();
    }

    public MyAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initdata();
    }


    private void initdata() {
        List<String> brands = BrandManager.getInstance(getContext()).getBrands("");
//        setAdapter(new AutoEdAdapter<String>(getContext(),0, brands));
        setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, brands));

    }


    @Override
    public boolean enoughToFilter() {
        return true;
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        performFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
    }


    public void append() {
        String s = getText().toString();
        if (!TextUtils.isEmpty(s)) {
            BrandManager instance = BrandManager.getInstance(getContext());
            if (instance.getBrand(s) != null) {
                instance.updateBrand(s);
            } else {
                instance.appendBrand(s);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        String text = getText().toString();
        if (!TextUtils.isEmpty(text))
            BrandManager.getInstance(getContext()).appendBrand(text);

        super.onDetachedFromWindow();
    }
}
