package com.fuexpress.kr.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.CouponCurrencyInfoData;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.CouponDataManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.IntegralActivity;
import com.fuexpress.kr.ui.activity.WithdrawActivity;
import com.fuexpress.kr.ui.adapter.CouponAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomListView;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fksproto.CsUser;

import static android.R.attr.fragment;

/**
 * Created by Administrator on 2017-3-6.
 */

public class IntegralFragment extends BaseFragment {

    public static final String CURRENCY_CODE = "currency_code";
    public static final String CURRENCY_SIGN = "currency_sign";

    private View rootView;
    private String currencyCode;
    private String currencySign;
    private TextView availableBalanceTv;
    private TextView availablePointsTv;
    private RefreshListView integralListView;
    private List<CsUser.creditsDetail> list;
    private IntegralAdapter integralAdapter;
    public CsUser.GetCreditsDetailResponse response;
    private int pageNo = 1;
    private boolean isAlbumHasMore=true;



    @Override
    protected void initTitleBar() {

    }


    @Override
    public View initView() {
        rootView = View.inflate(getContext(), R.layout.fragment_integral, null);
        availableBalanceTv = (TextView) rootView.findViewById(R.id.integral_available_balance_tv);
        availablePointsTv = (TextView) rootView.findViewById(R.id.integral_available_points_tv);
        integralListView = (RefreshListView) rootView.findViewById(R.id.integral_list_view);
        return rootView;
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        currencyCode = bundle.getString(CURRENCY_CODE);
        currencySign = bundle.getString(currencySign);
        list = new ArrayList<CsUser.creditsDetail>();
        integralAdapter = new IntegralAdapter(getContext(), list);
        integralListView.setAdapter(integralAdapter);
        getCreditsDetailRequest(currencyCode, 1);
        integralListView.setOnRefreshListener(mOnRefreshListener);
    }

    public static IntegralFragment newInstance(Bundle args) {
        IntegralFragment f = new IntegralFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        if(event.getType() == BusEvent.GO_TO_WITHDRAW){
            Intent intent = new Intent();
            intent.setClass(getActivity(), WithdrawActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("couponCurrencyInfoData", (CouponCurrencyInfoData)event.getParam());
            bundle.putSerializable("currentGetCreditsDetailResponse", response);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private RefreshListView.OnRefreshListener mOnRefreshListener=new RefreshListView.OnRefreshListener() {
        @Override
        public void onRefresh(RefreshListView refreshListView) {
            pageNo = 1;
            getCreditsDetailRequest(currencyCode, pageNo);

        }

        @Override
        public void onLoadMore(RefreshListView refreshListView) {
            if(isAlbumHasMore){
                getCreditsDetailRequest(currencyCode, ++pageNo);
            }
        }
    };



    public void getCreditsDetailRequest(final String currencyCode, final int pageNo){
        CsUser.GetCreditsDetailRequest.Builder builder = CsUser.GetCreditsDetailRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setCurrencycode(currencyCode);
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        builder.setPagenum(pageNo);
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetCreditsDetailResponse>() {

            @Override
            public void onSuccess(CsUser.GetCreditsDetailResponse response) {
                LogUtils.d(response.toString());
                ((IntegralActivity)getActivity()).creditMap.put(currencyCode, response);

                Message msg = Message.obtain();
                msg.obj = response;
                msg.arg1 = pageNo;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ((IntegralActivity)getActivity()).loaded = true;
            response = (CsUser.GetCreditsDetailResponse) msg.obj;
            if(TextUtils.equals(response.getMore(),"yes")){
                isAlbumHasMore = true;
                integralListView.setHasLoadMore(true);
                integralListView.stopLoadMore(true);
//                getCreditsDetailRequest(currencyCode, msg.arg1 + 1);
            }else {
                isAlbumHasMore = false;
                integralListView.setHasLoadMore(false);
                integralListView.stopLoadMore(false);
            }
            integralListView.stopRefresh();

            if(response.getCreditsdetailCount() > 0){
                if(msg.arg1 == 1){
                    list.clear();
                }
                list.addAll(response.getCreditsdetailList());
                integralAdapter.notifyDataSetChanged();
            }
//            availableBalanceTv.setText(UIUtils.getCurrency(getContext(), currencyCode, Float.parseFloat(response.getAmount())));
            availableBalanceTv.setText(response.getAmount());
            availablePointsTv.setText(getString(R.string.integral_available_points, response.getCredits() + ""));
        }
    };

    class IntegralAdapter extends BaseAdapter{

        private Context context;
        private List<CsUser.creditsDetail> detailList;

        public IntegralAdapter(Context context, List<CsUser.creditsDetail> detailList){
            this.context = context;
            this.detailList = detailList;
        }

        @Override
        public int getCount() {
            return detailList != null ? detailList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return detailList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.integral_item, null);
                holder = new ViewHolder();
                holder.dateTv = (TextView) convertView.findViewById(R.id.integral_item_date_tv);
                holder.descTv= (TextView) convertView.findViewById(R.id.integral_item_desc_tv);
                holder.changeTv = (TextView) convertView.findViewById(R.id.integral_item_change_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            CsUser.creditsDetail item = detailList.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sdf.parse(item.getCreatetime());
                holder.dateTv.setText(getString(R.string.integral_list_date, sdf.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.descTv.setText(item.getDescription());
            holder.changeTv.setText(item.getChange() + "");

            return convertView;
        }

        class ViewHolder {
            TextView dateTv;
            TextView descTv;
            TextView changeTv;
        }
    }
}
