package com.fuexpress.kr.ui.activity.append_item;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.bean.CategoryBean;
import com.fuexpress.kr.bean.ItemAppendBean;
import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ItemUploadManager;
import com.fuexpress.kr.model.MaterialsManager;
import com.fuexpress.kr.ui.activity.MaterialsSearchActivity;
import com.fuexpress.kr.ui.activity.PreviewParceldiseActivity;
import com.fuexpress.kr.ui.activity.append_parcel.NumberFormate;
import com.fuexpress.kr.ui.activity.choose_category.ChooseCategoryActivity;
import com.fuexpress.kr.ui.activity.help_send.ParcelItemUseCase;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.InputInfoItemView;
import com.fuexpress.kr.ui.view.InputInfoItemView2;
import com.fuexpress.kr.ui.view.imageselector.ImageSelectorActivity;
import com.fuexpress.kr.utils.FloatUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.MyNumberFormat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A fragment with a Google +1 button.
 * Use the {@link AppendItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppendItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    @BindView(R.id.ll_phote_container)
    LinearLayout llPhoteContainer;
    @BindView(R.id.edt_item_info)
    EditText edtItemInfo;
    @BindView(R.id.edt_declare_price)
    EditText edtDeclarePrice;
    @BindView(R.id.edt_item_counts)
    EditText edtItemCounts;
    @BindView(R.id.img_append_photo)
    ImageView imgAppendPhoto;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.tv_price_type)
    TextView tvPriceCode;
    @BindView(R.id.item_materials)
    InputInfoItemView mItemMaterials;
    @BindView(R.id.item_category)
    InputInfoItemView mItemCategory;
    @BindView(R.id.item_brand_name)
    InputInfoItemView2 mItemBrandName;


    private List<String> mPathList;
    private AppendItemActivity mContext;
    private ItemUploadManager itemUploadManager;
    private Object failImages;
    private List<String> mComplent;
    private ItemAppendBean item;
    private int position;


    public AppendItemFragment() {
    }

    public static AppendItemFragment newInstance(ItemAppendBean item, int index, int parcelID) {
        AppendItemFragment fragment = new AppendItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, item);
        args.putSerializable(ARG_PARAM2, index);
        args.putSerializable(ARG_PARAM3, parcelID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = new ItemAppendBean();
        item.setmParcelId(getArguments().getInt(ARG_PARAM3));
        if (getArguments() != null) {
            ItemAppendBean.copy((ItemAppendBean) getArguments().getSerializable(ARG_PARAM1), item);
            position = getArguments().getInt(ARG_PARAM2);
        }
        mContext = (AppendItemActivity) getContext();
        mComplent = new ArrayList<>();
        mPathList = item.getPath();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_append_item, container, false);
        ButterKnife.bind(this, view);
        reInfo();
        item.formatPathImg(preImgs);
        appendPhotos(preImgs);
        if (item.getmItemID() != 0 || mContext.getItemSize() >= 3) {
            btnContinue.setVisibility(View.GONE);
        }
        initEvent();
        return view;
    }

    private void initEvent() {
        mItemCategory.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCategory();
            }
        });

        mItemMaterials.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMaterials();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mContext.showDelete(item.isAppended());
        String currencyCode = AccountManager.getInstance().getCurrencyCode();
        if (currencyCode.contains("CNY")) {
            edtDeclarePrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (currencyCode.contains("KRW") || currencyCode.contains("TWD")) {
        } else {
            edtDeclarePrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        tvPriceCode.setText(currencyCode);
    }

    @OnClick({R.id.img_append_photo, R.id.btn_continue, R.id.btn_save, R.id.item_materials, R.id.item_category})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_append_photo:
                MPermissions.requestPermissions(this, 6, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.btn_continue:
                mItemBrandName.saveBrandName();

                if (checkInput()) {
                    getInfo();
                    getLoadedPhoto(true);
                }
                break;
            case R.id.btn_save:
                if (checkInput()) {
                    getInfo();
                    getLoadedPhoto(false);
                }
                break;
            case R.id.item_materials:
//                MaterialsManager.getInstance().deleteAll();
                startMaterials();
                break;
            case R.id.item_category:
                startCategory();
                break;
        }
    }

    private void startMaterials() {
//        Intent intent = new Intent(mContext, MaterialsSearchActivity.class);
        Intent intent = new MaterialsSearchActivity.IntentBuilder(item.getMaterails() != null ? item.getMaterails().getId() : 0)
                .build(mContext, new MaterialsSearchActivity.iMaterialsSelectListener() {
                    @Override
                    public void select(boolean select, MaterialsBean bean) {
                        if (select) {
                            mItemMaterials.setText(bean.getName());
                            item.setMaterails(new MaterialsBean(bean.getId(), bean.getName(), bean.getTime()));
                        }
                    }
                });
        startActivity(intent);
    }


    private void startCategory() {
        ChooseCategoryActivity.IntentBuilder intentBuilder = ChooseCategoryActivity.IntentBuilder.build(mContext)
                .setWhereFrom(mContext.getString(R.string.string_append_item))
                .setParentID(-1)//设置父级分类ID
                .setSubID(-1);//设置子级分类ID
        if (item.getCategory() != null) {
            intentBuilder.setParentID(item.getCategory().getParentID()).setSubID(item.getCategory().getSubID());
        }

        intentBuilder.setListener(new ChooseCategoryActivity.ChooseCategoryListener() {//设置监听
            @Override
            public void select(boolean select, CategoryBean bean) {
                LogUtils.e(String.valueOf(select));
                if (select) {
//                            mTvChooseCategoryShow.setText(bean.getSubName());
                    CategoryBean category = new CategoryBean();
                    category.setParentID(bean.getParentID());
                    category.setParentName(bean.getParentName());
                    category.setSubID(bean.getSubID());
                    category.setSubName(bean.getSubName());
                    item.setCategory(category);
                    mItemCategory.setText(bean.getSubName());
                }
            }
        });
        startActivity(intentBuilder);
    }


    /*适配6.0 权限检查*/
    @PermissionGrant(6)
    public void requestContactSuccess() {
        UIUtils.startImageSelectorForAddItem(1001, (ArrayList) mPathList, this, 4);
    }

    @PermissionDenied(6)
    public void requestContactFailed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 || resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
            if (data != null) {
                mPathList.clear();
                mPathList.addAll(data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT));
