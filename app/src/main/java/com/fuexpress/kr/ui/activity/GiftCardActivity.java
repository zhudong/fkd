package com.fuexpress.kr.ui.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.FlowLayout;
import com.fuexpress.kr.ui.view.NumberCounter2;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.SPUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fksproto.CsCard;


/**
 * Created by Administrator on 2016/5/3.
 */
public class GiftCardActivity extends BaseActivity {

//    private static final int[] GIFT_CARD_FACE_VALUES = {100, 200, 500, 1000};
    private static int[] GIFT_CARD_FACE_VALUES;
    private static final int[] GIFT_CARD_RESOURCES = {R.mipmap.card_small};
    private static final int[] GIFT_CARD_IDS = {68, 18, 19};
    private View rootView;
    private FlowLayout giftCardLayout;
    private FlowLayout faceValueLayout;
    private LinearLayout mLlComfirmInfo;
    private FrameLayout rootFrameLayout;
    private RelativeLayout giftCardTopRl;
    private RelativeLayout countLayout;
    private TextView toMeTv;
    private TextView titleTv;
    private TextView descTv;
    private ViewPager cardViewPager;
    private EditText addresseeNameEt;
    private EditText mailEt;
    private EditText senderNameEt;
    private EditText messageEt;
    private Button addToCartBtn;
    private Button confirmBtn;
    private TextView countTxt;
    private NumberCounter2 numberCount;
    private int giftCardNumber;
    private ImageView backIv;

    public boolean mIsOpenButtom;
    private boolean mIsOperaing = false;
    private int[] imageIds;
    private ImageView[] imageViews;
    private int cardPosition;
    private int valuePosition;
    private List<CsCard.GiftCardTemplateList> cardList;
    private List<CsCard.GiftCardValueList> cardValueLists;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_gift_card, null);
        TitleBarView titleBar = (TitleBarView) rootView.findViewById(R.id.gift_card_titlebar);
        titleBar.setTitleText(getString(R.string.String_buy_value_card));
        toMeTv = titleBar.getmTv_in_title_back_tv();
        toMeTv.setText(getString(R.string.main_me_tab_string));
        backIv = titleBar.getIv_in_title_back();

        titleTv = (TextView) rootView.findViewById(R.id.gift_card_title);
        descTv = (TextView) rootView.findViewById(R.id.gift_card_desc);
        mLlComfirmInfo = (LinearLayout) rootView.findViewById(R.id.gift_card_ll);
        giftCardLayout = (FlowLayout) rootView.findViewById(R.id.gift_card_flowLayout);
        faceValueLayout = (FlowLayout) rootView.findViewById(R.id.gift_card_face_value_flowLayout);
        rootFrameLayout = (FrameLayout) rootView.findViewById(R.id.gift_card_root_framelayout);
        giftCardTopRl = (RelativeLayout) rootView.findViewById(R.id.gift_card_top_rl);
        cardViewPager = (ViewPager) rootView.findViewById(R.id.gift_card_vp);
        addresseeNameEt = (EditText) rootView.findViewById(R.id.gift_card_addressee_name_et);
        mailEt = (EditText) rootView.findViewById(R.id.gift_card_mail_et);
        senderNameEt = (EditText) rootView.findViewById(R.id.gift_card_sender_name_et);
        messageEt = (EditText) rootView.findViewById(R.id.gift_card_message_limit_et);
        addToCartBtn = (Button) rootView.findViewById(R.id.gift_card_add_to_cart_btn);
        confirmBtn = (Button) rootView.findViewById(R.id.gift_card_confirm_btn);
        countTxt = (TextView) rootView.findViewById(R.id.gift_card_count_tv);
        countLayout = (RelativeLayout) rootView.findViewById(R.id.gift_card_count_layout);
        numberCount = (NumberCounter2) rootView.findViewById(R.id.gift_card_numberCount);
        numberCount.init(1, 9);
        numberCount.mNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberCount.setOnCounterClickListener(new NumberCounter2.OnCounterClickListener() {
            @Override
            public void onPlusClick(NumberCounter2 view) {

            }

            @Override
            public void onMinusClick(NumberCounter2 view) {

            }
        });
        initViewPager();
        initGiftCard();
