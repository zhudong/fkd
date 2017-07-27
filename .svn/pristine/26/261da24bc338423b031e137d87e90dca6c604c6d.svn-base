package com.fuexpress.kr.ui.activity.package_detail;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

import com.fuexpress.kr.model.BrandManager;
import com.fuexpress.kr.ui.adapter.AutoEdAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by andy on 2017/3/6.
 */
public class AutoEditText extends AutoCompleteTextView {


   /* public AutoEditText(Context context) {
        this(context, null);
    }

    public AutoEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public AutoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
*/

    public AutoEditText(Context context) {
        super(context);
    }

    public AutoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initdata();
    }

    public AutoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initdata();
    }


    private void initdata() {
        List<String> brands = BrandManager.getInstance(getContext()).getBrands("");
        setAdapter(new AutoEdAdapter<String>(getContext(), 0, brands));
//        setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, brands));

    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    boolean mFocused;

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(!mFocused){
            try {
                Field field = AutoCompleteTextView.class.getDeclaredField("mPopupCanBeUpdated");
                field.setAccessible(true);
                field.setBoolean(AutoEditText.this,true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            performFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
        }
        mFocused = focused;
    }


    public void append() {
        String s = getText().toString().trim();
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
            append();

        super.onDetachedFromWindow();
    }
}
