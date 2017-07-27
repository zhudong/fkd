package com.fuexpress.kr.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Longer on 2017/2/8.
 * 网易七鱼的图片加载类
 */
public class PicassoImageLoader implements UnicornImageLoader {

    private final Set<Target> protectedFromGarbageCollectorTargets = new HashSet<>();

    @Nullable
    @Override
    public Bitmap loadImageSync(String uri, int width, int height) {
        return null;
    }

    @Override
    public void loadImage(final String uri, final int width, final int height, final ImageLoaderListener listener) {
        final Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (listener != null) {
                    listener.onLoadComplete(bitmap);
                    protectedFromGarbageCollectorTargets.remove(this);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                if (listener != null) {
                    listener.onLoadFailed(null);
                    protectedFromGarbageCollectorTargets.remove(this);
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                RequestCreator requestCreator = Picasso.with(SysApplication.getContext()).load(uri).config(Bitmap.Config.RGB_565);
                if (width > 0 && height > 0) {
                    requestCreator = requestCreator.resize(width, height);
                }
                protectedFromGarbageCollectorTargets.add(mTarget);
                requestCreator.into(mTarget);
            }
        });
        /*Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestCreator requestCreator = Picasso.with(SysApplication.getContext()).load(uri).config(Bitmap.Config.RGB_565);
                if (width > 0 && height > 0) {
                    requestCreator = requestCreator.resize(width, height);
                }
                protectedFromGarbageCollectorTargets.add(mTarget);
                requestCreator.into(mTarget);
            }
        });*/
    }
}
