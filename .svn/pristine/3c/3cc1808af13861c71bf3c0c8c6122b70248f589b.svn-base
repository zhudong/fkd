package com.fuexpress.kr.ui.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.view.TitleBarView;
import com.fuexpress.kr.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fksproto.CsUser;

/**
 * Created by Administrator on 2017-2-14.
 */

public class FriendsActivity extends BaseActivity {

    private View rootView;
    private TitleBarView titleBarView;
    private ImageView backIv;
    private TextView backTv;

    private TextView invitedCountTv;
    private ListView listView;
    private List<CsUser.Invitees> inviteesList;
    private FriendAdapter friendAdapter;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_friends_layout, null);
        titleBarView = (TitleBarView) rootView.findViewById(R.id.friends_titlebar);
        backIv = titleBarView.getIv_in_title_back();
        backTv = titleBarView.getmTv_in_title_back_tv();
        backTv.setText(R.string.integral_management_msg);

        invitedCountTv = (TextView) rootView.findViewById(R.id.friends_invite_success_count_tv);
        listView = (ListView) rootView.findViewById(R.id.friends_list_view);
        init();
        backIv.setOnClickListener(this);
        backTv.setOnClickListener(this);
        return rootView;
    }

    public void init(){
        getInviteesListRequest();
        inviteesList = new ArrayList<CsUser.Invitees>();
        friendAdapter = new FriendAdapter(this, inviteesList);
        listView.setAdapter(friendAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_in_title_back:
            case R.id.tv_in_title_back_tv:
                finish();
                break;
        }
    }

    public void getInviteesListRequest(){
        CsUser.GetInviteesListRequest.Builder builder = CsUser.GetInviteesListRequest.newBuilder();
        builder.setUserinfo(AccountManager.getInstance().getBaseUserRequest());
        NetEngine.postRequest(builder, new INetEngineListener<CsUser.GetInviteesListResponse>() {

            @Override
            public void onSuccess(CsUser.GetInviteesListResponse response) {
                LogUtils.d(response.toString());
                if(response.getInviteeslistCount() > 0){
                    Message msg = Message.obtain();
                    msg.obj = response;
                    myHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            CsUser.GetInviteesListResponse response = (CsUser.GetInviteesListResponse) msg.obj;
            invitedCountTv.setText(response.getInviteeslistCount() + "");
            inviteesList.addAll(response.getInviteeslistList());
            friendAdapter.notifyDataSetChanged();
        }
    };

    private class FriendAdapter extends BaseAdapter{

        private Context context;
        private DisplayImageOptions options;
        private ImageLoader imageLoader;
        private List<CsUser.Invitees> inviteesList;

        public FriendAdapter(Context context, List<CsUser.Invitees> inviteesList){
            this.context = context;
            this.options = ImageLoaderHelper.getInstance(context).getDisplayOptions();
            this.imageLoader = ImageLoader.getInstance();
            this.inviteesList = inviteesList;
        }

        @Override
        public int getCount() {
            return inviteesList != null ? inviteesList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return inviteesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.friends_item, null);
                holder = new ViewHolder();
                holder.headIv = (CircleImageView) convertView.findViewById(R.id.friends_item_head_iv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.friends_item_name_tv);
                holder.timeTv = (TextView) convertView.findViewById(R.id.friends_item_time_tv);
                holder.statusTv = (TextView) convertView.findViewById(R.id.friends_item_status_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            CsUser.Invitees item = inviteesList.get(position);
            holder.nameTv.setText(getString(R.string.friends_name_2, item.getNickname()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sdf.parse(item.getCreatetime());
                holder.timeTv.setText(getString(R.string.friends_time_2, sdf.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            holder.statusTv.setText(getString(R.string.friends_status_2, item.get));
            String iconUrl = item.getIconurl();
            if(TextUtils.isEmpty(iconUrl) || TextUtils.isEmpty(iconUrl.split("!")[0])){
                holder.headIv.setImageResource(R.mipmap.me_photo);
            }else {
                imageLoader.displayImage(item.getIconurl(), holder.headIv, options);
            }
//            holder.headIv.setImageResource(R.drawable.ic_launcher);
            return convertView;
        }

        class ViewHolder{
            CircleImageView headIv;
            TextView nameTv;
            TextView timeTv;
            TextView statusTv;
        }
    }
}
