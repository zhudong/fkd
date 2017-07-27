package com.fuexpress.kr.ui.view.imageselector.imsadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.view.imageselector.ImageConfig;
import com.fuexpress.kr.ui.view.imageselector.imsbean.Folder;

import java.util.ArrayList;
import java.util.List;


/**
 * @创建者 ${user}
 * @创时间 2016/7/8
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public class FolderAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mLayoutInflater;
    private List<Folder> folderList = new ArrayList<>();
    private final static String TAG = "FolderAdapter";

    private int lastSelected = 0;

    private ImageConfig imageConfig;
    private int mImageSize;

    public FolderAdapter(Context context, ImageConfig imageConfig) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.imageConfig = imageConfig;
    }

    public void setData(List<Folder> folders,int ImageListSize) {
        if (folders != null && folders.size() > 0) {
            folderList = new ArrayList<>();
            folderList.addAll(folders);
        } else {
            folderList.clear();
        }
        mImageSize = ImageListSize;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return folderList.size() + 1;
    }

    @Override
    public Folder getItem(int position) {
        if (position == 0)
            return null;
        return folderList.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.imageselector_item_folder, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            if (position == 0) {
                holder.folder_name_text.setText(R.string.all_folder);
                holder.image_num_text.setText("" + mImageSize + (context.getResources().getText(R.string.sheet)));

                if (folderList.size() > 0) {
                    Folder folder = folderList.get(0);

                    imageConfig.getImageLoader().displayImage(context, folder.cover.path, holder.folder_image);

                    //                    Glide.with(context)
                    //                            .load(new File(folder.cover.path))
                    //                            .placeholder(R.mipmap.imageselector_photo)
                    //                            .centerCrop()
                    //                            .into(holder.folder_image);
                }
            } else {

                Folder folder = getItem(position);
                holder.folder_name_text.setText(folder.name);
                holder.image_num_text.setText("" + folder.images.size() + (context.getResources().getText(R.string.sheet)));

                imageConfig.getImageLoader().displayImage(context, folder.cover.path, holder.folder_image);

                //                Glide.with(context)
                //                        .load(new File(folder.cover.path))
                //                        .placeholder(R.mipmap.imageselector_photo)
                //                        .centerCrop()
                //                        .into(holder.folder_image);
            }

            if (lastSelected == position) {
                holder.indicator.setVisibility(View.GONE);
            } else {
                holder.indicator.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {

        ImageView folder_image;
        TextView folder_name_text;
        TextView image_num_text;
        ImageView indicator;

        ViewHolder(View itemView) {
            folder_image = (ImageView) itemView.findViewById(R.id.folder_image);
            folder_name_text = (TextView) itemView.findViewById(R.id.folder_name_text);
            image_num_text = (TextView) itemView.findViewById(R.id.image_num_text);
            indicator = (ImageView) itemView.findViewById(R.id.indicator);
            itemView.setTag(this);
        }

    }

    public int getSelectIndex() {
        return lastSelected;
    }


    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }


    public void setSelectIndex(int position) {
        if (lastSelected == position)
            return;
        lastSelected = position;
        notifyDataSetChanged();
    }

}
