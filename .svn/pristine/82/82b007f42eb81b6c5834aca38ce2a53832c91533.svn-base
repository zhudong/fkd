package com.fuexpress.kr.ui.view.wheel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by andy on 2016/11/14.
 */
public class OpenListView extends ListView {
    public OpenListView(Context context) {
        super(context);
    }

    public OpenListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OpenListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
