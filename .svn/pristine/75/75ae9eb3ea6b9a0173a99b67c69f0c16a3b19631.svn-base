package com.fuexpress.kr.model;

import android.content.Context;
import android.content.Intent;

import com.fuexpress.kr.conf.Constants;
import com.meiqia.core.MQScheduleRule;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;


/**
 * Created by Administrator on 2016/4/28.
 */
public class MeiqiaManager {

    private static final String TAG = "MeiqiaManager";
    private static Context mContext;
    private static MeiqiaManager instance = new MeiqiaManager();
    private MeiqiaManager(){

    }

    public static MeiqiaManager getInstance(Context context){
        mContext = context;
        return instance;
    }


    public void contactCustomerService(){

        MQConfig.init(mContext, Constants.MEIQIA_APP_KEY, new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
                Intent intent = new MQIntentBuilder(mContext)
                        .setScheduledGroup(Constants.MEIQIA_APP_GROUP) // groupId 可以从工作台查询
                        .setScheduleRule(MQScheduleRule.REDIRECT_GROUP)
                        .build();
                mContext.startActivity(intent);
            }

            @Override
            public void onFailure(int code, String message) {
//                Toast.makeText(MainActivity.this, "int failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
