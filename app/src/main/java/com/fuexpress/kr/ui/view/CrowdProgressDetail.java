package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.LgUitl;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsCrowd;


/**
 * Created by yuan on 2016-4-6.
 */
public class CrowdProgressDetail extends RelativeLayout {
    private float mProgressHeight;
    private float mButtomSpace;
    private int mTextSize;
    private TextView mTvStartPersen;
    private TextView mTvTarget;
    private TextView mTvStartDisCount;
    private TextView mTvMiddleDisCount;
    private TextView mTvEndDisCount;
    private CrowdProgressBar mProgressBar;
    private Context mContext;
    private float mPrice;
    private ItemBean mItem;
    private String mStirngPrice;
    private boolean mShowIndicator;
    private float mVerSpace;
    private RelativeLayout mRoot;
    private RelativeLayout mRoot2;
    private boolean mHintMiddle;
    private List<Float> mPricesList;
    private CsCrowd.Crowd mCrowd;

    public CrowdProgressDetail(Context context) {
        this(context, null);
    }

    public CrowdProgressDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPricesList = new ArrayList<>();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CrowdProgress);
        mShowIndicator = ta.getBoolean(R.styleable.CrowdProgress_showIndicator, true);
        mTextSize = ta.getInteger(R.styleable.CrowdProgress_size, 13);
        mVerSpace = ta.getFloat(R.styleable.CrowdProgress_vertical_space, 6);
        mButtomSpace = ta.getFloat(R.styleable.CrowdProgress_buttom_space, 12);
        mProgressHeight = ta.getFloat(R.styleable.CrowdProgress_progressHeight, 0);
        mHintMiddle = ta.getBoolean(R.styleable.CrowdProgress_CrowdProgress_hintMiddle, false);
