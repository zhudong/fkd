package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;


/**
 * Created by yuan on 2016-4-8.
 */
public class OrderMessageView extends LinearLayout {

    private View mRootView;
    public EditText mMessage;
    private TextView mWords;
    private int mWordsCount =200;

    public OrderMessageView(Context context) {
        this(context, null);
    }

    public OrderMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mRootView = View.inflate(getContext(), R.layout.view_order_message, this);
        mMessage = (EditText) mRootView.findViewById(R.id.et_order_message);
        mWords = (TextView) mRootView.findViewById(R.id.tv_number_count);
        mWords.setText(String.format(getContext().getResources().getString(R.string.String_word_number), mWordsCount));
        initEvent();
    }

    private void initEvent() {
        mMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mWords.setText(String.format(getContext().getResources().getString(R.string.String_word_number), mWordsCount - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>200){
                    mMessage.setText(s.subSequence(0,200));
                    mMessage.setSelection(200);
                }
            }
        });
    }

    public String getMessage(){
        return  mMessage.getText().toString();
    }
    public void setMessage(String msg){
        mMessage.setText(msg);
    }
    public void setHint(String hint){
        mMessage.setHint(hint);
    }
}
