package com.fuexpress.kr.ui.activity.package_detail;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.bean.ShareFriendsBean;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.model.ShareManager2Friend;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.fragment.MyBottomSheetDialogFragment;
import com.fuexpress.kr.ui.view.CustomToast;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsMy;

/**
 * Created by andy on 2017/10/12.
 */

public class SendMethodListFragment extends MyBottomSheetDialogFragment {

    private static final String REMIXER_TAG = "";
    @BindView(R.id.lv_body)
    ListView mLvBody;
    private Object syncLock = new Object();
    private boolean isAddingFragment;
    private ShareManager2Friend mShareManager2Friend;
    private Activity mContext;
    static int mId;
    static int mCount;
    static String mNote;


    public static SendMethodListFragment newInstance(int parcelID, int count, String string) {
        mId = parcelID;
        mCount = count;
        mNote = string;
        return new SendMethodListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_fu_methods, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (Activity) getContext();
        mShareManager2Friend = new ShareManager2Friend(mContext);
        Integer[] shareMethods = new Integer[]{ShareManager2Friend.WECHAT, ShareManager2Friend.WECHAT_FRIEND};
        mShareManager2Friend.setmMethods(Arrays.asList(shareMethods));
        mShareManager2Friend.initView(mLvBody, mContext);
        mShareManager2Friend.setOnClickShare(new ShareManager2Friend.onShareListener() {
            @Override
            public void onClick(int index) {
                sendShare(mShareManager2Friend, index);
            }

            @Override
            public void onResult(boolean success) {
                if (success) {
//                    dismiss();
//                    BaseActivity activity = (BaseActivity) getActivity();
//                    activity.initView();
                }
            }
        });
    }


    private void sendShare(final ShareManager2Friend shareManager, final int index) {
        final BaseActivity activity = (BaseActivity) getActivity();
        activity.showLoading();

        CsMy.DoShareBagRequest.Builder builder = CsMy.DoShareBagRequest.newBuilder();
        builder.setUserHead(AccountManager.getInstance().getBaseUserRequest()).setLocaleCode(AccountManager.getInstance().getLocaleCode())
                .setParcelid(mId)
                .setQtySplit(mCount)
                .setMessage(mNote);
        NetEngine.postRequest(builder, new INetEngineListener<CsMy.DoShareBagResponse>() {
            @Override
            public void onSuccess(final CsMy.DoShareBagResponse response) {
                ShareFriendsBean shareFriendsBean = new ShareFriendsBean();
                String msg = response.getMsg();
                String this_is = getString(R.string.string_this_is);
                if (!msg.contains(this_is)) {
                    msg = this_is + msg;
                }
                shareFriendsBean.setTitle(msg);
                if (TextUtils.isEmpty(mNote)) mNote = getString(R.string.string_send_fu_message);
                shareFriendsBean.setInfo(mNote);
                shareFriendsBean.setUrl(response.getBagUrl());
                shareFriendsBean.setBitmap(response.getIconUrl());
                shareManager.setShareInfo(shareFriendsBean);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.closeLoading();
                        shareManager.processShare(index);
                    }
                });
            }

            @Override
            public void onFailure(int ret, final String errMsg) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.closeLoading();
                        CustomToast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void attachToButton(final FragmentActivity activity, View button) {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showRemixer(activity.getSupportFragmentManager(), REMIXER_TAG);
            }
        });
    }

    public void showRemixer(FragmentManager manager, String tag) {
        synchronized (syncLock) {
            if (!isAddingFragment && !isAdded()) {
                isAddingFragment = true;
                show(manager, tag);
            }
        }
    }

    @Override
    public void onResume() {
        isAddingFragment = false;
        super.onResume();
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        dismiss();
    }
}
