package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.ui.adapter.UserPhoneNumberAdapter;

import java.util.ArrayList;



/**
 * Created by root on 17-2-27.
 */

public class ChooseUserPhoneActivity extends BaseActivity {
    public static final String PHONE_LIST="phone_list";
    ListView lv;
    UserPhoneNumberAdapter mAdapter;
    ArrayList<String> phones;
    @Override
    public View setInitView() {

        View view = View.inflate(this, R.layout.activity_choose_user_phone, null);
        setLocalInBottom(view);
        lv=(ListView)view.findViewById(R.id.lv_body);
        lv.setDivider(new ColorDrawable(Color.GRAY));
        lv.setDividerHeight(1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(phones!=null){
                    sendSms(phones.get(position));
                }
                finish();
            }
        });
        return view;
    }
    public void setLocalInBottom(View view) {
        Window window = this.getWindow();//宽度为屏幕宽,位置为底部
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        view.setMinimumWidth(mWidthPixels);
        window.setAttributes(lp);
        LinearLayout ll_01_in_down_to_up_dialog = (LinearLayout) view.findViewById(R.id.ll_01_in_down_to_up_dialog);
        ll_01_in_down_to_up_dialog.setAlpha(0.9f);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phones =getIntent().getStringArrayListExtra(PHONE_LIST);
        if(phones!=null){
            mAdapter=new UserPhoneNumberAdapter(this,phones);
            lv.setAdapter(mAdapter);
        }
    }
    public  void sendSms(String toBody){
        Uri smsToUri = Uri.parse("smsto:"+toBody);

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", InviteFriendsActivity.response.getTitile()+InviteFriendsActivity.response.getInviteurl());

        startActivity(intent);
    }
}
