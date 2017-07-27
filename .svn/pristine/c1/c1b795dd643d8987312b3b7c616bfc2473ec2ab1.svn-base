package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SimpleBaseAdapter;
import com.fuexpress.kr.bean.ReParcelItemBean;
import com.fuexpress.kr.ui.activity.ParcelSplitActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import fksproto.CsParcel;


/**
 * Created by yuan on 2016-5-6.
 */
public class ParcelSplitItemsApadter extends SimpleBaseAdapter<ReParcelItemBean> implements View.OnClickListener {
    private int lastFocussedPosition = -1;
    private Handler handler = new Handler();

    private final ArrayMap<Integer, Float> mDeclarePrices;
    private final ArrayMap<Integer, Integer> mDeclareNum;
    private final String currencycode;
    private RadioButton mFocusedNumRadio;

    public ParcelSplitItemsApadter(Activity content, List<ReParcelItemBean> data, CsParcel.Parcel parcel) {
        super(content, data);
        mDeclarePrices = new ArrayMap<Integer, Float>();
        mDeclareNum = new ArrayMap<>();
        currencycode = parcel.getCurrencycode();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.view_item_parcel_split, null);
            holder.mTvCurrency = (TextView) convertView.findViewById(R.id.tv_currency);
            holder.mRadioButton = (RadioButton) convertView.findViewById(R.id.rb_check_state);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.img_item_icon);
            ViewGroup group = (ViewGroup) convertView.findViewById(R.id.ll_des_detail);
            holder.mTitle = (TextView) group.getChildAt(0);
            holder.mPrice = (TextView) group.getChildAt(1);
            holder.mCount = (TextView) convertView.findViewById(R.id.tv_count);
            holder.mDeclarePrice = (EditText) convertView.findViewById(R.id.ed_declare_price);

            holder.mRlTopNum = (RelativeLayout) convertView.findViewById(R.id.rl_top_num);
            holder.mEditNum = (EditText) convertView.findViewById(R.id.ed_declare_num);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.mDeclarePrice.setTag(position);

        ReParcelItemBean item = getItem(position);
        ImageLoader.getInstance().displayImage(item.getImagePath(), holder.mIcon, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        holder.mTitle.setText(item.getCaptionCutPrice());
        if (item.getQtyPack() > 1) {
            Integer numValue = mDeclareNum.get(position);
            int count = numValue != null ? (int) (float) numValue : item.getQtyPack();
            holder.mRlTopNum.setVisibility(View.VISIBLE);
            holder.mEditNum.setText(count + "");
            holder.mCount.setVisibility(View.GONE);
        } else {
            holder.mCount.setVisibility(View.VISIBLE);
            holder.mRlTopNum.setVisibility(View.GONE);
            holder.mCount.setText(mContent.getString(R.string.package_cheng) + item.getQtyPack());
        }

        holder.mTvCurrency.setText(UIUtils.getCurrency(mContent, currencycode));

        if (mDeclarePrices.get(position) != null) {
            String value = UIUtils.getCurrency(mContent, currencycode, mDeclarePrices.get(position)).replace(UIUtils.getCurrency(mContent, currencycode), "");
            holder.mDeclarePrice.setText(value);
        } else {
            String declareValue = UIUtils.getCurrency(mContent, currencycode, item.getDeclaredValue()).replace(UIUtils.getCurrency(mContent, currencycode), "");
            holder.mDeclarePrice.setText(declareValue);
        }
        if (item.isSelect()) {
            holder.mRadioButton.setChecked(true);
        } else {
            holder.mRadioButton.setChecked(false);
        }
        holder.mPrice.setText(UIUtils.getCurrency(mContent, currencycode, item.getPrice()));
        convertView.setTag(R.id.rb_check_state, position);
        convertView.setOnClickListener(this);
        View.OnFocusChangeListener declareListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                                lastFocussedPosition = position;
                                holder.mDeclarePrice.requestFocus();
                                holder.mDeclarePrice.setSelection(holder.mDeclarePrice.getText().length());
                            }
                        }
                    }, 200);

                } else {
                    if (lastFocussedPosition == position) {
                        checkPrice(position, holder.mDeclarePrice);
                    }
                    lastFocussedPosition = -1;
                }
            }
        };
        holder.mDeclarePrice.setOnFocusChangeListener(declareListener);

        View.OnFocusChangeListener numListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                                lastFocussedPosition = position;
                                holder.mEditNum.requestFocus();
                                holder.mEditNum.setSelection(holder.mEditNum.getText().length());
                                mFocusedNumRadio = holder.mRadioButton;
                            }
                        }
                    }, 200);
                } else {
                    if (lastFocussedPosition == position) {
                        checkNum(position, holder.mEditNum, holder.mRadioButton);
                        mFocusedNumRadio = null;
                    }
                    lastFocussedPosition = -1;
                }
            }
        };
        holder.mEditNum.setOnFocusChangeListener(numListener);
        return convertView;
    }

    private void checkNum(int position, EditText num, RadioButton radioButton) {
        ReParcelItemBean itemList = mData.get(position);
        String s = num.getText().toString();
        int value = 1;
        if (!TextUtils.isEmpty(s)) {
            value = Integer.valueOf(s);
              /* if (value > itemList.getQtyPack()) {
                value = itemList.getQtyPack();
            }
            if (value < 1) value = 1;
            */
        }
        mDeclareNum.put(position, value);
//        checkDeclareNum(itemList, radioButton);
        if (value > itemList.getQtyPack())
            itemList.setSelect(false);
        ((ParcelSplitActivity) mContent).showSelectItems();
    }

    public boolean checkDeclareNum(ReParcelItemBean item, RadioButton radioButton) {
        if (mFocusedNumRadio != null) {
            item = getItem(lastFocussedPosition);
            radioButton = mFocusedNumRadio;
        }
        int declareSum = 0;
        boolean itemNumOf = false;
        for (int i = 0; i < mData.size(); i++) {
            ReParcelItemBean itemBean = mData.get(i);
            if (!itemBean.isSelect()) continue;

            if (mDeclareNum.get(i) != null & itemBean.isSelect()) {
                if (mDeclareNum.get(i) > itemBean.getQtyPack()) {
                    itemBean.setSelect(false);
                    itemNumOf = true;
                }
            }
            declareSum += mDeclareNum.get(i) == null || mDeclareNum.get(i) == 0 ? itemBean.getQtyPack() : mDeclareNum.get(i);
        }
        int sum = 0;
        for (ReParcelItemBean itemBean : mData) {
            sum += itemBean.getQtyPack();
        }
        if (declareSum >= sum) {
            CustomToast.makeText(mContent, R.string.package_toast_keep_one, Toast.LENGTH_SHORT).show();
          /*  if (item != null && radioButton != null) {
                item.setSelect(false);
                radioButton.setChecked(false);
            }*/
            if (item != null) item.setSelect(false);
            ((ParcelSplitActivity) mContent).showSelectItems(true);
            return false;
        }
        if (itemNumOf) {
            ((ParcelSplitActivity) mContent).showSelectItems(true);
            return false;
        }
        return true;
    }

    int checkCount = 0;

    @Override
    public void onClick(View v) {
        int postion = (int) v.getTag(R.id.rb_check_state);
        ReParcelItemBean item = getItem(postion);
        Holder holder = (Holder) v.getTag();
        checkCount = 0;
        for (ReParcelItemBean list : mData) {
            if (list.isSelect()) {
                checkCount++;
            }
        }
       /* if (checkCount >= mData.size() - 1 && !item.isSelect()) {
            Toast.makeText(mContent, R.string.package_toast_keep_one, Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (!item.isSelect()) {
            item.setSelect(true);
            holder.mRadioButton.setChecked(true);
        } else {
            item.setSelect(false);
            holder.mRadioButton.setChecked(false);
        }
//        checkDeclareNum(item, holder.mRadioButton);
        mData.set(postion, item);
        ParcelSplitActivity content = (ParcelSplitActivity) mContent;
        content.showSelectItems();
    }

    public void hintInputMethod() {

    }


    private void checkPrice(int postion, EditText text) {
        ReParcelItemBean itemList = mData.get(postion);
        float maxValue = itemList.getQtyPack() * itemList.getPrice();
        float value;
        String strValue = text.getText().toString();
        if (!TextUtils.isEmpty(strValue)) {
            value = Float.valueOf(strValue.replaceAll(",", ""));
            if (value > maxValue) {
                value = maxValue;
            }
        } else {
            value = 0;
        }
        String sValue = UIUtils.getCurrency(mContent, currencycode, value).replace(UIUtils.getCurrency(mContent, currencycode), "");/*mContent.getString(R.string.package_double_decimals, value);*/
        text.setText(sValue);
        mDeclarePrices.put(postion, value);
        ((ParcelSplitActivity) mContent).showSelectItems();
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mDeclarePrices.clear();
    }

    public ArrayMap getDeclarePrices() {
        return mDeclarePrices;
    }

    public ArrayMap<Integer, Integer> getDeclareNum() {
        return mDeclareNum;
    }

    static class Holder {
        TextView mTvCurrency;
        RadioButton mRadioButton;
        ImageView mIcon;
        TextView mTitle;
        TextView mPrice;
        TextView mCount;
        EditText mDeclarePrice;

        RelativeLayout mRlTopNum;
        EditText mEditNum;
    }
}
