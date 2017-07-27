package com.fuexpress.kr.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.HelpAdapterValueBean;
import com.fuexpress.kr.bean.HelpItemViewBean;
import com.fuexpress.kr.ui.uiutils.AnimationUtils;
import com.fuexpress.kr.ui.view.CustomGridView;
import com.fuexpress.kr.ui.view.InputBoxView;

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
public class AdapterForHelp extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnFocusChangeListener, TextWatcher {

    public static final String KEY_HELP_ME_SIGNED = "HELP_ME_SIGNED";//帮我收货的type
    public static final String KEY_HELP_ME_GET = "HELP_ME_GET";//帮我取货的type
    public static final String KEY_HELP_ADD = "HELP_ADD";//帮我取货


    private Context mContext;
    private List<ViewHolder> mViewHolderList;
    private boolean mIsClickAni = false;
    private boolean mIsShowingAni = false;
    private HelpAdapterInterface mHelpAdapterInterface;
    private List<CsParcel.ProductDataList> mProductDataLists;
    private String mType = "";
    private ArrayMap<Integer, InputBoxView> mInputBoxViewArrayMap;
    private Set<Integer> mKeySet;
    private int mEditDataPossition;
    private int mEditDataID;
    private EditText mDataEditText;
    private ArrayMap<String, CsBase.Warehouse> mIntegerWarehouseArrayMap;
    private int mMaxSize = 0;


    public AdapterForHelp(Context context) {
        for (int i = 0; i < 3; i++) {
            testqurlList.add(testUrl);
        }
        mContext = context;
    }


    List<String> testqurlList = new ArrayList<>();
    String testUrl = "http://dimage.yissimg.com/storage/emulated/0/Android/data/adyen.com.adyenpaysdk.controllers.NetworkController/cache/201269headIcon1481005394523!140.jpg!small9";

    /**
     * 构造方法,需要下面一个参数
     *
     * @param helpAdapterValueBean 这个Adapter的封装类,详情可以去看这个Bean中的api
     */
    public AdapterForHelp(@NonNull HelpAdapterValueBean helpAdapterValueBean) {
        mContext = checkNotNull(helpAdapterValueBean.getContext(), "Context Not Null!");
        mHelpAdapterInterface = checkNotNull(helpAdapterValueBean.getHelpAdapterInterface(), "Interface Not Null");
        mProductDataLists = checkNotNull(helpAdapterValueBean.getDataLists(), "dataList Not Null");
        mType = helpAdapterValueBean.getType();
        mInputBoxViewArrayMap = checkNotNull(helpAdapterValueBean.getInputBoxArrayMap(), "arrayMap Not Null");
        mMaxSize = helpAdapterValueBean.getMaxSize();
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
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_for_normal_layout, null);
                viewHolder.iv_in_item_delete = (ImageView) convertView.findViewById(R.id.iv_in_item_delete);
                viewHolder.ll_in_normal_item = (LinearLayout) convertView.findViewById(R.id.ll_in_normal_item);
                if (mKeySet == null)
                    mKeySet = mInputBoxViewArrayMap.keySet();
                for (int key : mKeySet) {
                    InputBoxView inputBoxView = mInputBoxViewArrayMap.get(key);
                    if (InputBoxView.WARE_HOUSE_TYPE == inputBoxView.getIpBoxType()) {
                        inputBoxView.getContainerView().setTag(R.string.adapter_id_ip_tag, position);
                        inputBoxView.getContainerView().setTag(R.string.adapter_key_ip_tag, key);
                        inputBoxView.getContainerView().setOnClickListener(this);
                        setWareHouseInfoView(mProductDataLists.get(position).getWarehouseid(), inputBoxView);
                    } else {
                        inputBoxView.getEditTextView().setTag(R.string.adapter_id_ip_tag, position);
                        inputBoxView.getEditTextView().setTag(R.string.adapter_key_ip_tag, key);
                        inputBoxView.getEditTextView().setOnFocusChangeListener(this);
                    }
                    viewHolder.ll_in_normal_item.addView(inputBoxView);
                }
            /*viewHolder.ip_01_help_adapter = (InputBoxView) convertView.findViewById(R.id.ip_01_help_adapter);
            viewHolder.ip_02_help_adapter = (InputBoxView) convertView.findViewById(R.id.ip_02_help_adapter);
            viewHolder.ip_03_help_adapter = (InputBoxView) convertView.findViewById(R.id.ip_03_help_adapter);
            if (KEY_HELP_ME_SIGNED.equals(mType))
                viewHolder.ip_03_help_adapter.setVisibility(View.VISIBLE);
            viewHolder.ip_04_help_adapter = (InputBoxView) convertView.findViewById(R.id.ip_04_help_adapter);
            viewHolder.ip_05_help_adapter = (InputBoxView) convertView.findViewById(R.id.ip_05_help_adapter);*/
                viewHolder.gv_help_head = (CustomGridView) convertView.findViewById(R.id.gv_help_head);
                convertView.setTag(viewHolder);
                mViewHolderList.add(viewHolder);
                if (mIsClickAni)
                    doDeleteAnimationPlus(viewHolder, 1, mIsClickAni);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.gv_help_head.setAdapter(new HelpHeadAdapter(mContext, mProductDataLists.get(position).getExtraList()));
            viewHolder.gv_help_head.setOnItemClickListener(this);
            viewHolder.gv_help_head.setTag(R.string.adapter_id_position, position);
            viewHolder.iv_in_item_delete.setOnClickListener(this);
            viewHolder.iv_in_item_delete.setTag(position);
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
            case R.id.ip_ll_container:
                int po = (int) v.getTag(R.string.adapter_id_ip_tag);
                mHelpAdapterInterface.centerTvOnClick(mInputBoxViewArrayMap.get(R.string.adapter_key_ip_tag), po);
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
            mHelpAdapterInterface.editImage((Integer) parent.getTag(R.string.adapter_id_position), position);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mEditDataPossition = (int) v.getTag(R.string.adapter_id_ip_tag);
            mEditDataID = (Integer) v.getTag(R.string.adapter_key_ip_tag);
            mDataEditText = (EditText) v;
            mDataEditText.addTextChangedListener(this);
        }/* else {
            mHelpAdapterInterface.editData((EditText) v, (int) v.getTag(R.string.adapter_id_ip_tag), (int) v.getTag(R.string.adapter_key_ip_tag));
        }*/

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //mDataEditText.removeTextChangedListener(this);
        mHelpAdapterInterface.editData(mDataEditText, mEditDataPossition, mEditDataID);//假如需要对这个EditText修改的话,请自己手动移除TextWatch
    }

    class ViewHolder {
        LinearLayout ll_in_normal_item;
        ImageView iv_in_item_delete;
        CustomGridView gv_help_head;
        InputBoxView ip_01_help_adapter;
        InputBoxView ip_02_help_adapter;
        InputBoxView ip_03_help_adapter;
        InputBoxView ip_04_help_adapter;
        InputBoxView ip_05_help_adapter;
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
     * @param wareHouseID  需要显示的仓库id
     * @param inputBoxView 需要显示的仓库实体对象
     */
    public void setWareHouseInfoView(String wareHouseID, InputBoxView inputBoxView) {
        if (mIntegerWarehouseArrayMap == null) return;

        CsBase.Warehouse warehouse = mIntegerWarehouseArrayMap.get(wareHouseID);
        inputBoxView.setWareHouseContainerShow(warehouse);
    }


}