package com.fuexpress.kr.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.SimpleAlbumBean;
import com.fuexpress.kr.model.AlbumManager;
import com.fuexpress.kr.model.ItemBean;
import com.fuexpress.kr.ui.activity.CreatAlbumListActivity;
import com.fuexpress.kr.ui.adapter.AlbumListDialogAdapter;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by longer on 2017/7/7.
 */

public class AlbumListDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshListView.OnRefreshListener {

    Context context;
    private RelativeLayout mRl_for_fragment_home_02_addalbum;
    private TextView mTv_for_fragment_home_02_addalbum;
    private ProgressBar mPb_for_fragment_home_02_addalbum;
    private RelativeLayout mRl_add_for_add_albums_dialog_creat;
    ItemBean mItemBean;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayOptions;
    public AlbumListDialogAdapter mAlbumListDialogAdapter;
    private List<SimpleAlbumBean> mSimpleAlbumBeans;
    private RefreshListView mRfl_for_add_albums_dialog;
    private boolean mIsHasMore;
    private int mPage = 1;


    public AlbumListDialog(Context context, ItemBean itemBean) {
        super(context);
        this.context = context;
        mItemBean = itemBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = SysApplication.getImageLoader();
        mDisplayOptions = ImageLoaderHelper.getInstance(context).getDisplayOptions(5);
        init();
    }


    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_albums_layout, null);
        setContentView(view);

        EventBus.getDefault().register(this);

        ListView lv_for_add_albums_dialog = (ListView) view.findViewById(R.id.lv_for_add_albums_dialog);
        lv_for_add_albums_dialog.setOnItemClickListener(this);
        ImageView iv_head_for_add_albums_dialog = (ImageView) view.findViewById(R.id.iv_head_for_add_albums_dialog);
        ImageView iv_close_for_add_albums_dialog = (ImageView) view.findViewById(R.id.iv_close_for_add_albums_dialog);
        TextView tv_desc_for_add_albums_dialog = (TextView) view.findViewById(R.id.tv_desc_for_add_albums_dialog);
        mRl_for_fragment_home_02_addalbum = (RelativeLayout) view.findViewById(R.id.rl_for_fragment_home_02_addalbum);
        mTv_for_fragment_home_02_addalbum = (TextView) view.findViewById(R.id.tv_for_fragment_home_02_addalbum);
        mPb_for_fragment_home_02_addalbum = (ProgressBar) view.findViewById(R.id.pb_for_fragment_home_02_addalbum);
        mRl_add_for_add_albums_dialog_creat = (RelativeLayout) view.findViewById(R.id.rl_add_for_add_albums_dialog_creat);
        mRfl_for_add_albums_dialog = (RefreshListView) view.findViewById(R.id.rfl_for_add_albums_dialog);
        mRfl_for_add_albums_dialog.setHeaderViewHide();
        mRfl_for_add_albums_dialog.setOnRefreshListener(this);
        mRfl_for_add_albums_dialog.setOnItemClickListener(this);

        mImageLoader.displayImage(mItemBean.getImageUrl() + "!small9", iv_head_for_add_albums_dialog, mDisplayOptions);
        //String title = mItemBean.getTitle();
        tv_desc_for_add_albums_dialog.setText(mItemBean.getTitle());
/*
        //拿到用户的图集集合:
        mSimpleAlbumBeans = AlbumManager.getInstance().simpleAlbumBeans;

        //作为参数传递给Adapter:
        mAlbumListDialogAdapter = new AlbumListDialogAdapter(context, mSimpleAlbumBeans);
        lv_for_add_albums_dialog.setAdapter(mAlbumListDialogAdapter);
        */
        iv_close_for_add_albums_dialog.setOnClickListener(this);
        initData(mPage);
        mRl_add_for_add_albums_dialog_creat.setOnClickListener(this);
    }

    private void initData(int i) {
        AlbumManager.getInstance().getSimpleAlbumListDatas(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_for_add_albums_dialog:
                dismiss();
                break;
            case R.id.rl_add_for_add_albums_dialog_creat:
                Intent intent = new Intent();
                intent.setClass(context, CreatAlbumListActivity.class);
                context.startActivity(intent);
                //    dismiss();
                break;
        }

    }


    public void onEventMainThread(BusEvent event) {
        switch (event.getType()) {
            case BusEvent.CREAT_ALBUM_SUCCESS:
                mSimpleAlbumBeans = AlbumManager.getInstance().simpleAlbumBeans;
                mAlbumListDialogAdapter.notifyDataSetChanged();
                //AlbumManager.getInstance().getSimpleAlbumList();
                //   show();
                break;
            case BusEvent.ADD_ITEM_TO_ALBUM_SUCCESS:
                //Toast.makeText(context, "加入图集成功!", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new BusEvent(BusEvent.ADD_ITEM_TO_ALBUM_SUCCESS_SHOW_DIALOG, null));
                dismiss();
                break;
            case BusEvent.ADD_ITEM_TO_ALBUM_FAIL:
                //Toast.makeText(context, "加入图集失败!", Toast.LENGTH_SHORT).show();
                mTv_for_fragment_home_02_addalbum.setText(SysApplication.getContext().getString(R.string.add_to_album_failed));
                SysApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 1000);
                break;
            case BusEvent.GET_SIMPLE_ALBUM_SUCCESS:
                mIsHasMore = event.getBooleanParam();
                mRfl_for_add_albums_dialog.stopLoadMore(true);
                mRfl_for_add_albums_dialog.setHasLoadMore(mIsHasMore);
                List<SimpleAlbumBean> simpleAlbumBeans = AlbumManager.getInstance().simpleAlbumBeans;
                if (mAlbumListDialogAdapter == null) {
                    mAlbumListDialogAdapter = new AlbumListDialogAdapter(context, simpleAlbumBeans);
                    mRfl_for_add_albums_dialog.setAdapter(mAlbumListDialogAdapter);
                } else {
                    mAlbumListDialogAdapter.setmList(simpleAlbumBeans);
                    mAlbumListDialogAdapter.notifyDataSetChanged();
                }

                break;
            case BusEvent.REQUEST_FAIL:
                CustomToast.makeText(context, "request fail:" + event.getStrParam(), Toast.LENGTH_SHORT).show();
                break;

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<SimpleAlbumBean> simpleAlbumBeans = AlbumManager.getInstance().simpleAlbumBeans;
        //一旦被点击了,我们就拿到相应的item去添加:
        if (position >= simpleAlbumBeans.size())
            position -= 1;
        SimpleAlbumBean simpleAlbumBean = simpleAlbumBeans.get(position);
        long album_id = simpleAlbumBean.getAlbum_id();
        long itemid = mItemBean.getItemid();
        String title = mItemBean.getTitle();
        String itemImageUrl = mItemBean.getImageUrl();
        mRl_for_fragment_home_02_addalbum.setVisibility(View.VISIBLE);
        AlbumManager.getInstance().addItemToAlbumRequest(itemid, title, album_id, itemImageUrl);


    }


    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mRfl_for_add_albums_dialog.stopRefresh();
        return;
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mIsHasMore)
            initData(++mPage);

    }
}
