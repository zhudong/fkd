package com.fuexpress.kr.ui.activity.giftcard_order;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.activity.CardOrderDetailActivity;
import com.fuexpress.kr.ui.adapter.FillingOrderListAdapter;
import com.fuexpress.kr.ui.view.NoteRadioGroup;
import com.fuexpress.kr.ui.view.RefreshListView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsCard;

/**
 * Created by Longer on 2017/5/24.
 */
public class FragmentForGiftCard01 extends BaseFragment<MyGiftCardOrderActivity> implements RefreshListView.OnRefreshListener, AdapterView.OnItemClickListener {

    private View mRootVew;
    private RadioButton mRb_wait_pay, mRb_payed, mRb_cancled, mRb_all_order;
    private NoteRadioGroup mRbg_my_gift_card_list_fragment_01;
    private RefreshListView mRfl_in_gift_card_order_list;
    private FillingOrderListAdapter mMyGiftCardOrderListAdapter;
    private MyGiftCardOrderActivity mMyGiftCardOrderActivity;
    private int getListStauts = 1;
    private boolean mIsHasMore;
    private int pageNum = 1;
    private int requestType = 0;
    private TextView mTv_red_point;

    public FragmentForGiftCard01(int type) {
        requestType = type;
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        mRootVew = View.inflate(SysApplication.getContext(), R.layout.frgment_01_for_my_gift_card_order_list, null);
        mRb_wait_pay = (RadioButton) mRootVew.findViewById(R.id.rb_wait_pay);
        mRb_payed = (RadioButton) mRootVew.findViewById(R.id.rb_payed);
        mRb_cancled = (RadioButton) mRootVew.findViewById(R.id.rb_cancled);
        mRb_all_order = (RadioButton) mRootVew.findViewById(R.id.rb_all_order);
        mTv_red_point = (TextView) mRootVew.findViewById(R.id.tv_red_point);
        mRbg_my_gift_card_list_fragment_01 = (NoteRadioGroup) mRootVew
                .findViewById(R.id.rbg_my_gift_card_list_fragment_01);
        mRbg_my_gift_card_list_fragment_01.check(R.id.rb_wait_pay);
        mMyGiftCardOrderActivity = (MyGiftCardOrderActivity) getActivity();
        mRfl_in_gift_card_order_list = (RefreshListView) mRootVew.findViewById(R.id.rfl_in_gift_card_order_list);
        mRfl_in_gift_card_order_list.setHeadViewTextColor(Color.BLACK);
        return mRootVew;
    }

