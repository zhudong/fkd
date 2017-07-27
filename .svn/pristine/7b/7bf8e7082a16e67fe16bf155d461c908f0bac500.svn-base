package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.wheel.OnWheelChangedListener;
import com.fuexpress.kr.ui.view.wheel.ScreenInfo;
import com.fuexpress.kr.ui.view.wheel.WheelView;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.NumericWheelAdapterForTimePick;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.WheelViewForTimePick;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TImePickerActivity extends BaseActivity {

    private WheelViewForTimePick wv_year;
    private WheelViewForTimePick wv_month;
    private WheelViewForTimePick wv_day;
    private WheelViewForTimePick wv_hours;
    private WheelViewForTimePick wv_mins;
    public int screenheight;
    private boolean hasSelectTime;
    private static int START_YEAR = 1, END_YEAR = 10000;
    private View mRootView;
    private TextView mTv_in_time_dialog_config_btn;

    public static int getSTART_YEAR() {
        return START_YEAR;
    }

    public static void setSTART_YEAR(int sTART_YEAR) {
        START_YEAR = sTART_YEAR;
    }

    public static int getEND_YEAR() {
        return END_YEAR;
    }

    public static void setEND_YEAR(int eND_YEAR) {
        END_YEAR = eND_YEAR;
    }

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_time_picker, null);
        hasSelectTime = false;
        Window window = this.getWindow();//宽度为屏幕宽,位置为底部
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        mRootView.setMinimumWidth(UIUtils.getScreenWidthPixels(this));
        window.setAttributes(lp);
        return mRootView;
    }

    @Override
    public void initView() {
        ScreenInfo screenInfo = new ScreenInfo(TImePickerActivity.this);
        screenheight = screenInfo.getHeight();
        //screenheight = 200;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mTv_in_time_dialog_config_btn = (TextView) mRootView.findViewById(R.id.tv_in_time_dialog_config_btn);
        mTv_in_time_dialog_config_btn.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            int year1 = intent.getIntExtra("year", 0);
            int month1 = intent.getIntExtra("month", 0);
            int day1 = intent.getIntExtra("day", 0);
            if (year1 != 0 && month1 != 0 && day1 != 0) {
                initDateTimePicker(year1, month1, day1, 0, 0);
            } else {
                initDateTimePicker(year, month, day, 0, 0);
            }
        } else {
            initDateTimePicker(year, month, day, 0, 0);
        }
    }


    /**
     * @Description: TODO 弹出日期时间选择器
     */
    public void initDateTimePicker(int year, int month, int day, int h, int m) {
        // int year = calendar.get(Calendar.YEAR);
        // int month = calendar.get(Calendar.MONTH);
        // int day = calendar.get(Calendar.DATE);
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        // 年
        wv_year = (WheelViewForTimePick) mRootView.findViewById(R.id.year);
        wv_year.setVisibleItems(7);
        wv_year.setAdapter(new NumericWheelAdapterForTimePick(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(false);// 可循环滚动
        wv_year.setLabel(getString(R.string.string_year));// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        wv_month = (WheelViewForTimePick) mRootView.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapterForTimePick(1, 12));
        wv_month.setCyclic(true);
        wv_month.setLabel(getString(R.string.string_month));
        wv_month.setVisibleItems(7);
        wv_month.setCurrentItem(month);

        // 日
        wv_day = (WheelViewForTimePick) mRootView.findViewById(R.id.day);
        wv_day.setVisibleItems(7);
        wv_day.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 28));
        }
        wv_day.setLabel(getString(R.string.string_day));
        wv_day.setCurrentItem(day - 1);

        wv_hours = (WheelViewForTimePick) mRootView.findViewById(R.id.hour);
        wv_mins = (WheelViewForTimePick) mRootView.findViewById(R.id.min);
        if (hasSelectTime) {
            wv_hours.setVisibility(View.VISIBLE);
            wv_mins.setVisibility(View.VISIBLE);

            wv_hours.setAdapter(new NumericWheelAdapterForTimePick(0, 23));
            wv_hours.setCyclic(true);// 可循环滚动
            wv_hours.setLabel("时");// 添加文字
            wv_hours.setCurrentItem(h);

            wv_mins.setAdapter(new NumericWheelAdapterForTimePick(0, 59));
            wv_mins.setCyclic(true);// 可循环滚动
            wv_mins.setLabel("分");// 添加文字
            wv_mins.setCurrentItem(m);
        } else {
            wv_hours.setVisibility(View.GONE);
            wv_mins.setVisibility(View.GONE);
        }

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

            }

            @Override
            public void onChanged(WheelViewForTimePick wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big
                        .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 31));
                } else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 28));
                }
            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

            }

            @Override
            public void onChanged(WheelViewForTimePick wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 30));
                } else {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapterForTimePick(1, 28));
                }
            }
        };
        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);

        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
        int textSize = 0;
        if (hasSelectTime)
            //textSize = (screenheight / 100) * 3;
            textSize = UIUtils.dip2px(18);
        else
            //textSize = (screenheight / 100) * 4;
            textSize = UIUtils.dip2px(17);
        wv_day.TEXT_SIZE = textSize;
        wv_month.TEXT_SIZE = textSize;
        wv_year.TEXT_SIZE = textSize;
        wv_hours.TEXT_SIZE = textSize;
        wv_mins.TEXT_SIZE = textSize;

    }

    public String getTime() {
        StringBuffer sb = new StringBuffer();
        String monthString;
        String dayString;
        int month = wv_month.getCurrentItem() + 1;
        int day = wv_day.getCurrentItem() + 1;
        if (month < 10) {
            monthString = "0" + String.valueOf(month);
        } else {
            monthString = String.valueOf(month);
        }
        if (day < 10) {
            dayString = "0" + String.valueOf(day);
        } else {
            dayString = String.valueOf(day);
        }
        /*if (!hasSelectTime)
            sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1));
        else
            sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1)).append(" ")
                    .append(wv_hours.getCurrentItem()).append(":")
                    .append(wv_mins.getCurrentItem());*/
        if (!hasSelectTime)
            sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
                    .append(monthString).append("-")
                    .append(dayString);
        else
            sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
                    .append(monthString).append("-")
                    .append(dayString).append(" ")
                    .append(wv_hours.getCurrentItem()).append(":")
                    .append(wv_mins.getCurrentItem());
        return sb.toString();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_in_time_dialog_config_btn:
                Intent intent = new Intent();
                String time = getTime();
                intent.putExtra("time", getTime());
                setResult(3, intent);
                finish();
                break;
        }
    }
}
