package com.fuexpress.kr.ui.activity.order_detail;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.OrderConstants;
import com.fuexpress.kr.bean.SalesOrderBean;
import com.fuexpress.kr.bean.SalesOrderItemBean;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.SalesOrderManager;
import com.fuexpress.kr.net.RequestNetListener;
import com.fuexpress.kr.ui.activity.my_order.OrderAll;
import com.fuexpress.kr.ui.adapter.SalesOrderDetailAdapter;
import com.fuexpress.kr.ui.adapter.ShippingAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CrowdProgressDetail;
import com.fuexpress.kr.ui.view.CrowdTimer;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.TimeUtils;
import com.socks.library.KLog;


import java.util.List;

import butterknife.ButterKnife;
import fksproto.CsOrder;


/**
 * Created by k550 on 5/9/2016.
 */
public class OrderDetailCanceledActivity extends BaseActivity {

    private TextView leftTv, centerTv, rigthTv;
    private ImageView leftIv, rightIv;
    public SalesOrderBean mSalesOrderBean;
    private TextView idTv, priceTv, timeTv, countTv;
    private ListView mListView, mShippingListView;
    final String string = UIUtils.getString(R.string.String_price);
    private SalesOrderDetailAdapter mAdapter;
    public TextView editBtn, payMethodTv;
    private List<SalesOrderItemBean> mSalesOrderItemBeans;
    private CrowdProgressDetail mProgressDetail;
    private CrowdTimer mCrowdTimer;
    //海外方式直邮需要委外的信息
    public LinearLayout otherLayout;
    public TextView addressTv, nameTv,
            deliverTv;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.title_tv_left:
                    onBackPressed();
                    break;
                case R.id.title_iv_left:
                    onBackPressed();
                    break;

            }
        }
    };

    @Override
    public View setInitView() {
        View view = View.inflate(this, R.layout.activity_order_detail_canceled, null);
        mSalesOrderBean = (SalesOrderBean) getIntent().getBundleExtra(OrderAll.BEAN).getSerializable(OrderAll.BEAN);

        leftIv = (ImageView) view.findViewById(R.id.title_iv_left);
        rightIv = (ImageView) view.findViewById(R.id.title_iv_right);
        leftTv = (TextView) view.findViewById(R.id.title_tv_left);
        centerTv = (TextView) view.findViewById(R.id.title_tv_center);
        rigthTv = (TextView) view.findViewById(R.id.title_tv_right);
        rightIv.setVisibility(View.GONE);
        rigthTv.setVisibility(View.GONE);
        leftIv.setOnClickListener(onClickListener);
        leftTv.setOnClickListener(onClickListener);
        leftTv.setText(getString(R.string.order_canceled));
        centerTv.setText(getString(R.string.String_canceled));

        mListView = (ListView) view.findViewById(R.id.mListView);
        View footView = View.inflate(this, R.layout.layout_order_payed, null);
        mShippingListView = (ListView) footView.findViewById(R.id.list_shipping);
        otherLayout = (LinearLayout) footView.findViewById(R.id.otherLayout);
        addressTv = (TextView) footView.findViewById(R.id.ad_address_tv);
        nameTv = (TextView) footView.findViewById(R.id.ad_name_tv);
        deliverTv = (TextView) footView.findViewById(R.id.deliverTv);
        payMethodTv = (TextView) footView.findViewById(R.id.payment_type_tv);
        payMethodTv.setText("?????");
        mListView.addFooterView(footView);
        addCrowdView();
        getSalesDetail();
        return view;
    }

    public void getSalesDetail() {
        SalesOrderManager.getSalesOrderDetail(mSalesOrderBean.order_id, new RequestNetListener<CsOrder.GetSalesOrderDetailResponse>() {
            @Override
            public void onSuccess(CsOrder.GetSalesOrderDetailResponse response) {
                KLog.i("getSalesDetail", response.toString());
                mSalesOrderItemBeans = ClassUtil.conventSalesOrderItemList2BeanList(response.getOrderItemsList());
                mAdapter = new SalesOrderDetailAdapter(OrderDetailCanceledActivity.this, mSalesOrderItemBeans);
                mListView.setAdapter(mAdapter);
                String price = UIUtils.getCurrency(myActivity(), response.getOrder().getCurrencycode(), response.getOrder().getGrandTotal());
                priceTv.setText(getString(R.string.grand_total) + price);
                idTv.setText(getString(R.string.order_no) + response.getOrder().getOrderNumber());
                countTv.setText(getString(R.string.item_number) + response.getOrderItemsCount());
                timeTv.setText(getString(R.string.order_date) + TimeUtils.getDateStyle(response.getOrder().getCreateTime()));
                int itemOrderCount = 0;
                for (SalesOrderItemBean bean : mSalesOrderItemBeans) {
                    itemOrderCount = itemOrderCount + bean.qty;
                }
                countTv.setText(getString(R.string.item_number) + itemOrderCount);
                if (response.getOrder().getIsCrowd() && mProgressDetail != null) {

                    mProgressDetail.setData(response.getCrowd());
                    mCrowdTimer.initTime(response.getCrowd());
                } else {
                    mProgressDetail.setVisibility(View.GONE);
                    mCrowdTimer.setVisibility(View.GONE);
                }
                showOtherInfo(response);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void showOtherInfo(CsOrder.GetSalesOrderDetailResponse response) {
        if (response.getOrder().getShippingScheme() == 1) {
            otherLayout.setVisibility(View.VISIBLE);
            deliverTv.setText(getString(R.string.send_direct_));
        } else if (response.getOrder().getShippingScheme() == 2) {
            deliverTv.setText(getString(R.string.String_merge_order));
        }
        float fee = getFee(response.getShippingsList());
        String price = String.format(string, fee);
        //String region = AssetFileManager.getInstance().readFilePlus(response.getAddress().getRegion(), AssetFileManager.ADDRESS_TYPE);
        String region = response.getAddress().getFullRegionName();

        String addressDetail = region + response.getAddress().getStreet();

        nameTv.setText(response.getAddress().getName() + "," + response.getAddress().getPhone());
        addressTv.setText(addressDetail);
        ShippingAdapter shippingAdapter = new ShippingAdapter(OrderDetailCanceledActivity.this, response.getShippingsList(), response.getOrder().getCurrencycode());
        mShippingListView.setAdapter(shippingAdapter);
        setListViewHeightBasedOnChildren(mShippingListView);
        payMethodTv.setText(response.getOrder().getPayMethodStr());
    }

    public void addCrowdView() {
        View mHeadView = View.inflate(this, R.layout.layout_order_crowd_detail, null);
        idTv = (TextView) mHeadView.findViewById(R.id.idTv);
        priceTv = (TextView) mHeadView.findViewById(R.id.priceTv);
        timeTv = (TextView) mHeadView.findViewById(R.id.timeTv);
        countTv = (TextView) mHeadView.findViewById(R.id.countTv);
        editBtn = (TextView) mHeadView.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(onClickListener);
        mCrowdTimer = (CrowdTimer) mHeadView.findViewById(R.id.crowd_timer);
        mProgressDetail = (CrowdProgressDetail) mHeadView.findViewById(R.id.crowd_progress_detail);
        if (!mSalesOrderBean.is_crowd) {
            mCrowdTimer.setVisibility(View.GONE);
            mProgressDetail.setVisibility(View.GONE);
        }
        mListView.addHeaderView(mHeadView);
    }

    private float getFee(List<CsOrder.SalesOrderShipping> list) {
        float fee = 0;
        if (list == null) {
            return 0;
        }
        for (CsOrder.SalesOrderShipping shipping : list) {
            if (shipping != null) {
                fee = fee + shipping.getShippingFee();
            }
        }
        return fee;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeght()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_TEXT_TRANSLATE_SUCCESS:
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
