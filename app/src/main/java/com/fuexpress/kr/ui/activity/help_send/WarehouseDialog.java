package com.fuexpress.kr.ui.activity.help_send;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.activity.append_parcel.AppendParcelActivity;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.wheel.WheelView;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.AbstractWheelTextAdapter;
import com.fuexpress.kr.ui.view.wheel.wheeladapter.WheelViewAdapter;
import com.fuexpress.kr.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsBase;
import fksproto.CsParcel;
import fksproto.CsUser;

/**
 * Created by Administrator on 2016-11-11.
 */
public class WarehouseDialog extends DialogFragment {
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.wv_body)
    WheelView mLvBody;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    private OnWareSelectListener onListener;

    private int typ;
    public static final String result_ware_house = "RESULT_WARE_HOUSE";

    @OnClick({R.id.warehouse_cancel, R.id.warehouse_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.warehouse_cancel:
                dismiss();
                break;
            case R.id.warehouse_confirm:
                if(mLvBody.getViewAdapter()==null)return;
                int currentItem = mLvBody.getCurrentItem();
                WheelViewAdapter viewAdapter = mLvBody.getViewAdapter();
                if (viewAdapter == null && viewAdapter.getItemsCount() == 0) {
                    dismiss();
                    return;
                }

                if (viewAdapter instanceof WarehouseAdaprer) {
                    CsUser.WareHouseList item = ((WarehouseAdaprer) viewAdapter).getItemData(currentItem);
                    int warehouseid = item.getWarehouseid();
                    Intent intent = new Intent(getContext(), AppendParcelActivity.class);
                    intent.putExtra(AppendParcelActivity.WAREHOUSE_ID, warehouseid);
                    intent.putExtra(AppendParcelActivity.DEFAULT_WEIGHT, item.getDefaultparcelweight());
                    intent.putExtra(result_ware_house, item);
                    getContext().startActivity(intent);
                } else {
                    CsBase.Warehouse warehouse = ((WarehouseAdaprerType1) viewAdapter).getItemData(currentItem);
                    onListener.onSelect(warehouse);
                }
                dismiss();
                break;
        }
    }

    public interface OnWareSelectListener {
        public void onSelect(CsBase.Warehouse warehouse);
    }

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */


 /*   public void showWarehouseDialog() {
//        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        WarehouseDialog dialog = WarehouseDialog.newInstance();
        dialog.show(ft, "dialog");
    }*/
    static WarehouseDialog newInstance() {
        return newInstance(2);
    }

    public static WarehouseDialog newInstance(int type) {
        WarehouseDialog f = new WarehouseDialog();
        Bundle args = new Bundle();
        args.putInt("num", type);
        f.setArguments(args);
        return f;
    }

    public void setOnWarehouseSelectListener(OnWareSelectListener listener) {
        this.onListener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typ = getArguments().getInt("num");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnim;
        dialog.setContentView(R.layout.warehouse_fragment_dialog);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        ButterKnife.bind(this, dialog); // Dialog即View
        mProgress.setVisibility(View.VISIBLE);
        getWarehouses();

        return dialog;
    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.warehouse_fragment_dialog, container, false);
        View tv = v.findViewById(R.id.text);
        progress = v.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        listView = (OpenListView) v.findViewById(R.id.lv_body);
        listView.setOnItemClickListener(this);
        ButterKnife.bind(this, v);
        return v;
    }*/

