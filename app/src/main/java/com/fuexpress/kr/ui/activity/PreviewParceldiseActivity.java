package com.fuexpress.kr.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/6/14.
 */
public class PreviewParceldiseActivity extends BaseActivity {

    public static final int REQUEST_CODE_BACK_IMG_LIST = 100;
    public static final int RESOURCE_TYPE_EDIT = 1;
    public static final int RESOURCE_TYPE_ADD = 0;
    private View rootView;
    private ViewPager mViewPager;
    private TextView currentTv;
    private TextView subTotalTv;
    private List<ImageView> imageViews;
    private List<String> imgList;
    private MyAdapter adapter;
    private DisplayImageOptions mOptions;
    private ImageLoader imageLoader;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.preview_merchandise_layout, null);

        TitleBarView titleBarView = (TitleBarView) rootView.findViewById(R.id.title_pre);
        titleBarView.setTitleText(getString(R.string.preview_title_msg));
        titleBarView.getIv_in_title_back().setOnClickListener(this);
//        TextView deleteTv = titleBarView.getSubmitTv();
        TextView deleteTv = titleBarView.getTv_in_title_right();
        deleteTv.setText(getString(R.string.preview_delete_msg));
        mViewPager = (ViewPager) rootView.findViewById(R.id.preview_view_pager);
        currentTv = (TextView) rootView.findViewById(R.id.preview_current_view_tv);
        subTotalTv = (TextView) rootView.findViewById(R.id.preview_subtotal_view_tv);
        deleteTv.setOnClickListener(this);

        initViewPager();
        return rootView;
    }

    public void initViewPager() {
        Intent intent = getIntent();

        imgList = (List<String>) intent.getExtras().getSerializable("imgList");
        int position = intent.getExtras().getInt("position");
        mOptions = ImageLoaderHelper.getInstance(this).getDisplayOptions();
        imageLoader = ImageLoader.getInstance();
        imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < imgList.size(); i++) {
            ImageView img = new ImageView(this);

            switch (intent.getIntExtra("resourceType", RESOURCE_TYPE_ADD)) {
                case RESOURCE_TYPE_EDIT:

                    //imageLoader.displayImage(imgList.get(i), img, mOptions);
                    Glide.with(this).load(imgList.get(i)).into(img);
                    break;
                case RESOURCE_TYPE_ADD:
                    try {
                        //                        img.setImageBitmap(BitmapFactory.decodeFile(imgList.get(i)));

                        String[] split = imgList.get(i).split(",");

                        if (split.length > 1 && !"".equals(split[1])) {
                            //imageLoader.displayImage(split[1] + Constants.ImgUrlSuffix.mob_list, img, mOptions);
                            Glide.with(this).load(split[1] + Constants.ImgUrlSuffix.mob_list).into(img);
                        } else {
                            Uri uri = Uri.fromFile(new File(split[0]));
                            //imageLoader.displayImage(uri.toString(), img, mOptions);
                            Glide.with(this).load(uri.toString()).into(img);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            imageViews.add(img);
        }
        adapter = new MyAdapter(imageViews);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);
        currentTv.setText(position + 1 + "");
        subTotalTv.setText(imgList.size() + "");
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTv.setText(position + 1 + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResultForActivity();
        super.onBackPressed();
    }

    public void setResultForActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        ArrayList list = new ArrayList();
        for (int i = 0; i < imgList.size(); i++) {
            String s = imgList.get(i).split(",")[0];
            list.add(s);
        }
        bundle.putSerializable("backImgList", (Serializable) list);
        intent.putExtras(bundle);
        setResult(REQUEST_CODE_BACK_IMG_LIST, intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_title_back:
                setResultForActivity();
                finish();
                break;
            case R.id.tv_in_title_right:
                showDialog();
                break;
        }
    }

    public void showDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.edit_album_dialog_title_msg));
        builder.setMessage(getString(R.string.preview_album_dialog_msg));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (imageViews.size() > 0) {
                    imgList.remove(mViewPager.getCurrentItem());
                    imageViews.remove(mViewPager.getCurrentItem());
                    adapter.notifyDataSetChanged();
                    if (imageViews.size() == 0) {
                        currentTv.setText("0");
                        setResultForActivity();
                        finish();
                    } else {
                        currentTv.setText(mViewPager.getCurrentItem() + 1 + "");
                    }
                    subTotalTv.setText(imageViews.size() + "");
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    class MyAdapter extends PagerAdapter {
        private List<ImageView> imgViews;

        public MyAdapter(List<ImageView> imageViews) {
            this.imgViews = imageViews;
        }

        @Override
        public int getCount() {
            return imgViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //            ((ViewPager)container).removeView(imgViews.get(position));
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                ((ViewPager) container).addView(imgViews.get(position), 0);
            } catch (Exception e) {
                //handler something
            }
            return imgViews.get(position);
        }


        @Override
        public int getItemPosition(Object object) {
            //            if ( mChildCount > 0) {
            //                mChildCount --;
            return POSITION_NONE;
            //            }
            //            return super.getItemPosition(object);

        }
    }
}
