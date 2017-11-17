package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by yuan on 2016/3/9.
 */
public class ItemImgsAdapter extends PagerAdapter {
    List<String> urls;
    private Context mContext;
    String mSuffix;

    public ItemImgsAdapter(Context context, List<String> urls, String suffix) {
        this.urls = urls;
        this.mContext = context;
        this.mSuffix = suffix;
    }

    @Override
    public int getCount() {
        if (urls != null) {
            if (urls.size() > 1) {
                return 10000 - 1000 % urls.size();
            } else {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        int index = position % urls.size();
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(Color.WHITE);
        String url = urls.get(index);
        if (!TextUtils.isEmpty(mSuffix)) {
            url = url + mSuffix;
        }
//        LogUtils.d("img", url);
        ImageLoader.getInstance().displayImage(url, imageView, ImageLoaderHelper.getInstance(mContext).getDisplayOptions());
        //imageView.setImageResource(R.mipmap.ic_launcher);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        object = null;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