//        ta.getFloat(R.styleable.CrowdProgress_prgressHeight,)
        ta.recycle();
        initView();
    }

    private void initView() {
        mRoot = (RelativeLayout) View.inflate(getContext(), R.layout.view_crowd_progress_detail, this);
        mRoot2 = (RelativeLayout) mRoot.findViewById(R.id.rl_root);
        mTvStartPersen = (TextView) mRoot.findViewById(R.id.tv_start_percent);
        mTvTarget = (TextView) mRoot.findViewById(R.id.tv_target);
        mTvStartDisCount = (TextView) mRoot.findViewById(
                R.id.tv_start_discount);
        mTvMiddleDisCount = (TextView) mRoot.findViewById(R.id.tv_middle_discount);
        mTvEndDisCount = (TextView) mRoot.findViewById(R.id.tv_end_discount);
        mProgressBar = (CrowdProgressBar) mRoot.findViewById(R.id.progress_crowd);
        mProgressBar.setProgressMax(100);
        mContext = getContext();
        mStirngPrice = mContext.getResources().getString(R.string.String_price);
        mProgressBar.showIndicator(mShowIndicator);
        setTextSize();
        mProgressBar.hideMiddle(mHintMiddle);
        if (mHintMiddle) {
            mTvMiddleDisCount.setVisibility(GONE);
        } else {
            mTvMiddleDisCount.setVisibility(VISIBLE);
        }
    }

    private void setTextSize() {
        if (mTextSize != 13) {
            int size = mTextSize;
            mTvStartPersen.setTextSize(size);
            mTvTarget.setTextSize(size);
            mTvStartDisCount.setTextSize(size);
            mTvMiddleDisCount.setTextSize(size);
            mTvEndDisCount.setTextSize(size);
        }

        if (mVerSpace != 6) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mProgressBar.getLayoutParams();
            int left = UIUtils.dip2px(2);
            int ver = UIUtils.dip2px( mVerSpace);
            layoutParams.setMargins(-left, ver, -left, ver);
            mRoot2.updateViewLayout(mProgressBar, layoutParams);
        }
        if (mProgressHeight != 0) {
            mProgressBar.setProgressHeight(mProgressHeight);
        }
    }

    private void setButtomColor(int present) {
        mTvStartDisCount.setTextColor(mContext.getResources().getColor(R.color.text_color_999));
        mTvMiddleDisCount.setTextColor(mContext.getResources().getColor(R.color.text_color_999));
        mTvEndDisCount.setTextColor(mContext.getResources().getColor(R.color.text_color_999));
        if (present > 0) {
            mTvStartDisCount.setTextColor(mContext.getResources().getColor(R.color.progress_patch_green));
        }
        if (present >= 50) {
            mTvMiddleDisCount.setTextColor(mContext.getResources().getColor(R.color.progress_patch_green));
        }
        if (present >= 100) {
            mTvEndDisCount.setTextColor(mContext.getResources().getColor(R.color.progress_patch_green));
        }
    }

    public void setData(CsCrowd.Crowd crowd) {
        setData(crowd, null);
    }

    public void setData(CsCrowd.Crowd crowd, ItemBean item) {
        if (item != null) {
            this.mItem = item;
            mPrice = item.getDefaultPrice() * item.getExchangeRate();
        }

        this.mCrowd = crowd;
        List<CsCrowd.CrowdLadder> ladders = crowd.getLaddersList();
        String target = mContext.getResources().getString(R.string.String_count_target_count);
        String targetMoney = mContext.getResources().getString(R.string.String_money_target);
        if (crowd.getLaddersList().size() <= 0) {
            return;
        }

        if (crowd.getCountType() == 1) {
            initNumberLadder(mTvStartDisCount, null);
//            件数
            if (ladders.size() == 1) {
                mTvMiddleDisCount.setVisibility(View.GONE);
                mProgressBar.hideMiddle(true);

                CsCrowd.CrowdLadder ladder1 = ladders.get(0);
                initNumberLadder(mTvEndDisCount, ladder1);
                int progress = (int) ((crowd.getComplete() / ladder1.getGoal()) * 100 + 0.5f);
                mProgressBar.setProgress(progress);

                mTvTarget.setText(String.format(target, (int) (ladder1.getGoal() + 0.5)));
//                progress = progress>100?100:progress;
                mTvStartPersen.setText(getProgressStr(progress));
                setButtomColor(progress);
            } else {
                if (!mHintMiddle) {
                    mTvMiddleDisCount.setVisibility(View.VISIBLE);
                    mProgressBar.hideMiddle(false);
                }
                initNumberLadder(mTvMiddleDisCount, ladders.get(0));
                initNumberLadder(mTvEndDisCount, ladders.get(1));
                CsCrowd.CrowdLadder ladder = ladders.get(0);
                CsCrowd.CrowdLadder ladder2 = ladders.get(1);
                int progress;
                if (crowd.getComplete() <= ladder.getGoal()) {
                    progress = (int) (crowd.getComplete() / ladder.getGoal() * 50);
                } else {
                    progress = (int) (((crowd.getComplete() - ladder.getGoal()) / (ladder2.getGoal() - ladder.getGoal())) * 50) + 50;
                }
                mProgressBar.setProgress(progress);
                mTvTarget.setText(String.format(target, (int) (ladder2.getGoal() + 0.5)));

                int present = (int) ((crowd.getComplete() / ladder2.getGoal()) * 100 + 0.5f);
                mTvStartPersen.setText(getProgressStr(present));
                setButtomColor(present);
            }
        } else {
            initNumberLadder(mTvStartDisCount, null);
            if (ladders.size() == 1) {
//               一阶拼单
                mTvMiddleDisCount.setVisibility(View.GONE);
                mProgressBar.hideMiddle(true);
                CsCrowd.CrowdLadder ladder1 = ladders.get(0);
                initMoneyLadder(mTvEndDisCount, ladder1);
                int progress = (int) ((crowd.getComplete() / ladder1.getGoal()) * 100 + 0.5f);
                mProgressBar.setProgress(progress);

                mTvTarget.setText(String.format(targetMoney, formatMoney(ladder1.getGoal())));
//                progress = progress>100?100:progress;
                mTvStartPersen.setText(getProgressStr(progress));
                setButtomColor(progress);
            } else {
                if (!mHintMiddle) {
                    mTvMiddleDisCount.setVisibility(View.VISIBLE);
                    mProgressBar.hideMiddle(false);
                }
                initMoneyLadder(mTvMiddleDisCount, ladders.get(0));
                initMoneyLadder(mTvEndDisCount, ladders.get(1));
                CsCrowd.CrowdLadder ladder = ladders.get(0);
                CsCrowd.CrowdLadder ladder2 = ladders.get(1);
                int progress;
                if (crowd.getComplete() <= ladder.getGoal()) {
                    progress = (int) (crowd.getComplete() / ladder.getGoal() * 50);
                } else {
                    progress = (int) (((crowd.getComplete() - ladder.getGoal()) / (ladder2.getGoal() - ladder.getGoal())) * 50) + 50;
                }
                mProgressBar.setProgress(progress);
//                mTvTarget.setText(String.format(targetMoney, (int) (ladder2.getGoal() + 0.5)));
                mTvTarget.setText(String.format(targetMoney, formatMoney(ladder2.getGoal())));
                int present = (int) ((crowd.getComplete() / ladder2.getGoal()) * 100 + 0.5f);
//                present = present>100?100:present;
                mTvStartPersen.setText(getProgressStr(present));
                setButtomColor(present);
            }
        }
    }


    private void initNumberLadder(TextView textView, CsCrowd.CrowdLadder ladder1) {
        String noCount = mContext.getResources().getString(R.string.String_count_no_discount);
        String numCount = mContext.getResources().getString(R.string.String_count_num_discount);
        String numCountInt = mContext.getResources().getString(R.string.String_count_num_discount_int);
        String numReduceMoney = mContext.getResources().getString(R.string.String_count_reduce_money);

        if (ladder1 != null && ladder1.getDiscount() != 0) {
            if (ladder1.getDiscountType() == 1) {
                String format = String.format(numReduceMoney, (int) ladder1.getGoal(), formatMoney(ladder1.getDiscount()));
                format = appendPrice(format, mPrice, ladder1.getDiscount(), 1);
                textView.setText(format);
            } else {
                float disCount = formatDiscount(ladder1.getDiscount());
                if (isInt(disCount)) {
                    String format = String.format(numCountInt, (int) ladder1.getGoal(), (int) disCount);
                    format = appendPrice(format, mPrice, ladder1.getDiscount(), 0);
                    textView.setText(format);
                } else {
                    String format = String.format(numCount, (int) ladder1.getGoal(), disCount);
                    format = appendPrice(format, mPrice, ladder1.getDiscount(), 0);
                    textView.setText(format);
                }
            }

        } else {
            String format;
            if (ladder1 != null) {
                if (ladder1.getGoal() > 1) {
                    format = mContext.getResources().getString(R.string.String_count_nos_discount, (int) ladder1.getGoal());
                } else {
                    format = String.format(noCount, (int) ladder1.getGoal());
                }
            } else {
                format = String.format(noCount, 1);
            }
            format = appendPrice(format, mPrice, 0, 1);
            textView.setText(format);
        }
    }

    private float formatDiscount(float discount) {
        if (LgUitl.isOtherLanguage(AccountManager.getInstance().getLocaleCode())) {
            return discount * 100;
        } else {
            return ((1 - discount) * 10);
        }
    }

    private void initMoneyLadder(TextView textView, CsCrowd.CrowdLadder ladder) {
        String noCount = mContext.getResources().getString(R.string.String_money_no_discount);
        String numCount = mContext.getResources().getString(R.string.String_money_num_discount);
        String numCountInt = mContext.getResources().getString(R.string.String_money_num_discount_int);
        String moneyReduceMoney = mContext.getResources().getString(R.string.String_money_reduce_money);

        if (ladder != null && ladder.getDiscount() != 0) {
            if (ladder.getDiscountType() == 1) {
                String format = String.format(moneyReduceMoney, formatMoney(ladder.getGoal()), formatMoney(ladder.getDiscount()));
                format = appendPrice(format, mPrice, ladder.getDiscount(), 1);
                textView.setText(format);
            } else {
                float disCount = formatDiscount(ladder.getDiscount());
                if (isInt(disCount)) {
                    String format = String.format(numCountInt, formatMoney(ladder.getGoal()), (int) disCount);
                    format = appendPrice(format, mPrice, ladder.getDiscount(), 0);
                    textView.setText(format);
                } else {
                    String format = String.format(numCount, formatMoney(ladder.getGoal()), disCount);
                    format = appendPrice(format, mPrice, ladder.getDiscount(), 0);
                    textView.setText(format);
                }
            }
        } else {
            String format;
            if (ladder != null) {
                format = String.format(noCount, formatMoney(ladder.getGoal()));
            } else {
                format = String.format(noCount, 1);
            }
            format = appendPrice(format, mPrice, 0, 1);
            textView.setText(format);
        }
    }

    private String formatMoney(float money) {
        String localeCode = AccountManager.getInstance().getLocaleCode();
        String one = getResources().getString(R.string.String_money_ten_thousand);
        String two = getResources().getString(R.string.String_money_million);

        if (localeCode.contains("en") || localeCode.contains("ru")) {
            if (money >= 1000000) {
                return getCurrencyCode(mContext, money / 1000000, 1, two);
            } else if (money >= 1000) {
                return getCurrencyCode(mContext, money / 1000, 1, one);
            } else {
                return getCurrencyCode(mContext, money, 1, "");
            }
        } else if (localeCode.contains("zh")) {
            if (money >= 1000000) {
                return getCurrencyCode(mContext, money / 1000000, 1, two);

            } else if (money >= 10000) {
                return getCurrencyCode(mContext, money / 10000, 1, one);

            } else {
                return getCurrencyCode(mContext, money, 1, "");
            }
        } else if (localeCode.contains("ko")) {
            if (money >= 1000000) {
                return getCurrencyCode(mContext, money / 1000000, 1, two);

            } else if (money >= 10000) {
                return getCurrencyCode(mContext, money / 10000, 1, one);
            } else {
                return getCurrencyCode(mContext, money, 1, "");
            }
        } else {
            return getCurrencyCode(mContext, money, 1, "");
        }
    }


    public String getCurrencyCode(Context context, float price, int type, String achor) {
        final String currencyCode = mCrowd.getCurrencyCode();
        String formatprice;
        formatprice = Math.round(price) + "";
        if (type != 1) {
            return formatprice + achor + currencyCode;
        } else {
            return currencyCode + formatprice + achor;
        }
    }

    /*private boolean isInt(float num) {
        String sNum = String.format(mContext.getResources().getString(R.string.String_float_1), num);
        String start = ((int) num) + "";
        String suffix = sNum.substring(start.length() + 1, sNum.length());
        Integer value = Integer.valueOf(suffix);
        if (value > 0) {
            return false;
        } else {
            return true;
        }
    }*/
    private boolean isInt(float num) {
        if (AccountManager.getInstance().getLocaleCode().contains("en")) return true;
        if (num == (int) num) {
            return true;
        } else {
            return false;
        }/*
//        String sNum = String.format(mContext.getResources().getString(), num);
        String sNum = mContext.getString(R.string.String_float_1, num);
//        String start = ((int)num)+"";
//        String suffix = sNum.substring(start.length()+1,sNum.length());
        String[] split = sNum.split("/.");
        if (split.length > 1) {
            Integer value = Integer.valueOf(split[1]);
            if (value > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }*/
    }


    private String appendPrice(String string, float price, float discount, int type) {
        if (mItem != null) {
            float finalPrice;
            if (type == 0) {
                finalPrice = price * (1 - discount);
            } else {
                finalPrice = price - (discount * mItem.getExchangeRate());
            }
            mPricesList.add(finalPrice);
            return string + "\n" + UIUtils.getCurrency(mContext, finalPrice);
        } else {
            return string;
        }
    }


    public float getEndPrice() {
        if (mTvMiddleDisCount.getVisibility() == VISIBLE) {
            return mPricesList.get(1);
        } else {
            return mPricesList.get(mPricesList.size() - 1);
        }

    }

    public String getProgressStr(int progress) {
        if (progress >= 100) {
            return (mContext.getString(R.string.String_crowd_sucessed) + progress + "%");
        } else {
            return (progress + "%");
        }
    }
}