/*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }*/

    public void getWarehouses() {
        if (typ != 1) {
            CsUser.GetWarehouseListRequest.Builder builder = CsUser.GetWarehouseListRequest.newBuilder()
                    .setUserHead(AccountManager.getInstance()
                            .getBaseUserRequest()).setType(typ).setLocalecode(AccountManager.getInstance().getLocaleCode());
            NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetWarehouseListResponse>() {
                @Override
                public void onSuccess(final CsUser.GetWarehouseListResponse response) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            if (isHidden()||mLvBody==null) return;
                            mLvBody.setViewAdapter(new WarehouseAdaprer(getActivity(), response.getWarehouselistList()));
                            mProgress.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onFailure(int ret, String errMsg) {

                }
            });
        } else {//当状态值=1时,请求帮我收初始化接口
            CsParcel.HelpMyReceiveInitRequest.Builder builder = CsParcel.HelpMyReceiveInitRequest.newBuilder();
            builder.setUserinfo(AccountManager.getInstance()
                    .getBaseUserRequest());
            builder.setLocaleCode(AccountManager.getInstance().getLocaleCode());
            builder.setCurrencyCode(AccountManager.getInstance().getCurrencyCode());
            NetEngine.postRequest(builder, new INetEngineListener<CsParcel.HelpMyReceiveInitResponse>() {
                @Override
                public void onSuccess(final CsParcel.HelpMyReceiveInitResponse response) {
                    if (isHidden()) return;
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            mLvBody.setViewAdapter(new WarehouseAdaprerType1(getActivity(), response.getWarehouseListList()));
                            mProgress.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onFailure(int ret, String errMsg) {
                    LogUtils.e("错误:" + ret + "  " + errMsg);
                }
            });
        }

    }

   /* @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (onListener != null && typ == 1) {//根据有无回调和类型来判断
            CsBase.Warehouse warehouse = ((WarehouseAdaprerType1) parent.getAdapter()).getItem(position);
            onListener.onSelect(warehouse);
        } else {
            CsUser.WareHouseList item = ((WarehouseAdaprer) parent.getAdapter()).getItem(position);
            Intent intent = new Intent(getContext(), AppendParcelActivity.class);
            int warehouseid = item.getWarehouseid();
            intent.putExtra(AppendParcelActivity.WAREHOUSE_ID, warehouseid);
            intent.putExtra(AppendParcelActivity.DEFAULT_WEIGHT, item.getDefaultparcelweight());
            intent.putExtra(result_ware_house, item);
            getContext().startActivity(intent);
        }
        dismiss();
    }*/


    class WarehouseAdaprer extends AbstractWheelTextAdapter {
        List<CsUser.WareHouseList> mData;

        public WarehouseAdaprer(Context context, List<CsUser.WareHouseList> data) {
            super(context);
            mData = data;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return mData.get(index).getWarehousename();
        }

        @Override
        public int getItemsCount() {
            if (mData != null) return mData.size();
            return 0;
        }

        public CsUser.WareHouseList getItemData(int index) {
            return mData.get(index);
        }

    }

    class WarehouseAdaprerType1 extends AbstractWheelTextAdapter {
        List<CsBase.Warehouse> mData;

        public WarehouseAdaprerType1(Context context, List<CsBase.Warehouse> data) {
            super(context);
            mData = data;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return mData.get(index).getName();
        }

        @Override
        public int getItemsCount() {
            if (mData != null) return mData.size();
            return 0;
        }

        public CsBase.Warehouse getItemData(int index) {
            return mData.get(index);
        }

    }


   /* class WarehouseAdaprer extends SimpleBaseAdapter<CsUser.WareHouseList> {


        public WarehouseAdaprer(Activity content, List data) {
            super(content, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setText(mData.get(position).getWarehousename());
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(UIUtils.dip2px(4));
            int top = UIUtils.dip2px(12);
            textView.setPadding(0, top, 0, top);
            return textView;
        }
    }*/

   /* class WarehouseAdaprerType1 extends SimpleBaseAdapter<CsBase.Warehouse> {//新增了一个CsBase.Warehouse类型的Adapter


        public WarehouseAdaprerType1(Activity content, List data) {
            super(content, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setText(mData.get(position).getName());
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(UIUtils.dip2px(4));
            int top = UIUtils.dip2px(12);
            textView.setPadding(0, top, 0, top);
            return textView;
        }
    }*/
}