//                appendPhotos(mPathList);
                processAppendPhoto();
                upLoadPhoto(mPathList);
            }
        }
        if (requestCode == PreviewParceldiseActivity.REQUEST_CODE_BACK_IMG_LIST) {
            if (data != null) {
                List<String> backList = (List<String>) data.getExtras().getSerializable("backImgList");
                item.getPath().clear();
                item.getPath().addAll(backList);
                List<String> imgs = item.getImgs();
                Log.d(AppendItemFragment.class.getSimpleName(), imgs.toString());
                processAppendPhoto();

            }
        }
    }

    private void processAppendPhoto() {
        item.formatPathImg(preImgs);
        appendPhotos(mPathList);
    }

    private void upLoadPhoto(List<String> mPathList) {
        if (itemUploadManager == null)
            itemUploadManager = new ItemUploadManager(AppendItemFragment.class.getSimpleName());
        itemUploadManager.addTask(mPathList);
    }

    public void appendPhoto(String url, int type) {
        int width = UIUtils.dip2px(73);
        int padding = UIUtils.dip2px(1);
        int margin = UIUtils.dip2px(8);
        ImageView imageView = new ImageView(getContext());
        imageView.setCropToPadding(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackground(getResources().getDrawable(R.drawable.shape_bg_rectangle_));
        DisplayImageOptions options = ImageLoaderHelper.getInstance(mContext).getDisplayOptions();

        if (type != 0) {
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } else {
            ImageLoader.getInstance().displayImage(url, imageView, options);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        imageView.setPadding(padding, padding, padding, padding);
        int index = llPhoteContainer.getChildCount() - 1 < 0 ? 0 : llPhoteContainer.getChildCount() - 1;
        params.setMargins(0, 0, margin, 0);
        llPhoteContainer.addView(imageView, index, params);
        int position = 0;
        for (int i = llPhoteContainer.getChildCount() - 1; i >= 0; i--) {
            if (imageView.equals(llPhoteContainer.getChildAt(i))) {
                position = i;
            }
        }
        imageView.setTag(position);
        imageView.setOnClickListener(new PhotoClickListener());
    }

    public void appendPhotos(List<String> urls) {
        llPhoteContainer.removeAllViews();
        llPhoteContainer.addView(imgAppendPhoto);

        for (int i = 0; i < urls.size(); i++) {
            String[] split = urls.get(i).split(",");
            if ("".equals(split[0]))
                continue;
            if (split.length > 1 && !"".equals(split[1])) {
                appendPhoto(split[1], 1);
            } else {
                String s = "file://" + split[0];
                appendPhoto(s, 0);
            }
        }
        if (urls.size() >= 4) {
            imgAppendPhoto.setVisibility(View.GONE);
        } else {
            imgAppendPhoto.setVisibility(View.VISIBLE);
        }
    }

    public void getLoadedPhoto(final boolean newItem) {
        item.setAppended(true);
        if (itemUploadManager == null) {
            mContext.saveItem(item, position, new ParcelItemUseCase.IOperareResult() {
                @Override
                public void onResult(boolean success, int itemID, String msg) {
                    if (!success) {
                        CustomToast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newItem) {
                        mContext.appendNewItem(true);
                    } else {
                        mContext.close();
                    }
                }
            });

        } else {
            mContext.showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, mContext.getString(R.string.string_img_uploading1));
            mComplent.clear();
            if (itemUploadManager != null) {
                itemUploadManager.getAllComplete(new ItemUploadManager.UpLoadeCompleteListener() {
                    @Override
                    public void complete(final float prgress, final ArrayMap map) {
                        if (failImages != null) failImages = null;
                        mContext.showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, mContext.getString(R.string.string_img_uploading) + Math.round(prgress) + "%");
                        if (prgress >= 100) {
                            for (String key : mPathList) {
                                String value = (String) map.get(key);
                                if (value == null || value.equals(item.getPath_img().get(key)))
                                    continue;
                                item.getPath_img().put(key, value);
                            }
                            mContext.showProgressDiaLog(SweetAlertDialog.PROGRESS_TYPE, mContext.getString(R.string.string_img_uploading) + Math.round(100) + "%");
                            mContext.closeLoading();
                            item.getImgs();
                            mContext.saveItem(item, position, new ParcelItemUseCase.IOperareResult() {
                                @Override
                                public void onResult(boolean success, int itemID, String msg) {
                                    if (success) {
                                        if (newItem) {
                                            mContext.appendNewItem(true);
                                        } else {
                                            mContext.close();
                                        }
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void error(List<String> urls) {

                    }
                });
            }
        }
    }

    public boolean checkInput() {
        if (item.getPath() == null || item.getPath().size() <= 0) {
            CustomToast.makeText(mContext, R.string.item_please_input_img, Toast.LENGTH_SHORT).show();
            return false;
        }
      /*  if (TextUtils.isEmpty(mItemMaterials.getValue())) {
            CustomToast.makeText(mContext, R.string.item_please_input_materials, Toast.LENGTH_SHORT).show();
            return false;
        }*/

        if (TextUtils.isEmpty(mItemCategory.getValue())) {
            CustomToast.makeText(mContext, R.string.item_please_input_category, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (TextUtils.isEmpty(edtDeclarePrice.getText().toString())) {
            CustomToast.makeText(mContext, R.string.item_please_input_price, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Float aFloat =  FloatUtils.vlaueOf(edtDeclarePrice.getText().toString().replace(",", ""));
            if (aFloat <= 0) {
                CustomToast.makeText(mContext, R.string.item_input_price_less, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (TextUtils.isEmpty(edtItemCounts.getText().toString())) {
            CustomToast.makeText(mContext, R.string.item_please_input_num, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Integer integer = Integer.valueOf(edtItemCounts.getText().toString());
            if (integer < 1) {
                CustomToast.makeText(mContext, R.string.item_input_num_less, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public void getInfo() {
        Float price =  FloatUtils.vlaueOf(edtDeclarePrice.getText().toString().replace(",", ""));
        Integer num = Integer.valueOf(edtItemCounts.getText().toString());
        String info = edtItemInfo.getText().toString();
        item.setPrice(price);
        item.setNum(num);
        item.setInfo(info);
//        item.setMaterails(mItemMaterials.getValue());
//        item.setCategory(mItemCategory.getValue());
        item.setBrandName(mItemBrandName.getValue());
    }

    public void reInfo() {
        String currencyCode = AccountManager.getInstance().getCurrencyCode();
        if (item.getPrice() <= 0) {
            edtDeclarePrice.setText("");
        } else {
            edtDeclarePrice.setText(NumberFormate.getCurrency(mContext, currencyCode, item.getPrice()));
        }
        if (item.getNum() <= 0) {
            edtItemCounts.setText("");
        } else {
            edtItemCounts.setText(MyNumberFormat.format(item.getNum(), 0));
        }
        edtItemInfo.setText(item.getInfo());
        mItemBrandName.setText(item.getBrandName());
        mItemCategory.setText(item.getCategory() != null ? item.getCategory().getSubName() : "");
        mItemMaterials.setText(item.getMaterails() != null ? item.getMaterails().getName() : "");
    }


    List<String> preImgs = new ArrayList<>();


    class PhotoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int postioin = (int) v.getTag();
            Intent intent = new Intent();
            intent.setClass(mContext, PreviewParceldiseActivity.class);
            Bundle bundle = new Bundle();
            item.formatPathImg(preImgs);
            bundle.putSerializable("imgList", (Serializable) preImgs);
            intent.putExtra("resourceType", PreviewParceldiseActivity.RESOURCE_TYPE_ADD);
            bundle.putInt("position", postioin);
            intent.putExtras(bundle);
            startActivityForResult(intent, PreviewParceldiseActivity.REQUEST_CODE_BACK_IMG_LIST);
        }
    }

    private void reSetimgs() {
        if (item == null || item.getPath() == null || item.getImgs() == null) return;
        for (int i = 0; i < item.getPath().size(); i++) {
            for (String img : item.getImgs()) {

            }
        }
    }
}
