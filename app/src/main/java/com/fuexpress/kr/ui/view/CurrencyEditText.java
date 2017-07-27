package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.utils.LogUtils;

import java.text.DecimalFormat;

/**
 * Created by andy on 2016/12/28.
 */
public class CurrencyEditText extends EditText {

    private String mText;

    public CurrencyEditText(Context context) {
        super(context);
        init();
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {

        final DecimalFormat myformat = new DecimalFormat();
        myformat.applyPattern("##,###.##");
        this.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    setText(myformat.format(Double.parseDouble(mText)));
                }
                return false;
            }
        });
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                removeTextChangedListener(this);
                String str[] = s.toString().split(",");
                String s1 = "";
                for (int i = 0; i < str.length; i++) {
                    s1 += str[i];
                }
                mText = s1;
//                if (!TextUtils.isEmpty(s1)) {
//                    setText(myformat.format(Double.parseDouble(s1)));
//                }
//                setSelection(getText().length());
//                addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public String getmText() {
        return mText;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        setCurrency(AccountManager.getInstance().getCurrencyCode());
    }

    public void setCurrency(String currencyCode) {
//        if (currencyCode.contains("CNY")) {
//            this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        } else
        if (currencyCode.contains("KRW") || currencyCode.contains("TWD")) {
            this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        } else {
            this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
    }

}
