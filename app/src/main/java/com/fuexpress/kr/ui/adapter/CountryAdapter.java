package com.fuexpress.kr.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.bean.SortCountrtyModel;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.CharacterParser;
import com.fuexpress.kr.utils.SPUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.fuexpress.kr.ui.uiutils.UIUtils.getResources;


/**
 * Created by k550 on 2016/3/23.
 */
public class CountryAdapter extends BaseAdapter {
    private CharacterParser characterParser=CharacterParser.getInstance();
    private List<SortCountrtyModel> mDates;
    private Context mContext;
    private boolean showNumber=true;
    private List<String> listStr=new ArrayList<>();
    public CountryAdapter(Context context, List<SortCountrtyModel> list,boolean showNumber) {
        mDates=list;
        this.mContext = context;
        this.showNumber=showNumber;
    }


    public CountryAdapter(Context context, List<SortCountrtyModel> list) {
        mDates=list;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        if (mDates != null) {
            return mDates.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDates != null) {
            return mDates.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View child = View.inflate(mContext, R.layout.item_for_country, null);

        TextView catalog=(TextView)child.findViewById(R.id.catalog);
        TextView countryTitle = (TextView) child.findViewById(R.id.countryText);
        TextView numberTitle = (TextView) child.findViewById(R.id.numberText);
        LinearLayout linearLayout=(LinearLayout)child.findViewById(R.id.line1);
        ImageView iv=(ImageView)child.findViewById(R.id.select_iv);
        if(showNumber){
            numberTitle.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }else {
            numberTitle.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        iv.setVisibility(View.GONE);

        SortCountrtyModel sortCountrtyModel=(SortCountrtyModel)getItem(position);
        if("select".equals(sortCountrtyModel.getCourntyCode())){
            iv.setVisibility(View.VISIBLE);
            countryTitle.setTextColor(Color.RED);
        }
        String pinyin = characterParser.getSelling(sortCountrtyModel.getCountry());
        String sortString = pinyin.substring(0, 1).toUpperCase();
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            catalog.setVisibility(View.VISIBLE);
            catalog.setText(sortString);
        } else {
            catalog.setVisibility(View.GONE);
        }
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        KLog.i("lang = "+language);
        if (!language.startsWith("en") && !language.startsWith("zh")) {
            catalog.setVisibility(View.GONE);
        }

        countryTitle.setText(sortCountrtyModel.getCountry());
        numberTitle.setText("+"+sortCountrtyModel.getPhone());
        return child;
    }
    public int getSectionForPosition(int position) {
        return mDates.get(position).getSortLetters().charAt(0);
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr =mDates.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }
}
