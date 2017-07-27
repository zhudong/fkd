package com.fuexpress.kr.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.BaseActivity;
import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.bean.SortCountrtyModel;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.activity.choose_address.AddressBundleBean;
import com.fuexpress.kr.ui.activity.choose_address.AddressBundleBeanFactory;
import com.fuexpress.kr.ui.activity.choose_address.AddressResponBean;
import com.fuexpress.kr.ui.activity.choose_address.RegionDataManager;
import com.fuexpress.kr.ui.adapter.CountryAdapter;
import com.fuexpress.kr.ui.adapter.NewCountryAdapter;
import com.fuexpress.kr.ui.view.CustomToast;
import com.fuexpress.kr.ui.view.RefreshListView;
import com.fuexpress.kr.utils.CharacterParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fksproto.CsAddress;

/**
 * Created by k550 on 2016/3/22.
 */
public class ChooseCountryActivity extends BaseActivity implements RefreshListView.OnRefreshListener {
    private final static String TAG = "chooseCountry";
    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.title_tv_center)
    TextView mTitleTvCenter;
    private RelativeLayout mTitleView;
    private TextView cancelText;
    private CountryAdapter countryAdapter;
    private ListView listView;
    private List<SortCountrtyModel> courtrys;
    private CharacterParser characterParser;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textview_title_left:
                    finish();
                    break;
            }
        }
    };
    private RefreshListView mRfl_regisCountry;
    private View mRootView;
    private int mPageNum = 1;
    private boolean mIsHasMore = false;
    private List<CsAddress.DirectoryCountryInfo> mDirectoryCountryInfos;
    private NewCountryAdapter mNewCountryAdapter;

    @Override
    public View setInitView() {
        mRootView = View.inflate(this, R.layout.activity_choose_country, null);

        /*listView = (ListView) view.findViewById(R.id.countryList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortCountrtyModel sortCountrtyModel = (SortCountrtyModel) countryAdapter.getItem(position);
                Log.i(TAG, " countryCode = " + sortCountrtyModel.getCourntyCode());
                AccountManager.getInstance().userCountry = sortCountrtyModel.getCountry();
                AccountManager.getInstance().userNumber = sortCountrtyModel.getPhone();
                AccountManager.getInstance().mCountryCode=sortCountrtyModel.getCourntyCode();
                finish();
            }
        });*/
        return mRootView;

    }

    @Override
    public void initView() {
        /*pinyinComparator pinyincomparator = new pinyinComparator();
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        courtrys = filledData(getResources().getStringArray(R.array.date));
        Collections.sort(courtrys, pinyincomparator);
        countryAdapter = new CountryAdapter(ChooseCountryActivity.this, courtrys);
        listView.setAdapter(countryAdapter);*/
        mRfl_regisCountry = (RefreshListView) mRootView.findViewById(R.id.rfl_regisCountry);
        mRfl_regisCountry.setHeaderViewHide();
        mRfl_regisCountry.setOnRefreshListener(this);
        mRfl_regisCountry.setOnItemClickListener(this);
        initData(mPageNum);
    }

    private void initData(int pageNum) {
        AddressBundleBean addressBundleSortBean = AddressBundleBeanFactory.getInstance().getAddressBundleSortBean(pageNum);
        RegionDataManager.getInstance().getCountryListData(addressBundleSortBean);
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortCountrtyModel> filledData(String[] date) {
        List<SortCountrtyModel> mSortList = new ArrayList<SortCountrtyModel>();

        for (int i = 0; i < date.length; i++) {
            SortCountrtyModel sortModel = new SortCountrtyModel();

            String dataStr = date[i].toString();
            dataStr.replace(" ", "");
            String[] str1 = date[i].split(",");
            sortModel.setCountry(str1[0]);
            sortModel.setPhone(str1[1]);
            sortModel.setCourntyCode(str1[2]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        mTitleTvCenter.setText(getString(R.string.String_choose_country));
    }

    @OnClick({R.id.title_iv_left, R.id.title_tv_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
            case R.id.title_tv_left:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh(RefreshListView refreshListView) {
        mRfl_regisCountry.stopRefresh();
    }

    @Override
    public void onLoadMore(RefreshListView refreshListView) {
        if (mIsHasMore) {
            initData(++mPageNum);
        }
    }

    @Override
    public void onEventMainThread(BusEvent event) {
        super.onEventMainThread(event);
        switch (event.getType()) {
            case BusEvent.GET_REGION_COMPLETE:
                if (event.getBooleanParam()) {
                    mRfl_regisCountry.stopLoadMore(true);
                    AddressResponBean respBean = (AddressResponBean) event.getParam();
                    // TODO: 2017/6/6 获取数据,填充RefListView
                    if (mDirectoryCountryInfos == null) mDirectoryCountryInfos = new ArrayList<>();
                    mDirectoryCountryInfos = respBean.getDirectoryCountryInfos();
                    if (mNewCountryAdapter == null) {
                        mNewCountryAdapter = new NewCountryAdapter(this, mDirectoryCountryInfos);
                        mRfl_regisCountry.setAdapter(mNewCountryAdapter);
                    } else {
                        mNewCountryAdapter.setData(mDirectoryCountryInfos);
                        mNewCountryAdapter.notifyDataSetChanged();
                    }
                    mIsHasMore = respBean.isHasMore();
                    mRfl_regisCountry.setHasLoadMore(mIsHasMore);
                } else {
                    CustomToast.makeText(this, event.getStrParam(), Toast.LENGTH_SHORT).show();
                    mRfl_regisCountry.stopLoadMore(false);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CsAddress.DirectoryCountryInfo directoryCountryInfo = mDirectoryCountryInfos.get(position-1);
        AccountManager.getInstance().userCountry = directoryCountryInfo.getDirectoryCountryName();
        AccountManager.getInstance().userNumber = directoryCountryInfo.getAreaCode();
        AccountManager.getInstance().mCountryCode = directoryCountryInfo.getDirectoryCountryCode();
        finish();
    }
}