//        initFaceValueLayout();

        SharedPreferences accountShare = getSharedPreferences("accountShare", 101);
        String email = SPUtils.getString(this, "emailStr", "");
        mailEt.setText(email);
        countLayout.setOnClickListener(this);
        giftCardTopRl.setOnClickListener(this);
        toMeTv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        addToCartBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gift_card_count_layout:
                toActivity(ShopCartForCardActivity.class);
                break;
            case R.id.gift_card_top_rl:
                closeBottomLayout();
                break;
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
            case R.id.gift_card_add_to_cart_btn:
                if(TextUtils.isEmpty(mailEt.getText().toString())){
                    Toast.makeText(this, getString(R.string.gift_card_email_input_msg2), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isEmail(mailEt.getText().toString())) {
                    if (addresseeNameEt != null && mailEt != null && senderNameEt != null && messageEt != null) {
                        Hidekeyboard(addresseeNameEt);
                        Hidekeyboard(mailEt);
                        Hidekeyboard(senderNameEt);
                        Hidekeyboard(messageEt);
                    }
                    switchBottomState();
                } else {
                    Toast.makeText(this, getString(R.string.gift_card_email_input_msg), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.gift_card_confirm_btn:
                if (giftCardNumber == 20) {
                    Toast.makeText(this, getString(R.string.gift_card_add_cart_toast_msg), Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    addToGiftCardCart();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartInfoNum();
    }

    public static boolean isEmail(String strEmail) {
//        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        String strPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    @Override
    public void onBackPressed() {
        closeLoading();
        LogUtils.d("-------------onBackPressed");

        super.onBackPressed();
    }

    public void toActivity(Class c) {
        Intent intent = new Intent();
        intent.setClass(this, c);
        startActivity(intent);
    }

    public void initGiftCard() {

        for (int i = 0; i < GIFT_CARD_RESOURCES.length; i++) {
            final ImageView img = new ImageView(GiftCardActivity.this);
            img.setBackground(null);
            if (i == cardPosition) {
//                    cardPosition = 0;
                img.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.gift_card_bg));
            }
            int pixelSize = UIUtils.px2dip(400);
//                imageLoader.displayImage(cardList.get(i).getEmailTemplate(), img, options);
            img.setImageResource(GIFT_CARD_RESOURCES[i]);
            giftCardLayout.setHorizontalSpacing(UIUtils.dip2px(15));
            giftCardLayout.setVerticalSpacing(UIUtils.dip2px(15));
            giftCardLayout.addView(img, new FlowLayout.LayoutParams(pixelSize, pixelSize));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < giftCardLayout.getChildCount(); i++) {
                        ImageView imageView = (ImageView) giftCardLayout.getChildAt(i);
                        if (v == imageView) {
                            cardPosition = i;
                                cardViewPager.setCurrentItem(cardPosition);
                            imageView.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.gift_card_bg));
                        } else {
                            imageView.setBackground(null);
                        }
                    }
                }
            });
        }

        showLoading();
        CsCard.InitGiftCardRequest.Builder builder = CsCard.InitGiftCardRequest.newBuilder();
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setLocalecode(AccountManager.getInstance().getLocaleCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.InitGiftCardReponse>() {

            @Override
            public void onSuccess(CsCard.InitGiftCardReponse response) {
                closeLoading();
                LogUtils.d(response.toString());
                Message msg = Message.obtain();
                msg.obj = response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
                LogUtils.d(errMsg + " ret:" + ret);
            }
        });


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CsCard.InitGiftCardReponse response = (CsCard.InitGiftCardReponse) msg.obj;
            titleTv.setText(response.getTitle());
            descTv.setText(response.getDesc());
            cardValueLists = response.getGiftCardValueListList();
            if(cardValueLists == null || cardValueLists.size() == 0){
                confirmBtn.setEnabled(false);
                return;
            }
            GIFT_CARD_FACE_VALUES = new int[cardValueLists.size()];
            for (int i = 0; i < cardValueLists.size(); i++) {
                GIFT_CARD_FACE_VALUES[i] = Integer.valueOf(cardValueLists.get(i).getValue());
            }
            initFaceValueLayout();
