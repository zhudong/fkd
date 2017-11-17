package com.fuexpress.kr.ui.activity.crowd;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ShareManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.ItemDetailActivity;
import com.fuexpress.kr.ui.activity.ItemMoreInfoActivity;
import com.fuexpress.kr.ui.adapter.CrowdItemAdapter;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CrowdProgressDetail;
import com.fuexpress.kr.ui.view.CrowdTimer;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsBase;
import fksproto.CsCrowd;


public class CrowdDetailActivity extends BaseActivity implements RefreshListView.OnRefreshListener {

    private static final String TAG = "CrowdDetailActivity";
    public static String CROWD_ID = "crowd_id";
    private View mRootView;
    private CsCrowd.Crowd mCrowd;
    private CrowdProgressDetail mProgressDetail;
    private TextView mCrowdDes;
    private TextView mCrowdName;
    private ImageView mMCover;

    private static Handler mHandler = new Handler();
    private RefreshListView mListView;
    private TextView mAttendCount;
    private LinearLayout mCrowderContainer;
    private CrowdTimer mTimerView;
    private View mRight;
    private View mBack;
    private CsBase.Item mItem;
    private TextView mTvInvita;
    private List<CsBase.Item> itemsList;
    private List<CsBase.PairIntFloat> discountsList;
    private CrowdItemAdapter adapter;
    private int mPageNO = 1;
    private ArrayMap<Long,Float> finaldiscountList;
    private long mCrowdId = 2902;
    private CsCrowd.GetCrowdDetailResponse mResponse;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_crowd_detail, null);
        mRootView.findViewById(R.id.tv_go_crowd_detail).setOnClickListener(this);
        View mHeadView = View.inflate(this, R.layout.head_crowd_detail, null);
        mMCover = (ImageView) mHeadView.findViewById(R.id.img_crowd_cover);
        mCrowdName = (TextView) mHeadView.findViewById(R.id.tv_crowd_item_name);
        mCrowdDes = (TextView) mHeadView.findViewById(R.id.tv_crowd_iten_des);
        mAttendCount = (TextView) mHeadView.findViewById(R.id.tv_attend_count);
        mProgressDetail = (CrowdProgressDetail) mHeadView.findViewById(R.id.crowd_progress_detail);
        mTimerView = (CrowdTimer) mHeadView.findViewById(R.id.crowd_timer);
        mCrowderContainer = (LinearLayout) mHeadView.findViewById(R.id.ll_crowder_container);
        mTvInvita = (TextView) mHeadView.findViewById(R.id.tv_invita);

        initTitle();

        mListView = (RefreshListView) mRootView.findViewById(R.id.lv_crowd_item_body);
        mListView.setOnRefreshListener(this);
        mListView.addHeaderView(mHeadView);
        initEvent();
        initData();
        return mRootView;
    }

    private void initEvent() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu();
            }
        });

        mTvInvita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_go_crowd_detail:
                if ("".equals(mResponse.getSelectImage()) | "0".equals(mResponse.getSelectImage())) {
                    String url = Constants.WebWiewUrl.getUrl(Constants.WebWiewUrl.CROWD_DETAIL) + mCrowdId + "?decorator=empty" + "&localeCode=" + AccountManager.getInstance().getLocaleCode();
                    intent = new Intent(this, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailActivity.URL, url);
                    intent.putExtra(ItemDetailActivity.TAG, true);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, ItemMoreInfoActivity.class);
                    intent.putExtra(ItemMoreInfoActivity.URL, mResponse.getImageUrl());
                    startActivity(intent);
                }
                break;
        }
    }

    private void initTitle() {
//        TitleBarView TitleBarView = new TitleBarView(mRootView);
//        RelativeLayout commonTitle = TitleBarView.getCommonTitle();
        mBack = mRootView.findViewById(R.id.title_iv_left);
        mRight = mRootView.findViewById(R.id.title_iv_right);
        ((TextView) mRootView.findViewById(R.id.title_tv_center)).setText(getResources().getString(R.string.String_crowd_detail_title));
        mRight.setVisibility(View.VISIBLE);
    }

    private void initData() {
        Intent intent = getIntent();
        mCrowdId = intent.getLongExtra(CROWD_ID, 0);
        itemsList = new ArrayList<>();
        discountsList = new ArrayList<>();
        finaldiscountList = new ArrayMap<Long, Float>();
        getCrowdDetail(mPageNO);
    }

    private void initCrowInfo() {
        String uri = mCrowd.getLogo() + Constants.ImgUrlSuffix.mob_list;
        ImageLoader.getInstance().displayImage(uri, mMCover, ImageLoaderHelper.getInstance(this).getDisplayOptions());

        mCrowdName.setText(mCrowd.getTitle());
        mCrowdDes.setText(Html.fromHtml(mCrowd.getName()));
        mProgressDetail.setData(mCrowd);

        mTimerView.initTime(mCrowd);
    }


    private void getCrowdDetail(final int index) {
        CsCrowd.GetCrowdDetailRequest.Builder builder = CsCrowd.GetCrowdDetailRequest.newBuilder();
        AccountManager instance = AccountManager.getInstance();
        builder.setCrowdId(mCrowdId).setPageno(index).setUserinfo(instance.getBaseUserRequest())
                .setCurrencycode(instance.getCurrencyCode())
                .setCurrencyid(instance.getCurrencyId())
                .setLocalecode(instance.getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCrowd.GetCrowdDetailResponse>() {

            @Override
            public void onSuccess(final CsCrowd.GetCrowdDetailResponse response) {
                mResponse = response;
                mCrowd = mResponse.getCrowd();
              /*  itemsList = response.getItemsList();
                discountsList = response.getDiscountsList();*/
                if (index == 1) {
                    itemsList.clear();
                    discountsList.clear();
                }
                itemsList.addAll(response.getItemsList());
                discountsList.addAll(response.getDiscountsList());

                if (itemsList.size() > 0) {
                    mItem = itemsList.get(0);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initCrowInfo();
                        initAdapter(itemsList, discountsList);
                        String attendCount = CrowdDetailActivity.this.getResources().getString(R.string.String_has_attend_count);
                        mAttendCount.setText(String.format(attendCount, response.getUsersTotal()));
//                        showUsers(response.getUsersList());去掉邀请用户
                        mListView.stopRefresh();
                        mListView.stopLoadMore(true);
                        mListView.setHasLoadMore(response.getMore());
                    }
                });
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                Log.d(TAG, errMsg + ret);
            }
        });
    }

    private void initAdapter(List<CsBase.Item> itemsList, List<CsBase.PairIntFloat> discountsList) {
        finaldiscountList.clear();

        for (CsBase.PairIntFloat pairIntFloat : discountsList) {
               /* if (pairIntFloat.getK() == item.getItemid()) {
                    float discount = pairIntFloat.getV();
                    finaldiscountList.add(discount);
                }*/

            finaldiscountList.put(pairIntFloat.getK(),pairIntFloat.getV());
        }

        if (adapter == null) {
            adapter = new CrowdItemAdapter(this, itemsList, finaldiscountList);
            mListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    private void showUsers(final List<CsBase.UserInfo> users) {
        int width = UIUtils.dip2px((float) 46);
        int marginLeft = UIUtils.dip2px((float) 8);
        int padding = UIUtils.dip2px((float) 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        for (int i = 0; i < users.size(); i++) {
            SelectableRoundedImageView img = new SelectableRoundedImageView(this);
            img.setCornerRadiiDP(2, 2, 2, 2);
            img.setPadding(padding, padding, padding, padding);
            img.setCropToPadding(true);
            img.setBackground(this.getResources().getDrawable(R.drawable.shape_icon_stroke));
            img.setImageDrawable(this.getResources().getDrawable(R.color.home_add_and_like_bg));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.setMargins(marginLeft, 0, 0, 0);
            ImageLoader.getInstance().displayImage(users.get(i).getAvatar() + Constants.ImgUrlSuffix.small_9, img, ImageLoaderHelper.getInstance(this).getDisplayOptionsIcon());
            mCrowderContainer.addView(img, params);
            final CsBase.UserInfo userInfo = users.get(i);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toMeActivity(userInfo);
                }
            });
        }
    }


    private void toMeActivity(CsBase.UserInfo user) {
       /* UserInfoBean userInfoBean = ClassUtil.conventUser2UserInfoBean(user);
        Intent intent = new Intent(this, PersonInfoActivity.class);
        intent.putExtra(MeActivity.USER_INFO_BEAN, userInfoBean.uin);
        intent.putExtra(MeActivity.STATE, 0);
        startActivity(intent);*/
    }

    @Override
    protected View getAnchorView() {
        return mRight;
    }

    @Override
    protected void share() {
        ArrayList<CsBase.ItemImage> urls = new ArrayList<>();
        urls.add(CsBase.ItemImage.newBuilder().setImageUrl(mCrowd.getLogo()).build());
       /* String url = ShareUtil.getShareCrowdDetail().replace("1%", mCrowd.getCrowdId() + "") + Constants.WebWiewUrl.SHARE_URL_SUFFIX + AccountManager.getInstance().mInviteKey;
        ShareBean bean = new ShareBean(1, mCrowd.getTitle(), mItem.getImageUrl(), url, urls, null, ShareActivity.CategoryShare.CROWD);
        shareDetail(0, bean);*/
        ShareManager.initWithRes(urls, mCrowd.getName(), this);

    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.GO_CROWD_Detail) {
            mListView.autoRefresh();
        }
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mPageNO = 1;
        mListView.setHasLoadMore(true);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCrowdDetail(mPageNO);
            }
        }, 200);
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        mPageNO++;
        getCrowdDetail(mPageNO);
    }
}
