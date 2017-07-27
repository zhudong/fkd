package com.fuexpress.kr.ui.activity.help_signed;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.ClipboardManager;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.activity.MaterialsSearchActivity;
import com.fuexpress.kr.ui.activity.PreviewMerchandiseActivity;
import com.fuexpress.kr.ui.activity.choose_category.ChooseCategoryActivity;
import com.fuexpress.kr.ui.activity.help_send.WarehouseDialog;
import com.fuexpress.kr.ui.activity.help_signed.data.HelpMeSingleBean;
import com.fuexpress.kr.ui.activity.help_signed.data.WareHouseBean;
import com.fuexpress.kr.ui.adapter.HelpHeadAdapter;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomDialog;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.UpLoadImageUtils;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fksproto.CsBase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Longer on 2016/12/20.
 * 帮我收的单项详细Fragment
 * 包含添加和编辑
 */
public class HSItemDetailFragment extends BaseFragment<HelpMeSignedActivity> implements HelpSignedContract.ItemView, AdapterView.OnItemClickListener {

    @BindView(R.id.tl_in_help_get_package)
    TitleBarView mTlInHelpGetPackage;
    @BindView(R.id.gv_help_head)
    GridView mGvHelpHead;
    @BindView(R.id.ed_desc)
    EditText mEdDesc;
    @BindView(R.id.ed_remark)
    EditText mEdRemark;
    @BindView(R.id.tv_ware_house_show)
    TextView mTvWareHouseShow;
    @BindView(R.id.ll_choose_ware_house)
    RelativeLayout mLlChooseWareHouse;
    @BindView(R.id.ll_ware_house_container)
    LinearLayout mLlWareHouseContainer;
    @BindView(R.id.ed_price)
    EditText mEdPrice;
    @BindView(R.id.tv_currency_code)
    TextView mTvCurrencyCode;
    @BindView(R.id.ed_quantity)
    EditText mEdQuantity;
    @BindView(R.id.tv_add_demand)
    TextView mTvAddDemand;
    @BindView(R.id.btn_confirm_demand)
    Button mBtnConfirmDemand;
    @BindView(R.id.tv_ip_address)
    TextView mTvIpAddress;
    @BindView(R.id.tv_ip_receiver)
    TextView mTvIpReceiver;
    @BindView(R.id.tv_ip_phone)
    TextView mTvIpPhone;
    @BindView(R.id.tv_ip_post_code)
    TextView mTvIpPostCode;
    @BindView(R.id.ll_in_normal_item)
    LinearLayout mLlInNormalItem;
    @BindView(R.id.ed_name)
    EditText mEdName;
    @BindView(R.id.tv_choose_material_show)
    TextView mTvChooseMaterialShow;
    @BindView(R.id.rl_choose_material)
    RelativeLayout mRlChooseMaterial;
    @BindView(R.id.tv_choose_category_show)
    TextView mTvChooseCategoryShow;
    @BindView(R.id.rl_choose_category)
    RelativeLayout mRlChooseCategory;
    @BindView(R.id.ll_ware_house_copy)
    LinearLayout mLlWareHouseCopy;
    @BindView(R.id.tv_copy_address)
    TextView mTvCopyAddress;


    private ArrayList<String> mImagePathList;//临时用来保存图片本地的集合(供上传用)

    private ArrayList<String> mImagePathLastList;//用来保存开启图片选择器之前的图片集合

    private ArrayList<String> mShowImageList;//显示图片的集合(既可能有本地,也可能有网络上来的,因其混合特性,仅作显示用)

    private HelpSignedContract.NewPresenter mPresenter;//逻辑层对象
    private HelpHeadAdapter mHelpHeadAdapter;//头部的图片Adapter
    private final static int mMaxImageSize = 4;//当前最大可添加图片数量

    private final static int mISRequestCode = 1000;

