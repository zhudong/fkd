package com.fuexpress.kr.ui.activity.product_detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.view.LikeButton;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by andy on 2017/7/3.
 */
public class ProductButtomView extends RelativeLayout {

    @BindView(R.id.btn_like_item)
    LikeButton mBtnLikeItem;
    @BindView(R.id.img_add_cart)
    LinearLayout mImgAddCart;
    @BindView(R.id.btn_add_album)
    TextView mBtnAddAlbum;

    public ProductButtomView(Context context) {
        this(context, null);
    }

    public ProductButtomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductButtomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_product_buttom, this);
        ButterKnife.bind(this);
    }

    public LikeButton getBtnLikeItem() {
        return mBtnLikeItem;
    }

    public LinearLayout getImgAddCart() {
        return mImgAddCart;
    }

    public TextView getBtnAddAlbum() {
        return mBtnAddAlbum;
    }
}
