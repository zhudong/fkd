package com.fuexpress.kr.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseFragment;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.SortCountrtyModel;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.net.INetEngineListener;
import com.fuexpress.kr.net.NetEngine;
import com.fuexpress.kr.net.RequestNetListenerWithMsg;
import com.fuexpress.kr.ui.activity.ChooseCityActivity;
import com.fuexpress.kr.ui.adapter.CountryAdapter;
import com.fuexpress.kr.ui.view.SideBar;
import com.fuexpress.kr.utils.SPUtils;
import com.fuexpress.kr.utils.pinyinComparator;
import com.google.protobuf.GeneratedMessage;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fksproto.CsAddress;

/**
 * Created by root on 17-3-23.
 */

public class CityListFragment extends BaseFragment {
    @BindView(R.id.frame_view)
    FrameLayout mFrameView;
    private CsAddress.CountryRegionInfo mRegionInfoList;
    private CountryAdapter mAdapter;
    private ChooseCityActivity mChooseCityActivity;
    public static final String S_COUNTRY_CODE = "s_country_code";
    @BindView(R.id.lv_body)
    ListView mLvBody;
    @BindView(R.id.side_bar)
    SideBar mSideBar;
    @BindView(R.id.dialog)
    TextView mDialog;


    public static CityListFragment newInstance(Bundle args) {
        CityListFragment f = new CityListFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    public View initView() {
        View view = View.inflate(SysApplication.getContext(), R.layout.fragment_city_list, null);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);

        mSideBar.setTextView(mDialog);
        mSideBar.setVisibility(View.GONE);
      /*  mDialog.setVisibility(View.VISIBLE);
        mDialog.setText("A");*/

        Bundle bundle = getArguments();
        String code = bundle.getString(CityListFragment.S_COUNTRY_CODE);
        Activity activity = getActivity();

        if (activity instanceof ChooseCityActivity) {
            mChooseCityActivity=(ChooseCityActivity)activity;
            mRegionInfoList = mChooseCityActivity.getRegionInfoListByCode(code);

            final List<SortCountrtyModel> list = convertCity2CityModel(mRegionInfoList);
            SideBar bar = new SideBar(mChooseCityActivity, getCharList(mRegionInfoList));
            bar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener(){
                @Override
                public void onTouchingLetterChanged(String s) {
                    //����ĸ�״γ��ֵ�λ��
                    int position = mAdapter.getPositionForSection(s.charAt(0));
                    if(position != -1){
                        mLvBody.setSelection(position);
                    }

                }
            });
            mFrameView.addView(bar);


            pinyinComparator pinyincomparator = new pinyinComparator();
            Collections.sort(list, pinyincomparator);
            mAdapter=new CountryAdapter(getActivity(),list,false);
            mLvBody.setAdapter(mAdapter);
            mLvBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                    mChooseCityActivity.saveWareHouse(Integer.valueOf(list.get(position).getPhone()), new RequestNetListenerWithMsg(){
                        @Override
                        public void onSuccess(GeneratedMessage response) {
                            resetList(list);
                            list.get(position).setCourntyCode("select");
                            mChooseCityActivity.setCityList(Integer.valueOf(list.get(position).getPhone()));
                            mAdapter.notifyDataSetChanged();
                            SPUtils.putString(mChooseCityActivity,ChooseCityActivity.F_RECENT_CITY_ID,list.get(position).getPhone());
                            SPUtils.putString(mChooseCityActivity,ChooseCityActivity.F_RECENT_CITY,list.get(position).getCountry());
                            mChooseCityActivity.finish();
                        }

                        @Override
                        public void onFailure(int ret, String msg) {

                        }
                    });

                }
            });
            mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener(){
                @Override
                public void onTouchingLetterChanged(String s) {
                    //����ĸ�״γ��ֵ�λ��
                    int position = mAdapter.getPositionForSection(s.charAt(0));
                    if(position != -1){
                        mLvBody.setSelection(position);
                    }

                }
            });
        }
        return rootView;
    }

    private List<String> getCharList(CsAddress.CountryRegionInfo info) {
        List<String> list = new ArrayList<>();
        if (info == null) {
            return list;
        } else {
            List<CsAddress.RegionInfo> infos = info.getRegionInfoListList();
            for (int i = 0; i < infos.size(); i++) {
                String str = infos.get(i).getRegionFirstLetter();
                if (!list.contains(str)) {
                    list.add(str);
                }
            }
            list.add("#");
            return list;
        }
    }

    private void resetList(List<SortCountrtyModel> list) {
        if (list == null) {
            return;
        }
        for(SortCountrtyModel model:list){
            model.setCourntyCode("");
        }
    }
    //讲数据转换为适合listView的格式
    public  List<SortCountrtyModel> convertCity2CityModel(CsAddress.CountryRegionInfo info){
        List<SortCountrtyModel> list=new ArrayList<>();
        if(info==null){
            return list;
        }
        List<CsAddress.RegionInfo> infos=info.getRegionInfoListList();
        for(int i=0;i<infos.size();i++){
            CsAddress.RegionInfo regionInfo=infos.get(i);
            String firstLetter=regionInfo.getRegionFirstLetter();
            List<CsAddress.RegionCityInfo> regionCityInfos=regionInfo.getRegionCityInfoListList();
            for(int k=0;k<regionCityInfos.size();k++){

                CsAddress.RegionCityInfo cityInfo=regionCityInfos.get(k);
                SortCountrtyModel model=new SortCountrtyModel(cityInfo.getRegionName(),firstLetter,cityInfo.getRegionId(),"");
                if(model.getPhone().equals(SPUtils.getString(mChooseCityActivity,ChooseCityActivity.F_RECENT_CITY_ID,""))){
                    model.setCourntyCode("select");
                }
                list.add(model);
            }
        }
        return list;
    }
    private void saveWareHouse(int id){
        CsAddress.SaveWarehouseRegionRequest.Builder builder=CsAddress.SaveWarehouseRegionRequest.newBuilder();
        builder.setBaseuser(AccountManager.getInstance().getBaseUserRequest());
        builder.setRegionid(id);
        NetEngine.postRequest(builder, new INetEngineListener<CsAddress.SaveWarehouseRegionResponse>() {
            @Override
            public void onSuccess(CsAddress.SaveWarehouseRegionResponse response) {
                KLog.i("saveCity ",response.toString());
            }

            @Override
            public void onFailure(int ret, String errMsg) {

            }
        });
    }
}
