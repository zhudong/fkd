package com.fuexpress.kr.ui.view.imageselector;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.R;


/**
 * @创建者 ${user}
 * @创时间 2016/7/8
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class ImsDisPlayLoader implements ImageLoader {
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        //com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://" + path, imageView, ImageLoaderHelper.getInstance(context).getDisplayOptions());
        Glide.with(context)
                .load(path)
                .placeholder(R.mipmap.imageselector_photo)
                .centerCrop()
                .into(imageView);
    }
}
