package com.fuexpress.kr.ui.view.imageselector;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * @创建者 ${user}
 * @创时间 2016/7/8
 * @描述
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, ImageView imageView);
}
