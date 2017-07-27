package com.fuexpress.kr.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fuexpress.kr.MainActivity;
import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.ui.activity.my_package.content.PackageAll;
import com.fuexpress.kr.ui.activity.my_package.content.PackageDelivered;
import com.fuexpress.kr.ui.activity.my_package.content.PackageToSendNow;
import com.fuexpress.kr.ui.activity.my_package.content.PackageWaitReceive;
import com.fuexpress.kr.ui.fragment.myneed.ToPayFragment;
import com.fuexpress.kr.ui.view.NoteRadioGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Longer on 2016/10/26.
 */
public class MyPackageFragment extends BaseFragment<MainActivity> {
    public static final String TAB_TO_SEND = "tab_to_send";
    public static final String TAB_TO_RECEIVE = "tab_to_receive";
    public static final String TAB_DELIVERED = "tab_delivered";
    public static final String TAB_ALL = "tab_all";
    @BindView(R.id.rbg_my_need_fragment)
    NoteRadioGroup rbgMyNeedFragment;
    @BindView(R.id.toSendCountTV)
    TextView mToSendCountTV;
    private FragmentManager mFragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        return View.inflate(mContext, R.layout.package_fragment_layout, null);
    }

    @Override
    public void initData() {
        mFragmentManager = getChildFragmentManager();
        rbgMyNeedFragment.setOnCheckedChangeListener(new NoteRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(NoteRadioGroup group, int checkedId) {
                switchFragment(checkedId);
            }
        });
        rbgMyNeedFragment.check(R.id.rb_to_pay);
    }

    private void switchFragment(int checkedId) {
        transaction = mFragmentManager.beginTransaction();
        List<Fragment> fragments = mFragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment frg : fragments) {
                transaction.hide(frg);
            }
        }
        transaction.show(getFragment(checkedId));
        transaction.commitAllowingStateLoss();
        if (rbgMyNeedFragment.getCheckedRadioButtonId() != checkedId)
            rbgMyNeedFragment.check(checkedId);
    }


    private Fragment getFragment(int status) {
        PackageToSendNow fragment = null;
        switch (status) {
            case R.id.rb_to_pay:
                fragment = (PackageToSendNow) mFragmentManager.findFragmentByTag(TAB_TO_SEND);
                if (fragment == null) {
                    fragment = new PackageToSendNow();
                    transaction.add(R.id.fl_parcel_container, fragment, TAB_TO_SEND);
                } else {
                    fragment.reloade();
                }
                break;
            case R.id.rb_instorage:
                fragment = (PackageToSendNow) mFragmentManager.findFragmentByTag(TAB_TO_RECEIVE);
                if (fragment == null) {
                    fragment = new PackageWaitReceive();
                    transaction.add(R.id.fl_parcel_container, fragment, TAB_TO_RECEIVE);
                } else {
                    fragment.reloade();
                }
                break;
            case R.id.rb_cancel:
                fragment = (PackageToSendNow) mFragmentManager.findFragmentByTag(TAB_DELIVERED);
                if (fragment == null) {
                    fragment = new PackageDelivered();
                    transaction.add(R.id.fl_parcel_container, fragment, TAB_DELIVERED);
                } else {
                    fragment.reloade();
                }
                break;
            case R.id.rb_all:
                fragment = (PackageToSendNow) mFragmentManager.findFragmentByTag(TAB_ALL);
                if (fragment == null) {
                    fragment = new PackageAll();
                    transaction.add(R.id.fl_parcel_container, fragment, TAB_ALL);
                } else {
                    fragment.reloade();
                }
                break;
        }
        return fragment;
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        if (event.getType() == BusEvent.GO_SUBMIT_PARCEL) {
//            switchFragment(R.id.rb_to_pay);
            if (rbgMyNeedFragment.getCheckedRadioButtonId() == R.id.rb_to_pay) {
                PackageToSendNow fragment = (PackageToSendNow) mFragmentManager.findFragmentByTag(TAB_TO_SEND);
                if (fragment != null)
                    fragment.reloade();
            } else {
                rbgMyNeedFragment.check(R.id.rb_to_pay);
            }
        }
    }

    public void setNeedPayCount(int count) {
        if (count > 0) {
            mToSendCountTV.setVisibility(View.VISIBLE);
            mToSendCountTV.setText(count + "");
        } else {
            mToSendCountTV.setVisibility(View.GONE);
        }
    }
}
