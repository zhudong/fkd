package com.fuexpress.kr.ui.adapter;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.HelpAdapterValueBean;
import com.fuexpress.kr.bean.HelpItemViewBean;
import com.fuexpress.kr.ui.activity.help_signed.HelpSignedContract;
import com.fuexpress.kr.ui.activity.help_signed.HelpSignedFragment;
import com.fuexpress.kr.ui.uiutils.AnimationUtils;
import com.fuexpress.kr.ui.view.CustomGridView;
import com.fuexpress.kr.ui.view.InputBoxView;
import com.fuexpress.kr.ui.view.imageselector.ImageLoader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fksproto.CsBase;
import fksproto.CsParcel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Longer on 2016/10/28.
 * 帮我系列的Adapter
 */
public class AdapterForHelpSigned extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener, TextWatcher {

    private Context mContext;
    private List<ViewHolder> mViewHolderList;
    private boolean mIsClickAni = false;
    private boolean mIsShowingAni = false;
    private HelpAdapterInterface mHelpAdapterInterface;
    private List<CsParcel.ProductDataList> mProductDataLists;
    private String mType = "";
    private ArrayMap<String, CsBase.Warehouse> mIntegerWarehouseArrayMap;
    private int mMaxSize = 0;
    private ArrayMap<Integer, ArrayMap<Integer, EditText>> mIntegerEditTextArrayMap;
    private ArrayMap<Integer, HelpHeadAdapter> mPositionHeadAdapter;

    private HelpSignedContract.Presenter mPresenter;

    private EditText mEditDataEdText;
    private int mEditResID;
    private int mEidtIndex;
    /**
     * 构造方法,需要下面一个参数
     *
     * @param helpAdapterValueBean 这个Adapter的封装类,详情可以去看这个Bean中的api
     */
    public AdapterForHelpSigned(@NonNull HelpAdapterValueBean helpAdapterValueBean) {
        mContext = checkNotNull(helpAdapterValueBean.getContext(), "Context Not Null!");
        mHelpAdapterInterface = checkNotNull(helpAdapterValueBean.getHelpAdapterInterface(), "Interface Not Null");
        mProductDataLists = checkNotNull(helpAdapterValueBean.getDataLists(), "dataList Not Null");
        mType = helpAdapterValueBean.getType();
        mMaxSize = helpAdapterValueBean.getMaxSize();
        mPositionHeadAdapter = new ArrayMap<>();
    }

