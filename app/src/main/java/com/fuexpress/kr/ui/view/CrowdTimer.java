package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.utils.CommonUtils;

import java.util.Date;

import fksproto.CsCrowd;


/**
 * Created by yuan on 2016-4-7.
 */
public class CrowdTimer extends LinearLayout {

    private TextView mTvStartTiem;
    private TextView mTvHasTiemWord;
    private TextView mTvHasTiem;
    public boolean panding=false;
    public CrowdTimer(Context context) {
        this(context, null);
    }

    public CrowdTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public void initTime(CsCrowd.Crowd crowd, boolean panding){
        this.panding=panding;
        initTime(crowd);
    }
    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.view_for_crowd_timer, this);
        mTvStartTiem = (TextView) rootView.findViewById(R.id.tv_start_time);
        mTvHasTiem = (TextView) rootView.findViewById(R.id.tv_has_time);
        mTvHasTiemWord = (TextView) rootView.findViewById(R.id.tv_has_time_words);

    }

    public void initTime(CsCrowd.Crowd crowd) {
        Date currentTime = new Date();
        switch (crowd.getState()){
            case CsCrowd.CrowdState.CROWD_STATE_PENDING_VALUE:
                mTvStartTiem.setVisibility(View.VISIBLE);
                mTvHasTiem.setVisibility(View.GONE);
                mTvHasTiemWord.setVisibility(View.GONE);
                long startTimer = (((long)crowd.getBeginTime())*1000);
                String time = CommonUtils.getFormatDate2(startTimer);
                String hour = time.split(" ")[1];
                String[] split = time.split(" ")[0].split("-");
                String day = split[2];
                String mouth = split[1];
                String sStartTtime = getResources().getString(R.string.String_time_start_cowd);
                sStartTtime = String.format(sStartTtime,new Object[]{mouth, day, hour});
                mTvStartTiem.setText(sStartTtime);

                break;
            case CsCrowd.CrowdState.CROWD_STATE_CROWDING_VALUE:
                mTvStartTiem.setVisibility(View.GONE);
                mTvHasTiem.setVisibility(View.VISIBLE);
                mTvHasTiemWord.setVisibility(View.VISIBLE);
                int days  = (int) ((crowd.getEndTime() - currentTime.getTime() / 1000) / (24 * 60 * 60));
                if(days<=0){
                    int hours = (int) ((crowd.getEndTime() - currentTime.getTime() / 1000) / (60 * 60));
                    String hasTime = getResources().getString(R.string.String_time_has_hours,hours + "");
                    mTvHasTiem.setText(hasTime);
                }else {
                    String hasTime = getResources().getString(R.string.String_time_has_days,days + "");
                    mTvHasTiem.setText(hasTime);
                }
                break;
            case CsCrowd.CrowdState.CROWD_STATE_SUCCESS_VALUE:
                mTvStartTiem.setVisibility(View.VISIBLE);
                mTvHasTiem.setVisibility(View.GONE);
                mTvHasTiemWord.setVisibility(View.GONE);

                mTvStartTiem.setText(getResources().getString(R.string.String_crowd_sucess));
                break;
            case CsCrowd.CrowdState.CROWD_STATE_FAILED_VALUE:
                mTvStartTiem.setVisibility(View.VISIBLE);
                mTvHasTiem.setVisibility(View.GONE);
                mTvHasTiemWord.setVisibility(View.GONE);

                mTvStartTiem.setText(getResources().getString(R.string.String_crowd_fail));

                break;
            case CsCrowd.CrowdState.CROWD_STATE_CHECKING_VALUE:
                mTvStartTiem.setVisibility(View.VISIBLE);
                mTvHasTiem.setVisibility(View.GONE);
                mTvHasTiemWord.setVisibility(View.GONE);

                mTvStartTiem.setText(getResources().getString(R.string.String_start_crowd));
                break;
        }
        if(panding){
            //未付款
            mTvStartTiem.setVisibility(View.VISIBLE);
            mTvHasTiem.setVisibility(View.GONE);
            mTvHasTiemWord.setVisibility(View.GONE);
            mTvStartTiem.setText(getResources().getString(R.string.String_crowd_panding));
        }
    }

}