    @Override
    public void initData() {
        mMyGiftCardOrderActivity.showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, null);
        initDataFromNet(0);
    }

    public void initDataFromNet(int status) {
        if (0 == status) {
            pageNum = 1;
        } else if (1 == status) {
            if (mIsHasMore) {
                pageNum = pageNum + 1;
            }
        }
        GiftCardOrderManager.getInstance().getGiftCardOrderListRequest(requestType, getListStauts, pageNum, status);
    }


    @Override
    public void initListener() {
        mRfl_in_gift_card_order_list.setOnRefreshListener(this);
        mRfl_in_gift_card_order_list.setOnItemClickListener(this);
        mRbg_my_gift_card_list_fragment_01.setOnCheckedChangeListener(new NoteRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(NoteRadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_wait_pay:
                        getListStauts = 1;
                        break;
                    case R.id.rb_payed:
                        getListStauts = 2;
                        break;
                    case R.id.rb_cancled:
                        getListStauts = 3;
                        break;
                    case R.id.rb_all_order:
                        getListStauts = 0;
                        break;
                }
                initDataFromNet(0);
            }
        });
    }


    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_GIFT_CARD_LIST_REQUEST_COMPLETE:
                boolean isSuccess = event.getBooleanParam();
                if (isSuccess) {
                    List<CsCard.GiftCardOrderItem> giftcarditemsList;
                    if (1 == requestType) {
                        /*if (1 == getListStauts) {
                            giftcarditemsList = GiftCardOrderManager.getInstance().mGiftcarditemsList01_Tab_01;
                        } else {

                            giftcarditemsList = GiftCardOrderManager.getInstance().mGiftcarditemsList01;
                        }*/
                        giftcarditemsList = GiftCardOrderManager.getInstance().mGiftcarditemsList01;
                    } else {
                        /*if (1 == getListStauts) {
                            giftcarditemsList = GiftCardOrderManager.getInstance().mGiftcarditemsList02_Tab_01;
                        } else {

                            giftcarditemsList = GiftCardOrderManager.getInstance().mGiftcarditemsList02;
                        }*/
                        giftcarditemsList = GiftCardOrderManager.getInstance().mGiftcarditemsList02;
                    }
                    int status = event.getIntValue();
                    mRfl_in_gift_card_order_list.stopLoadMore(true);
                    mRfl_in_gift_card_order_list.stopRefresh();
                    if (0 == status) {
                        mMyGiftCardOrderListAdapter = new FillingOrderListAdapter(giftcarditemsList,mContext);
                        mRfl_in_gift_card_order_list.setAdapter(mMyGiftCardOrderListAdapter);
                        //mIsHasMore = event.getBooleanParam02();
                    } else if (1 == status) {
                        mMyGiftCardOrderListAdapter.notifyDataSetChanged();
                    }
                    mIsHasMore = event.getBooleanParam02();
                    if (mIsHasMore) {
                        mRfl_in_gift_card_order_list.setHasLoadMore(true);
                    } else {
                        mRfl_in_gift_card_order_list.setHasLoadMore(false);
                    }
                    if (getListStauts == 1) {
                        int oderNum = 0;
                        if (1 == requestType) {
                            oderNum = GiftCardOrderManager.getInstance().mPendingOrderNum_01;
                        } else {
                            oderNum = GiftCardOrderManager.getInstance().mPendingOrderNum_02;
                        }
                        if (oderNum > 0) {
                            mTv_red_point.setVisibility(View.VISIBLE);
                            mTv_red_point.setText(String.valueOf(oderNum));
                        } else {
                            mTv_red_point.setVisibility(View.GONE);
                        }

                    }
                    mMyGiftCardOrderActivity.dissMissProgressDiaLog(200);
                } else {
                    mMyGiftCardOrderActivity.showProgressDiaLog(SweetAlertDialog.ERROR_TYPE, "请求数据失败");
                    mMyGiftCardOrderActivity.dissMissProgressDiaLog(1000);
                }
                break;
        }
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        initData();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (!mIsHasMore) {
            return;
        }
        initDataFromNet(1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CsCard.GiftCardOrderItem giftCardOrderItem;
        if (1 == requestType) {
            giftCardOrderItem = GiftCardOrderManager.getInstance().mGiftcarditemsList01.get(position - 1);
        } else {
            giftCardOrderItem = GiftCardOrderManager.getInstance().mGiftcarditemsList02.get(position - 1);
        }
        int giftCardOrderId = (int) giftCardOrderItem.getGiftCardOrderId();
        int orderType = giftCardOrderItem.getOrderType();
        int status = giftCardOrderItem.getStatus();
        Intent intent = new Intent(mMyGiftCardOrderActivity, CardOrderDetailActivity.class);
        intent.putExtra(Constants.GIFT_CARD_ORDER_ID, giftCardOrderId);
        intent.putExtra(CardOrderDetailActivity.CURRENCY_CODE, giftCardOrderItem.getCurrencycode());
        intent.putExtra(CardOrderDetailActivity.CURRENCY_SGIN, giftCardOrderItem.getCurrencysign());
        if (1 == requestType) {
            intent.putExtra(Constants.GIFT_CARD_ORDER_TYPE, Constants.GIFT_CARD_ORDER_TYPE_RECHARGE);
        } else if (2 == requestType) {
            intent.putExtra(Constants.GIFT_CARD_ORDER_TYPE, Constants.GIFT_CARD_ORDER_TYPE_PREPAID);
        }

        // 0:充值卡订单-全部;  1: 充值卡订单-待付款; 2: 充值卡订单-已支付;  3: 充值卡订单-已取消
        if (1 == status) {
            intent.putExtra(Constants.GIFT_CARD_ORDER_STATE, Constants.GIFT_CARD_ORDER_STATE_PENDING);
        } else if (2 == status) {
            intent.putExtra(Constants.GIFT_CARD_ORDER_STATE, Constants.GIFT_CARD_ORDER_STATE_PAID);
        } else if (3 == status) {
            intent.putExtra(Constants.GIFT_CARD_ORDER_STATE, Constants.GIFT_CARD_ORDER_STATE_CANCELED);
        }
        mMyGiftCardOrderActivity.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDataFromNet(0);
    }
}
