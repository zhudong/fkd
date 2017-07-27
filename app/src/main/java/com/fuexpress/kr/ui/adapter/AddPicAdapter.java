package com.fuexpress.kr.ui.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.activity.AddRequireActivity;
import com.fuexpress.kr.ui.activity.PreviewMerchandiseActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.UpLoadImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2016/12/20.
 */

public class AddPicAdapter extends BaseAdapter {
    private Context mCtx;
    private List<String> list;
    private int parentPosition;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public AddPicAdapter(Context context, List<String> list, int position) {
        this.mCtx = context;
        this.list = list;
        this.parentPosition = position;
        this.imageLoader = ImageLoader.getInstance();
        this.options = ImageLoaderHelper.getInstance(mCtx).getDisplayOptions();
    }

    @Override
    public int getCount() {
        if (list == null || list.size() == 0) {
            return 1;
        } else if (list.size() == 4) {
            return 4;
        } else {
            return list.size() + 1;
        }

    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        return null;
    }

    class Holder {
        ImageView merchandiseIv;
    }



}