    private boolean mIsClickConf = false;

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.hs_item_detail_layout, null);
    }

    @Override
    public void initData() {
        mTlInHelpGetPackage.getIv_in_title_back().setOnClickListener(this);
        TextView title_back_tv = mTlInHelpGetPackage.getmTv_in_title_back_tv();
        if (AccountManager.getInstance().getCurrencyCode().equals("KRW")
                || AccountManager.getInstance().getCurrencyCode().equals("TWD")) {
            mEdPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEdPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }

        //mEdQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        title_back_tv.setText(getString(R.string.home_fg_help_02));
        title_back_tv.setOnClickListener(this);
        mImagePathList = new ArrayList<>();
        mShowImageList = new ArrayList<>();
        mImagePathLastList = new ArrayList<>();
        /*ViewGroup.LayoutParams layoutParams = mGvHelpHead.getLayoutParams();
        layoutParams.height = UIUtils.dip2px(91);
        mGvHelpHead.setLayoutParams(layoutParams);*/
        mGvHelpHead.setOnItemClickListener(this);
        /*mLlWareHouseCopy.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CsBase.Warehouse wareHouseInfo = mPresenter.getWareHouseInfo(mPresenter.getWareHouseID());
                String addressText = "";
                if (wareHouseInfo != null)
                    addressText = wareHouseInfo.getFulladdress();
                else {
                    HelpMeSingleBean shouldShowData = mPresenter.getShouldShowData();
                    addressText = shouldShowData.getWareHouseBean().getAddress();
                }
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(addressText);
                CustomToast.makeText(mContext, getString(R.string.hs_note_copy_address), Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/
        judgmentAndShowView();
    }

    @Override
    public List<String> getShowImageList() {
        return mShowImageList;
    }

    @Override
    public List<String> getImagePathList() {
        return mImagePathList;
    }

    @Override
    public ArrayList<String> getImagePathLastList() {
        return mImagePathLastList;
    }

    @Override
    public void showDeleteDialog(final int index) {
        // TODO: 2016/12/22 显示删除的dialog
        //CustomToast.makeText(mContext, "显示删除确认对话框", Toast.LENGTH_SHORT).show();
        CustomDialog.Builder dialog = new CustomDialog.Builder(mContext);
        dialog.setMessage(getString(R.string.delete_demand_note));
        dialog.setPositiveButton(getString(R.string.my_gift_card_order_binding_dialog_config), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mPresenter.deleteDemand(index);
                finishThisView();
            }
        });
        dialog.setNegativeButton(getString(R.string.my_gift_card_order_binding_dialog_cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();

    }

    @Override
    public void checkTextIsEnter(boolean isConfirm) {
        //mContext.hideSoftInput();
        boolean isParameterRight;
        String errText = "";
        if (!(isParameterRight = mPresenter.checkImagePathList(mShowImageList)))
            errText = getString(R.string.hs_note_image);
        else if (!(isParameterRight = mPresenter.checkItemDescIsRight(mEdDesc.getText().toString().trim())))
            errText = getString(R.string.hs_note_desc);
        else if (!(isParameterRight = mPresenter.checkRemarkIsRight(mEdRemark.getText().toString().trim())))
            errText = getString(R.string.hs_note_remark);
        else if (!(isParameterRight = mPresenter.isChooseWareHouse(mPresenter.getWareHouseID())))
            errText = getString(R.string.hs_note_warehouse);
        else if (!(isParameterRight = mPresenter.checkPriceIsRight(mEdPrice.getText().toString().trim())))
            errText = getString(R.string.hs_note_price);
        else if (!(isParameterRight = mPresenter.checkItemQuantity(mEdQuantity.getText().toString().trim())))
            errText = getString(R.string.hs_note_quantity);
        else if (!(isParameterRight = !TextUtils.isEmpty(mPresenter.getDataCateoryBean().getSubName())))
            errText = getString(R.string.hs_note_category);
        /*else if (!(isParameterRight = mPresenter.getDataCateoryBean().getSubID() != -1))
            errText = getString(R.string.hs_note_material);*/
        if (!isParameterRight) {
            showRemindDialog(SweetAlertDialog.WARNING_TYPE, errText);
            dissMissLoadingView(1000);
        } else {//说明已经通过基本的检验,可以把该需求单加入到集合中了
            HelpMeSingleBean shouldShowData = mPresenter.getShouldShowData();
            shouldShowData.setDesc(mEdDesc.getText().toString().trim());
            shouldShowData.setRemark(mEdRemark.getText().toString().trim());
            if (!shouldShowData.getWareHouseID().equals(mPresenter.getWareHouseID())) {
                shouldShowData.setWareHouseID(mPresenter.getWareHouseID());
                mPresenter.setWareHouseToBean(mPresenter.getWareHouseID(), shouldShowData);
            }
            shouldShowData.setPrice(mEdPrice.getText().toString().trim());
            shouldShowData.setItemNum(mEdQuantity.getText().toString().trim());
            List<String> imagePathLit = shouldShowData.getImagePathLit();
            List<String> showImgList = shouldShowData.getShowImgList();
            showImgList.clear();
            showImgList.addAll(mShowImageList);
            imagePathLit.clear();
            imagePathLit.addAll(mImagePathList);
            shouldShowData.setCoverImagePath(mShowImageList.get(0));
            /*if (mPresenter.getIsAddType()) {
                if (mPresenter.IsUpLoadImageComplete(mPresenter.getChooseItemIndex()))//假如该index下图片上传完了,我们就把图片塞进去
                    mPresenter.saveUrlToBean(shouldShowData);
            } else {
                if (!shouldShowData.isUrlReady())
                    if (mPresenter.IsUpLoadImageComplete(mPresenter.getChooseItemIndex()))//假如该index下图片上传完了,我们就把图片塞进去
                        mPresenter.saveUrlToBean(shouldShowData);
            }*/
            if (mPresenter.IsUpLoadImageComplete(mPresenter.getChooseItemIndex()))//假如该index下图片上传完了,我们就把图片塞进去
                mPresenter.saveUrlToBean(shouldShowData);
            shouldShowData.setIsClickConfirm(true);
            shouldShowData.setCurrency_sign(AccountManager.getInstance().mCurrencySign);
            if (TextUtils.isEmpty(shouldShowData.getCurrency_Code()))
                shouldShowData.setCurrency_Code(AccountManager.getInstance().getCurrencyCode());
            CategoryBean categoryBean = mPresenter.getDataCateoryBean();
            shouldShowData.setCategoryBean(categoryBean);
            shouldShowData.setBrandName(mEdName.getText().toString().trim());
            mPresenter.setDeMand(mPresenter.getChooseItemIndex(), shouldShowData);
            mContext.setIndexIsClickConf(mPresenter.getChooseItemIndex(), mIsClickConf = true);
            showToastMethod(R.string.add_demand_success);
            if (!isConfirm) {
                mPresenter.addAndReplaceDeMand();//替换新的需求单
                judgmentAndShowView();//添加模式下,清空视图
                //finishThisView();
            } else finishThisView();//编辑(或确认)模式下,结束视图
        }
    }

    @Override
    public void showWareHouseChoose(boolean isShow) {
        if (isShow) {
            // TODO: 2016/12/22 显示仓库选择器
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            WarehouseDialog dialog = WarehouseDialog.newInstance(1);
            dialog.setOnWarehouseSelectListener(new WarehouseDialog.OnWareSelectListener() {//处理仓库的返回结果
                @Override
                public void onSelect(CsBase.Warehouse warehouse) {
                    LogUtils.e("获得了仓库的返回值:" + warehouse.toString());
                    mPresenter.setWareHouseInfo(String.valueOf(warehouse.getWarehouseId()), warehouse);
                }
            });
            dialog.show(ft, "dialog");
        } else {
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    CustomToast.makeText(mContext, getString(R.string.string_no_warehouse), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void showRemindDialog(int type, String text) {
        mContext.showProgressDiaLog(type, text);
    }

    @Override
    public void showToastMethod(final int stringID) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomToast.makeText(mContext, getString(stringID), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void dissMissLoadingView(long delay) {
        if (mContext.getSweetAlertDialog() != null && mContext.getSweetAlertDialog().isShowing())
            mContext.dissMissProgressDiaLog(delay);
    }

    @Override
    public void showWareHouseInfo(CsBase.Warehouse warehouse, boolean isShow) {
        mLlWareHouseContainer.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if (warehouse != null) {
            mTvWareHouseShow.setText(warehouse.getName());
            mTvIpAddress.setText(getString(R.string.ip_text_address, warehouse.getFulladdress()));
            mTvIpPhone.setText(getString(R.string.ip_text_phone, warehouse.getPhone()));
            mTvIpPostCode.setText(getString(R.string.ip_text_post_code, warehouse.getPostcode()));
            mTvIpReceiver.setText(getString(R.string.ip_text_receiver, warehouse.getReceiver()));
        } else {
            mTvWareHouseShow.setText(getString(R.string.hms_warehouse_text_hint));
        }
    }

    @Override
    public void showWareHouseInfoByBean(@NonNull WareHouseBean wareHouseBean, boolean isShow) {
        mLlWareHouseContainer.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mTvWareHouseShow.setText(wareHouseBean.getName());
        mTvIpAddress.setText(getString(R.string.ip_text_address, wareHouseBean.getAddress()));
        mTvIpPhone.setText(getString(R.string.ip_text_phone, wareHouseBean.getPhone()));
        mTvIpPostCode.setText(getString(R.string.ip_text_post_code, wareHouseBean.getPostCode()));
        mTvIpReceiver.setText(getString(R.string.ip_text_receiver, wareHouseBean.getReceiver()));
    }

    @Override
    public void showMaterialInfoByBean(@NonNull MaterialsBean materialsBean) {
        String name = materialsBean.getName();
        if (TextUtils.isEmpty(name))
            name = getString(R.string.hs_material_hint);
        mTvChooseMaterialShow.setText(name);
    }

    @Override
    public void showCategoryInfoByBean(@NonNull CategoryBean categoryBean) {
        String name = categoryBean.getSubName();
        if (TextUtils.isEmpty(name))
            name = getString(R.string.hs_category_hint);
        mTvChooseCategoryShow.setText(name);
    }

    @Override
    public void referGridViewShow() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHelpHeadAdapter.setImagePathList(mShowImageList);
                mHelpHeadAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void setPresenter(@NonNull HelpSignedContract.NewPresenter presenter) {
        mPresenter = checkNotNull(presenter, "presenter is not null");
    }

    @Override
    public void judgmentAndShowView() {
        HelpMeSingleBean shouldShowData = mPresenter.getShouldShowData();
        mTvCurrencyCode.setText(TextUtils.isEmpty(shouldShowData.getCurrency_Code())
                ? AccountManager.getInstance().getCurrencyCode() : shouldShowData.getCurrency_Code());
        if (mPresenter.getIsAddType()) {//数据为空的情况下,说明是添加单品的操作
            mTlInHelpGetPackage.setTitleText(getString(R.string.parcel_append_item));
            setActivityCurrentIndex(mPresenter.getChooseItemIndex());
        } else {//数据不为空的情况下,说明是编辑的操作
            TextView tv_in_title_right = mTlInHelpGetPackage.getTv_in_title_right();
            tv_in_title_right.setText(getString(R.string.preview_delete_msg));
            tv_in_title_right.setOnClickListener(this);
            mTlInHelpGetPackage.setTitleText(getString(R.string.string_edit_item));
            mTvAddDemand.setVisibility(View.GONE);
        }
        mImagePathList = new ArrayList<>();
        mShowImageList = new ArrayList<>();
        mIsClickConf = shouldShowData.isClickConfirm();
        if (shouldShowData.getImagePathLit().size() > 0) {
            mImagePathList.addAll(shouldShowData.getImagePathLit());
            mImagePathLastList.addAll(shouldShowData.getImagePathLit());
        }
        if (shouldShowData.getShowImgList().size() > 0)
            mShowImageList.addAll(shouldShowData.getShowImgList());
        //CsParcel.ProductDataList chooseData = mPresenter.getShouldShowData();
        mEdDesc.setText(shouldShowData.getDesc());
        mEdRemark.setText(shouldShowData.getRemark());
        mEdPrice.setText(shouldShowData.getPrice());
        mEdQuantity.setText(shouldShowData.getItemNum());
        mEdName.setText(shouldShowData.getBrandName());
        if (!TextUtils.isEmpty(shouldShowData.getWareHouseID()))
            showWareHouseInfoByBean(shouldShowData.getWareHouseBean(), true);
        else
            showWareHouseInfo(null, false);
        showMaterialInfoByBean(shouldShowData.getMaterialsBean());
        showCategoryInfoByBean(shouldShowData.getCategoryBean());
        if (!mPresenter.isCanAddDemand())
            mTvAddDemand.setVisibility(View.GONE);
        mHelpHeadAdapter = new HelpHeadAdapter(mContext, mShowImageList, mMaxImageSize);
        mGvHelpHead.setAdapter(mHelpHeadAdapter);
    }

    @Override
    public Context getViewContext() {
        return mContext;
    }


    @Override
    public boolean getIsClickSub() {
        return mContext.getIsClickSub();
    }

    @Override
    public boolean getLoadingDialogIsShowing() {
        return mContext.getSweetAlertDialog().isShowing();
    }

    @Override
    public void setActivityCurrentIndex(int index) {
        mContext.setCurrentIndex(index);
    }


    @Override
    public void finishThisView() {
        mContext.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        if (!mIsClickConf)
            mPresenter.deleteDemand(mPresenter.getChooseItemIndex());
        super.onDestroyView();
        LogUtils.e("FG视图被销毁");
    }

    @OnClick({R.id.ll_choose_ware_house, R.id.tv_add_demand, R.id.btn_confirm_demand, R.id.rl_choose_material, R.id.rl_choose_category, R.id.tv_copy_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_choose_material:
                // TODO: 2017/1/17 启动材质选择
                Intent intent = new MaterialsSearchActivity.IntentBuilder(0)
                        .build(mContext, new MaterialsSearchActivity.iMaterialsSelectListener() {
                            @Override
                            public void select(boolean select, MaterialsBean bean) {
                                if (select) {
                                    showMaterialInfoByBean(bean);
                                    mPresenter.setDataMaterialBean(bean);
                                }
                            }
                        });
                startActivity(intent);
                break;
            case R.id.rl_choose_category:
                // TODO: 2017/1/17 启动选择分类
                ChooseCategoryActivity.IntentBuilder intentBuilder = ChooseCategoryActivity.IntentBuilder.build(mContext)
                        .setParentID(mPresenter.getShouldShowData().getCategoryBean().getParentID())//设置父级分类ID
                        .setSubID(mPresenter.getShouldShowData().getCategoryBean().getSubID())//设置子级分类ID
                        .setWhereFrom(getString(R.string.home_fg_help_02))//设置是从哪里来的
                        .setListener(new ChooseCategoryActivity.ChooseCategoryListener() {//设置监听
                            @Override
                            public void select(boolean select, CategoryBean bean) {
                                LogUtils.e(String.valueOf(select));
                                if (select) {
                                    showCategoryInfoByBean(bean);
                                    mPresenter.setDataCategoryBean(bean);
                                }
                            }
                        });
                startActivity(intentBuilder);
                break;
            case R.id.ll_choose_ware_house:
                // TODO: 2016/12/22 启动仓库选择器
                //showWareHouseChoose();
                mPresenter.getWareHouseDataRemote();//此处修改为先从网络上请求
                break;
            case R.id.tv_add_demand:
                // TODO: 2016/12/22 添加需求
                checkTextIsEnter(false);
                break;
            case R.id.btn_confirm_demand:
                // TODO: 2016/12/22 确认当前需求单
                checkTextIsEnter(true);
                break;
            case R.id.tv_in_title_right:
                // TODO: 2016/12/23 显示删除对话框
                showDeleteDialog(mPresenter.getChooseItemIndex());
                break;
            case R.id.tv_in_title_back_tv:
            case R.id.iv_in_title_back:
                /*if (!mIsClickConf)
                    mPresenter.deleteDemand(mPresenter.getChooseItemIndex());*/
                finishThisView();
                break;
            case R.id.tv_copy_address:
                CsBase.Warehouse wareHouseInfo = mPresenter.getWareHouseInfo(mPresenter.getWareHouseID());
                String copyText = "";
                StringBuffer sb = new StringBuffer();
                sb.append(mTvIpAddress.getText().toString().trim());
                sb.append("\n");
                sb.append(mTvIpReceiver.getText().toString().trim());
                sb.append("\n");
                sb.append(mTvIpPhone.getText().toString().trim());
                sb.append("\n");
                sb.append(mTvIpPostCode.getText().toString().trim());
                copyText = sb.toString();
                ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(copyText);
                //CustomToast.makeText(mContext, copyText, Toast.LENGTH_SHORT).show();
                showRemindDialog(SweetAlertDialog.SUCCESS_TYPE, getString(R.string.notice_copy_warehouse));
                dissMissLoadingView(1000);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mShowImageList.size()) {
            // TODO: 2016/12/22 启动图片选择器
            //CustomToast.makeText(mContext, "启动图片选择器", Toast.LENGTH_SHORT).show();
            MPermissions.requestPermissions(HSItemDetailFragment.this, 3, Manifest.permission.WRITE_EXTERNAL_STORAGE);//请求权限
        } else {
            // TODO: 2016/12/22 启动图片预览
            Intent intent = new Intent();
            intent.setClass(mContext, PreviewMerchandiseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imgList", mShowImageList);
            intent.putExtra("resourceType", PreviewMerchandiseActivity.RESOURCE_TYPE_ADD);
            bundle.putInt("position", position);
            intent.putExtras(bundle);
            startActivityForResult(intent, PreviewMerchandiseActivity.REQUEST_CODE_BACK_IMG_LIST);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//处理好各种返回值
        if (data != null) {
            switch (requestCode) {
                case mISRequestCode:
                    // TODO: 2016/12/23 处理图片选择器返回的图片
                    //mImagePathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                    ArrayList<String> backList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
                    List<String> shouldUpLoadList = mPresenter.handleImageSelectorReturn(0, mPresenter.getChooseItemIndex(), backList);
                    if (shouldUpLoadList.size() > 0)
                        mPresenter.upLoadImageToUpYun(shouldUpLoadList, UpLoadImageUtils.ADD_PACKAGE, mPresenter.getPresenterUpLoadListener(), mPresenter.getChooseItemIndex());
                    break;
                case PreviewMerchandiseActivity.REQUEST_CODE_BACK_IMG_LIST:
                    // TODO: 2016/12/23 处理预览图片返回的图片
                    ArrayList<String> backImgList = (ArrayList<String>) data.getExtras().getSerializable("backImgList");

                    /*mShowImageList = new ArrayList<>();
                    mShowImageList.addAll(backImgList);*/
                    mPresenter.handleImageSelectorReturn(1, mPresenter.getChooseItemIndex(), backImgList);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @PermissionGrant(3)
    public void requestContactSuccess() {//请求了读写的权限之后
        //ArrayList<String> itemImageList = mHelpSignedPresenter.getItemImageList(chooseImageItemPosition);
        UIUtils.startImageSelectorForAddItem(mISRequestCode, mImagePathList, mContext, 4 - (mShowImageList.size() - mImagePathList.size()));
    }

    @PermissionDenied(3)
    public void requestContactFailed() {
        CustomToast.makeText(mContext, "DENY ACCESS CONTACTS!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