//            cardList = response.getGiftcardtemplatelistsList();
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            DisplayImageOptions options = ImageLoaderHelper.getInstance(GiftCardActivity.this).getDisplayOptions();
//            for (int i = 0; i < cardList.size(); i++) {
//                final ImageView img = new ImageView(GiftCardActivity.this);
//                img.setBackground(null);
//                if (i == cardPosition) {
////                    cardPosition = 0;
//                    img.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.gift_card_bg));
//                }
//                int pixelSize = UIUtils.px2dip(400);
//                imageLoader.displayImage(cardList.get(i).getEmailTemplate(), img, options);
////                img.setImageResource(GIFT_CARD_RESOURCES[i]);
//                giftCardLayout.setHorizontalSpacing(UIUtils.dip2px(15));
//                giftCardLayout.setVerticalSpacing(UIUtils.dip2px(15));
//                giftCardLayout.addView(img, new FlowLayout.LayoutParams(pixelSize, pixelSize));
//                img.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        for (int i = 0; i < giftCardLayout.getChildCount(); i++) {
//                            ImageView imageView = (ImageView) giftCardLayout.getChildAt(i);
//                            if (v == imageView) {
//                                cardPosition = i;
//                                cardViewPager.setCurrentItem(cardViewPager.getCurrentItem() + i);
//                                imageView.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.gift_card_bg));
//                            } else {
//                                imageView.setBackground(null);
//                            }
//                        }
//                    }
//                });
//            }
        }
    };

    public void addToGiftCardCart() {
        showLoading(5000);
        CsCard.AddToGiftCardCartRequest.Builder builder = CsCard.AddToGiftCardCartRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        builder.setEmailtemplate(GIFT_CARD_IDS[cardPosition]);
        builder.setPrice(GIFT_CARD_FACE_VALUES[valuePosition]);
        builder.setNum(numberCount.getCurrentNumber());
        builder.setToname(addresseeNameEt.getText().toString());
        builder.setToemail(mailEt.getText().toString());
        builder.setFromname(senderNameEt.getText().toString());
        builder.setNote(messageEt.getText().toString());
        builder.setCurrencycode(AccountManager.getInstance().getCurrencyCode());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.AddToGiftCardCartResponse>() {

            @Override
            public void onSuccess(CsCard.AddToGiftCardCartResponse response) {
                LogUtils.d(response.toString());
                closeLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeBottomLayout();
                    }
                });
                getCartInfoNum();
                SPUtils.putString(GiftCardActivity.this, "emailStr", mailEt.getText().toString());
                toActivity(ShopCartForCardActivity.class);
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                closeLoading();
            }
        });
    }

    public void getCartInfoNum() {
        CsCard.GetCartInfoNumRequest.Builder builder = CsCard.GetCartInfoNumRequest.newBuilder();
        builder.setCurrencyCode(AccountManager.getInstance().getCurrencyCode());
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsCard.GetCartInfoNumResponse>() {

            @Override
            public void onSuccess(final CsCard.GetCartInfoNumResponse response) {
                LogUtils.d(response.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        giftCardNumber = response.getGiftNum();
                        if (giftCardNumber > 0) {
                            countTxt.setText(giftCardNumber + "");
                            countTxt.setVisibility(View.VISIBLE);
                        } else {
                            countTxt.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.makeText(GiftCardActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        countTxt.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public void initFaceValueLayout() {
//        valuePosition = 0;
        if(GIFT_CARD_FACE_VALUES.length == 0){
            confirmBtn.setEnabled(false);
            return;
        }
        for (int i = 0; i < GIFT_CARD_FACE_VALUES.length; i++) {
            Button valueBtn = new Button(this);
            valueBtn.setText(GIFT_CARD_FACE_VALUES[i] + "");
            valueBtn.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
            valueBtn.setTextSize(UIUtils.px2dip(39));
            valueBtn.setPadding(0, 0, 0, 0);
            valueBtn.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.shape_for_price_normal));
            if (i == 0) {
                valuePosition = 0;
                valueBtn.setBackground((ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.shape_for_price_selected)));
                valueBtn.setTextColor(Color.RED);
            }
            int width = UIUtils.px2dip(500);
            int height = UIUtils.px2dip(220);
            faceValueLayout.setHorizontalSpacing(UIUtils.dip2px(15));
            faceValueLayout.setVerticalSpacing(UIUtils.dip2px(15));
            faceValueLayout.addView(valueBtn, new FlowLayout.LayoutParams(width, height));
            valueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < faceValueLayout.getChildCount(); i++) {
                        Button btn = (Button) faceValueLayout.getChildAt(i);
                        if (v == btn) {
                            valuePosition = i;
                            btn.setBackground((ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.shape_for_price_selected)));
                            btn.setTextColor(Color.RED);
                        } else {
                            btn.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.shape_for_price_normal));
                            btn.setTextColor(ContextCompat.getColor(GiftCardActivity.this, R.color.text_gray));
                        }
                    }
                }
            });

        }
    }

    public void initViewPager() {
//        imageIds = new int[]{R.mipmap.big_card1, R.mipmap.big_card2, R.mipmap.big_card3};
        imageIds = new int[]{R.mipmap.card};
        imageViews = new ImageView[imageIds.length];
        for (int i = 0; i < imageViews.length; i++) {
            ImageView imageView = new ImageView(this);
            imageViews[i] = imageView;
            imageView.setImageResource(imageIds[i]);
        }

        MyAdapter mAdapter = new MyAdapter(imageViews);
        cardViewPager.setAdapter(mAdapter);
//        cardViewPager.setCurrentItem(imageViews.length * 100);
        cardViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                cardViewPager.setCurrentItem(position);
                LogUtils.d("------------------" + position % imageViews.length);
                cardPosition = position;
                for (int i = 0; i < giftCardLayout.getChildCount(); i++) {
                    ImageView imgView = (ImageView) giftCardLayout.getChildAt(i);

                    if (i == cardPosition) {
                        imgView.setBackground(ContextCompat.getDrawable(GiftCardActivity.this, R.drawable.gift_card_bg));
                    } else {
                        imgView.setBackground(null);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    class MyAdapter extends PagerAdapter {
        private ImageView[] imgViews;

        public MyAdapter(ImageView[] imageViews) {
            this.imgViews = imageViews;
        }

        @Override
        public int getCount() {
            return imgViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //((ViewPager)container).removeView(imgViews[position % imgViews.length]);
            container.removeView(imgViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                ((ViewPager) container).addView(imgViews[position], 0);
            } catch (Exception e) {
                //handler something
            }
            return imgViews[position];

        }
    }

    public void closeBottomLayout() {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        startAnima(height, 0);
        mIsOpenButtom = false;
    }

    public void switchBottomState() {
        int height = getWindowManager().getDefaultDisplay().getHeight();
        if (!mIsOpenButtom && !mIsOperaing) {
//            mScItemContent.smoothScrollTo(0, 0);
            startAnima(0, height);
            mIsOpenButtom = true;
        } else if (mIsOpenButtom && !mIsOperaing) {
            startAnima(height, 0);
            mIsOpenButtom = false;
        }
    }

    public void startAnima(int fromWhere, int toPlace) {
        ValueAnimator animator = ValueAnimator.ofInt(fromWhere, toPlace);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLlComfirmInfo.getLayoutParams();
                params.height = height;
                rootFrameLayout.updateViewLayout(mLlComfirmInfo, params);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsOperaing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsOperaing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsOperaing = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
}