    @Override
    public int getCount() {
        if (mProductDataLists == null)
            return 0;
        else {
            if (mProductDataLists.size() < mMaxSize)
                return mProductDataLists.size() + 1;
            else
                return mProductDataLists.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mProductDataLists == null ? null : mProductDataLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mProductDataLists == null ? 0 : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mViewHolderList == null) mViewHolderList = new ArrayList<>();
        if (position == mProductDataLists.size() && position < mMaxSize) {//显示尾部添加按钮的布局
            convertView = View.inflate(mContext, R.layout.help_adapter_foot_layout, null);
            LinearLayout ll_help_adapter_add = (LinearLayout) convertView.findViewById(R.id.ll_help_adapter_add);
            ll_help_adapter_add.setOnClickListener(this);
        } else {
            ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                ArrayMap<Integer, EditText> tempResIDEditTextMap = new ArrayMap<>();
                convertView = View.inflate(mContext, R.layout.item_for_help_signed, null);
                viewHolder.iv_in_item_delete = (ImageView) convertView.findViewById(R.id.iv_in_item_delete);
                viewHolder.ll_in_normal_item = (LinearLayout) convertView.findViewById(R.id.ll_in_normal_item);
                viewHolder.ll_ware_house_container = (LinearLayout) convertView.findViewById(R.id.ll_ware_house_container);
                viewHolder.tv_ware_house_show = (TextView) convertView.findViewById(R.id.tv_ware_house_show);
                viewHolder.ll_choose_ware_house = (LinearLayout) convertView.findViewById(R.id.ll_choose_ware_house);
                viewHolder.ll_choose_ware_house.setOnClickListener(this);
                viewHolder.ll_choose_ware_house.setTag(R.string.adapter_id_ip_tag, position);
                viewHolder.ed_desc = (EditText) convertView.findViewById(R.id.ed_desc);
                tempResIDEditTextMap.put(R.string.hm_item_desc, viewHolder.ed_desc);
                viewHolder.ed_desc.setTag(R.string.adapter_id_ip_tag, R.string.hm_item_desc);
                viewHolder.ed_desc.setTag(R.string.adapter_key_ip_tag, position);
                viewHolder.ed_desc.setOnFocusChangeListener(this);
                viewHolder.ed_remark = (EditText) convertView.findViewById(R.id.ed_remark);
                viewHolder.ed_remark.setTag(R.string.adapter_id_ip_tag, R.string.hm_item_remark);
                viewHolder.ed_remark.setTag(R.string.adapter_key_ip_tag, position);
                viewHolder.ed_remark.setOnFocusChangeListener(this);
                tempResIDEditTextMap.put(R.string.hm_item_remark, viewHolder.ed_remark);
                viewHolder.ed_price = (EditText) convertView.findViewById(R.id.ed_price);
                viewHolder.ed_price.setTag(R.string.adapter_id_ip_tag, R.string.hm_item_request_price);
                viewHolder.ed_price.setTag(R.string.adapter_key_ip_tag, position);
                viewHolder.ed_price.setOnFocusChangeListener(this);
                tempResIDEditTextMap.put(R.string.hm_item_request_price, viewHolder.ed_price);
                viewHolder.ed_quantity = (EditText) convertView.findViewById(R.id.ed_quantity);
                viewHolder.ed_quantity.setTag(R.string.adapter_id_ip_tag, R.string.hm_item_quantity);
                viewHolder.ed_quantity.setTag(R.string.adapter_key_ip_tag, position);
                viewHolder.ed_quantity.setOnFocusChangeListener(this);
                tempResIDEditTextMap.put(R.string.hm_item_quantity, viewHolder.ed_quantity);
                viewHolder.gv_help_head = (CustomGridView) convertView.findViewById(R.id.gv_help_head);
                convertView.setTag(viewHolder);
                mViewHolderList.add(viewHolder);
                if (mIntegerEditTextArrayMap == null)
                    mIntegerEditTextArrayMap = new ArrayMap<>();
                mIntegerEditTextArrayMap.put(position, tempResIDEditTextMap);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (mIsClickAni)
                doDeleteAnimationPlus(viewHolder, 1, mIsClickAni);
            CsParcel.ProductDataList productDataList = mProductDataLists.get(position);
            HelpHeadAdapter helpHeadAdapter = mPositionHeadAdapter.get(position);
            ArrayList<String> imagePathList = mPresenter.getItemLocalPathMap().get(position);
            if (helpHeadAdapter == null) {
                helpHeadAdapter = new HelpHeadAdapter(mContext, imagePathList);
                mPositionHeadAdapter.put(position, helpHeadAdapter);
            } else {
                helpHeadAdapter.setImagePathList(imagePathList);
                helpHeadAdapter.notifyDataSetChanged();
            }
            viewHolder.gv_help_head.setAdapter(helpHeadAdapter);
            viewHolder.gv_help_head.setOnItemClickListener(this);
            viewHolder.gv_help_head.setTag(R.string.adapter_id_position, position);
            viewHolder.iv_in_item_delete.setOnClickListener(this);
            viewHolder.iv_in_item_delete.setTag(position);
            if (!TextUtils.isEmpty(productDataList.getWarehouseid())) //如果该数据有wareHousID,我们就显示出来
                setWareHouseInfoView(productDataList.getWarehouseid(), viewHolder.ll_ware_house_container, viewHolder.tv_ware_house_show);
            else
                viewHolder.ll_ware_house_container.setVisibility(View.GONE);
            if (productDataList.hasProductdescription())
                viewHolder.ed_desc.setText(productDataList.getProductdescription());
            if (productDataList.hasProductremark())
                viewHolder.ed_remark.setText(productDataList.getProductremark());
            if (productDataList.hasPrice()) viewHolder.ed_price.setText(productDataList.getPrice());
            if (productDataList.hasNum()) viewHolder.ed_quantity.setText(productDataList.getNum());
        }
        return convertView;
    }


    public void doDeletAnimation() {
        if (mIsShowingAni) return;
        mIsClickAni = !mIsClickAni;
        for (ViewHolder viewHolder : mViewHolderList) {
            doDeleteAnimationPlus(viewHolder, 500, mIsClickAni);
        }
    }

