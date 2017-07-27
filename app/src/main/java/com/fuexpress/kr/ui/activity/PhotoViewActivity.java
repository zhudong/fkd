package com.fuexpress.kr.ui.activity;

import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.view.PhotoHead;

import java.util.List;

/**
 * Created by Longer on 2017/6/26.
 */
public class PhotoViewActivity extends BaseActivity {

    public static String PAGE_INDEX = "page_index";
    public static String PAGE_IMAGES = "page_images";

    private ViewPager mBody;

    @Override
    public View setInitView() {

      /*  View mRootView = View.inflate(this, R.layout.activity_photo_view, null);
        mBody = (ViewPager) mRootView.findViewById(R.id.vp_photo_view);
        int index = getIntent().getIntExtra(PAGE_INDEX, 0);
        List<String> imgs = getIntent().getStringArrayListExtra(PAGE_IMAGES);

        PhotoViewAdapter adapter = new PhotoViewAdapter(this, imgs);
        mBody.setAdapter(adapter);
        if (index != 0) {
            index = index % imgs.size();
            mBody.setCurrentItem(index);
        }
        return mRootView;
*/

        int index = getIntent().getIntExtra(PAGE_INDEX, 0);
        List<String> imgs = getIntent().getStringArrayListExtra(PAGE_IMAGES);
        PhotoHead photoHead = new PhotoHead(this);
        photoHead.setPointIndex(index);
        photoHead.initData(imgs);
        photoHead.setImageIndex(index);
        return photoHead.getRootView();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//监听点击返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            finish();
            overridePendingTransition(R.anim.activity_translate_out_alpha, R.anim.activity_translate_in_alpha);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
