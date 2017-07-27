package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.meiqia.meiqiasdk.third.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Longer on 2017/6/26.
 */
public class PhotoViewAdapter extends PagerAdapter {

    private Activity mContent;
    List<String> mImages;

    public PhotoViewAdapter(Activity content, List<String> imgs) {
        this.mContent = content;
        this.mImages = imgs;
    }

    @Override
    public int getCount() {
        if (mImages != null) {
            if (mImages.size() > 1) {
                return 10000 - 1000 % mImages.size();
            } else {
                return 1;
            }
        }
        return 0;

    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        position =  position % mImages.size();
        PhotoView photoView = new PhotoView(container.getContext());
        //photoView.setImageResource(sDrawables[position]);
        ImageLoader.getInstance().displayImage(mImages.get(position)+ Constants.ImgUrlSuffix.mob_list, photoView, ImageLoaderHelper.getInstance(mContent).getDisplayOptions(true));
        // Now just add PhotoView to ViewPager and return it
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