    private void doDeleteAnimationPlus(ViewHolder viewHolder, long time, boolean isOpen) {
        AnimationUtils.doDeletAnimation(HelpItemViewBean.create().setLLView(viewHolder.ll_in_normal_item).setImageView(viewHolder.iv_in_item_delete).setDuration(time).setAnimatiorAdapter(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIsShowingAni = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsShowingAni = false;
            }
        }), isOpen);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_in_item_delete:
                mHelpAdapterInterface.deletData((Integer) v.getTag());
                break;
            case R.id.ll_choose_ware_house:
                int po = (int) v.getTag(R.string.adapter_id_ip_tag);
                mHelpAdapterInterface.centerTvOnClick(po);
                break;
            case R.id.ll_help_adapter_add:
                mHelpAdapterInterface.addData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ((boolean) view.getTag(R.string.adapter_id_is_add))
            mHelpAdapterInterface.addImage((Integer) parent.getTag(R.string.adapter_id_position));
            //CustomToast.makeText(mContext, "我是添加照片按钮,我被点击了" + parent.getTag(R.string.adapter_id_position), Toast.LENGTH_SHORT).show();
        else
            //CustomToast.makeText(mContext, parent.getTag(R.string.adapter_id_position) + "----" + (String) view.getTag(R.string.adapter_id_url), Toast.LENGTH_SHORT).show();
            mHelpAdapterInterface.editImage((Integer) parent.getTag(R.string.adapter_id_position),position);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mEditDataEdText = (EditText) v;
            mEditResID = (int) mEditDataEdText.getTag(R.string.adapter_id_ip_tag);
            mEidtIndex = (int) mEditDataEdText.getTag(R.string.adapter_key_ip_tag);
            mEditDataEdText.addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mHelpAdapterInterface.editData(mEditDataEdText, mEidtIndex, mEditResID);
    }


    class ViewHolder {
        LinearLayout ll_in_normal_item;
        ImageView iv_in_item_delete;
        CustomGridView gv_help_head;
        EditText ed_desc;
        EditText ed_remark;
        LinearLayout ll_choose_wh;
        EditText ed_price;
        EditText ed_quantity;
        LinearLayout ll_ware_house_container;
        LinearLayout ll_choose_ware_house;
        TextView tv_ware_house_show;
    }

    /**
     * 提供给外部用来设置需要显示的仓库信息数据的方法:
     *
     * @param wareHouseID 仓库数据的id,也是对应的key
     * @param warehouse   仓库的数据实体
     */
    public void setWareHouseData(String wareHouseID, @NonNull CsBase.Warehouse warehouse) {
        if (mIntegerWarehouseArrayMap == null)
            mIntegerWarehouseArrayMap = new ArrayMap<>();
        mIntegerWarehouseArrayMap.put(wareHouseID, warehouse);
    }

    /**
     * 把仓库信息和视图绑定在一起的方法
     *
     * @param wareHouseID 需要显示的仓库id
     */
    public void setWareHouseInfoView(String wareHouseID, LinearLayout linearLayout, TextView textView) {
        if (mIntegerWarehouseArrayMap != null) {
            CsBase.Warehouse warehouse = mIntegerWarehouseArrayMap.get(wareHouseID);
            linearLayout.setVisibility(View.VISIBLE);
            TextView address = (TextView) linearLayout.findViewById(R.id.tv_ip_address);
            address.setText(SysApplication.getContext().getString(R.string.ip_text_address, warehouse.getFulladdress()));
            TextView receiver = (TextView) linearLayout.findViewById(R.id.tv_ip_receiver);
            receiver.setText(SysApplication.getContext().getString(R.string.ip_text_receiver, warehouse.getReceiver()));
            TextView tv_ip_phone = (TextView) linearLayout.findViewById(R.id.tv_ip_phone);
            tv_ip_phone.setText(SysApplication.getContext().getString(R.string.ip_text_phone, warehouse.getPhone()));
            TextView tv_ip_post_code = (TextView) linearLayout.findViewById(R.id.tv_ip_post_code);
            tv_ip_post_code.setText(SysApplication.getContext().getString(R.string.ip_text_post_code, warehouse.getPostcode()));
            textView.setText(warehouse.getName());
        }

    }


    public void checkAllItemParameter() {
        Set<Integer> integers = mIntegerEditTextArrayMap.keySet();
        for (int position : integers) {
            ArrayMap<Integer, EditText> integerEditTextArrayMap = mIntegerEditTextArrayMap.get(position);
            Set<Integer> resIDs = integerEditTextArrayMap.keySet();
            for (int resID : resIDs) {
                EditText editText = integerEditTextArrayMap.get(resID);
                String parameter = editText.getText().toString();
                // TODO: 2016/12/11 可能要对参数进行不同的判断,这里我们先判断是否为空

            }
        }
    }

    /**
     * 获得EditText的Map集合
     */
    public ArrayMap<Integer, ArrayMap<Integer, EditText>> getIntegerEditTextArrayMap() {
        return mIntegerEditTextArrayMap;
    }

    public void setPresenter(HelpSignedContract.Presenter presenter) {
        mPresenter = presenter;
    }

}