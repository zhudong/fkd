package com.fuexpress.kr.ui.fragment.myparcel;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.bean.NeedBean;
import com.fuexpress.kr.ui.adapter.ParcelItemsAdapter;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ToSendFragment extends BaseFragment<MainActivity> {
    @BindView(R.id.lv_body)
    ListView lvBody;
    @BindView(R.id.rl_hint)
    RelativeLayout rlHint;

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.fragment_to_send, null);
    }

    @Override
    public void initData() {
        String[] strings = {"http://car2.autoimg.cn/cardfs/product/g23/M0D/90/01/u_autohomecar__wKgFV1bSTEqAYEMEAAoYNQ1knhY263.jpg", "http://car2.autoimg.cn/cardfs/product/g23/M0D/90/01/u_autohomecar__wKgFV1bSTEqAYEMEAAoYNQ1knhY263.jpg", "http://car2.autoimg.cn/cardfs/product/g23/M0D/90/01/u_autohomecar__wKgFV1bSTEqAYEMEAAoYNQ1knhY263.jpg", "http://car2.autoimg.cn/cardfs/product/g23/M0D/90/01/u_autohomecar__wKgFV1bSTEqAYEMEAAoYNQ1knhY263.jpg"};
        ArrayList<NeedBean> beans = new ArrayList<>();
        beans.add(new NeedBean("2012-12-12", "帮我买", "去付款", strings));
        beans.add(new NeedBean("2014-12-12", "帮我买", "已入库", strings));
        beans.add(new NeedBean("2014-12-12", "帮我买", "已取消", strings));
        lvBody.setAdapter(new ParcelItemsAdapter(mContext, beans));
    }
}
