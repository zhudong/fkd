package com.fuexpress.kr.ui.activity.product_detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.TimeUtils;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andy on 2017/7/3.
 */
public class ProductCommentView extends LinearLayout {
    private DataBean mData = new DataBean();
    @BindView(R.id.img_person_icon)
    SelectableRoundedImageView mImgPersonIcon;
    @BindView(R.id.tv_person_nicke_name)
    TextView mTvPersonNickeName;
    @BindView(R.id.tv_comment_date)
    TextView mTvCommentDate;
    @BindView(R.id.tv_person_comment)
    TextView mTvPersonComment;
    @BindView(R.id.ll_imgs)
    LinearLayout mLlImgs;
    private DisplayImageOptions mImageOptions;
    private int mImageWidth;
    private int mImageMarginLeft;
    private int mImageCount;

    public ProductCommentView(Context context) {
        this(context, null);
    }

    public ProductCommentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductCommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.item_for_product_comment, this);
        ButterKnife.bind(this);
        mImageOptions = ImageLoaderHelper.getInstance(getContext()).getDisplayOptions();
        mImageWidth = UIUtils.dip2px(50);
        mImageMarginLeft = UIUtils.dip2px(4);
    }

    public void notifyDataChange() {
        ImageLoader.getInstance().displayImage(mData.imgIcon, mImgPersonIcon, mImageOptions);
        mTvPersonNickeName.setText(mData.getName());
        mTvCommentDate.setText(TimeUtils.getDateStyleString(mData.getSeconds()));
        mTvPersonComment.setText(mData.getComment());
        showCommentImags();
    }

    private void showCommentImags() {
        int max = getWidth() / (mImageWidth + mImageMarginLeft);
        mImageCount = mData.getImageUrls().size() > max ? max : mData.getImageUrls().size();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mImageWidth, mImageWidth);

        for (int i = 0; i < mImageCount; i++) {
            ImageView img = new ImageView(getContext());
            img.setPadding(mImageMarginLeft, mImageMarginLeft, mImageMarginLeft, mImageMarginLeft);
            img.setCropToPadding(true);
            img.setBackground(getResources().getDrawable(R.drawable.shape_album_circle_bg));
            img.setImageDrawable(getResources().getDrawable(R.color.home_add_and_like_bg));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.setMargins(mImageMarginLeft, 0, 0, 0);
            mLlImgs.addView(img, params);
            String uri = mData.getImageUrls().get(i) + Constants.ImgUrlSuffix.small_9;
            ImageLoader.getInstance().displayImage(uri, img, mImageOptions);
        }
    }

    class DataBean {
        String imgIcon;
        String name;
        int seconds;
        String comment;
        List<String> imageUrls = new ArrayList<>();


        public String getImgIcon() {
            return imgIcon;
        }

        public void setImgIcon(String imgIcon) {
            this.imgIcon = imgIcon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public List<String> getImageUrls() {
            return imageUrls;
        }

        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }
    }

    public DataBean getData() {
        return mData;
    }

    public int getImageCount() {
        return mImageCount;
    }
}
