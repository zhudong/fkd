package com.fuexpress.kr.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.KuaiDiBaen;
import com.fuexpress.kr.bean.KuaiDiGroup;
import com.fuexpress.kr.ui.adapter.ExpressageAdapter;
import com.fuexpress.kr.ui.view.CustomToast;
import com.google.gson.Gson;
import com.google.protobuf.ProtocolStringList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fksproto.CsParcel;


public class QueryShippingActivity extends BaseActivity {
    public static String kudi100 = "http://www.kuaidi100.com/query?type=%1@&postid=%2@";


    public static String SHIPPINGS = "shippings";
    private View mRootView;
    private ProtocolStringList shippingNumbersList;
    private CsParcel.ParcelShipping shipping;
    private String companyCode;
    private static Handler mHandler = new Handler();
    private ExpandableListView listView;
    private ExpressageAdapter expressageAdapter;
    ArrayMap<Integer, KuaiDiBaen> childs;


    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_query_shipping, null);
        listView = (ExpandableListView) mRootView.findViewById(R.id.ex_lv_body);
        initTitel(getString(R.string.package_query_parcel));
        initData();
        return mRootView;
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(SHIPPINGS);
        List<KuaiDiGroup> groups = new ArrayList<>();
        if (bundle != null) {
            shipping = (CsParcel.ParcelShipping) bundle.getSerializable(SHIPPINGS);
            companyCode = shipping.getShippingCompanyCode();
            shippingNumbersList = shipping.getShippingNumbersList();
            for (int k = 0; k < shippingNumbersList.size(); k++) {
                String s = shippingNumbersList.get(k);
                KuaiDiGroup group = new KuaiDiGroup();
                group.shippingCode = companyCode;
                group.shippingName = shipping.getShippingCompanyName();
                group.shippingNumber = s;
                groups.add(group);
            }
        }
        childs = new ArrayMap<>();
        expressageAdapter = new ExpressageAdapter(groups, childs, QueryShippingActivity.this);
        listView.setAdapter(expressageAdapter);

        for (int i = 0; i < shipping.getShippingNumbersCount(); i++) {
            getdetail(i);
        }
    }


    private void initTitel(String string) {
        mRootView.findViewById(R.id.title_iv_left).setOnClickListener(this);
        mRootView.findViewById(R.id.title_tv_right).setVisibility(View.GONE);
        mRootView.findViewById(R.id.title_iv_right).setVisibility(View.GONE);

        TextView title = (TextView) mRootView.findViewById(R.id.title_tv_center);
        View lefttv = mRootView.findViewById(R.id.title_tv_left);
        lefttv.setOnClickListener(this);
        ((TextView) lefttv).setText("  ");

        title.setText(string);
    }

    public void getdetail(final int postion) {
        final String s = shippingNumbersList.get(postion);
        String shippingCompanyCode = shipping.getShippingCompanyCode();
//        s = "424888128793";
//        shippingCompanyCode = "shunfeng";
        final String url = kudi100.replace("%1@", shippingCompanyCode).replace("%2@", s);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        String string = body.string();
                        Gson gson = new Gson();
                        final KuaiDiBaen kuaiDiBaen = gson.fromJson(string, KuaiDiBaen.class);
                        KuaiDiGroup group = (KuaiDiGroup) expressageAdapter.getGroup(postion);
                        group.state = kuaiDiBaen.state;
                        Log.d("kuadi", string);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (kuaiDiBaen.data != null && kuaiDiBaen.data.size() > 0) {
                                    childs.put(postion, kuaiDiBaen);
                                    expressageAdapter.notifyDataSetChanged();
                                    listView.expandGroup(postion);
                                } else {
                                    CustomToast.makeText(QueryShippingActivity.this, s+getString(R.string.package_no_shipping_msg), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
        }
    }

}
