package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;


/**
 * Created by yuan on 2016/3/7.
 */
public class LikeButton extends LinearLayout {
    public static final int STATE_LIKE = 0 ;
    public static final int STATE_LIKING = 1 ;
    public static final int STATE_UNLIKE = 2 ;
    public static final int STATE_UNLIKING = 3 ;
    private int mState= STATE_UNLIKE ;
    private ImageView mImgLike;
    private TextView mText;
    private TextView mCount;


    public LikeButton(Context context) {
        this(context, null);
    }

    public LikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View root = View.inflate(context, R.layout.view_for_like_button, this);
        mImgLike = (ImageView) root.findViewById(R.id.img_is_like);
        mText = (TextView) root.findViewById(R.id.tv_liking);
        mCount = (TextView) root.findViewById(R.id.tv_like_count);
    }
    public void setNumber(String count){mCount.setText(count);}
    public void setNumber(int number){
        mCount.setText(number+"");
    }
    public void setmText(String string){
        mText.setText(string);
    }
    public void setmImgEdit(Context context){
        mImgLike.setImageResource(R.mipmap.edit_red);
    }
    public void setmImgFocus(Context context){
        mImgLike.setImageResource(R.mipmap.focus);
    }
    public void setmImgUnFocus(Context context){
        mImgLike.setImageResource(R.mipmap.unfocus);
    }
    public void switchState(int state){
        switch (state){
            case STATE_LIKE:
                mImgLike.setImageResource(R.mipmap.unlike);
                mText.setText(getResources().getString(R.string.String_btn_like));
                break;
            case STATE_LIKING:
                mImgLike.setImageResource(R.mipmap.like);
                mText.setText(getResources().getString(R.string.String_btn_liking));
                break;
            case STATE_UNLIKE:
                mImgLike.setImageResource(R.mipmap.like);
                mText.setText(getResources().getString(R.string.String_btn_unlike));
                break;
            case STATE_UNLIKING:
                mImgLike.setImageResource(R.mipmap.unlike);
                mText.setText(getResources().getString(R.string.String_btn_unliking));
                break;
        }
    }
}
