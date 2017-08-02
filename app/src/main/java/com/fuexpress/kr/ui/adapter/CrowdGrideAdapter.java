package com.fuexpress.kr.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.ui.activity.crowd.CrowdDetailActivity;
import com.fuexpress.kr.ui.uiutils.ImageLoaderHelper;
import com.fuexpress.kr.ui.view.CrowdProgressDetail;
import com.fuexpress.kr.utils.LgUitl;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.ArrayList;
import java.util.List;

import fksproto.CsCrowd;


/**
 * Created by yuan on 2016-4-5.
 */
public class CrowdGrideAdapter extends CrowdAdapter implements View.OnClickListener {

    public CrowdGrideAdapter(Activity content, List<CsCrowd.Crowd> crowds) {
        super(content, crowds);
    }

    @Override
    public int getCount() {
        if (mCrowds != null) {
            return (mCrowds.size() + 1) / 2;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        ArrayList<Object> list;
        if (mCrowds != null) {
            list = new ArrayList<>();
            list.add(mCrowds.get(position * 2));

            if ((position * 2 + 1) <= mCrowds.size() - 1) {
                list.add(mCrowds.get(position * 2 + 1));
            }
            return list;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        List<CsCrowd.Crowd> list = (List<CsCrowd.Crowd>) getItem(position);
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContent, R.layout.item2_for_crowd_list, null);
            for (int i = 0; i < 2; i++) {
                View child = ((LinearLayout) convertView).getChildAt(i);
                holder.holderChildrens[i].root = child;
                holder.holderChildrens[i].mCover = (SelectableRoundedImageView) child.findViewById(R.id.img_crowd_cover);
                holder.holderChildrens[i].mCrowdName = (TextView) child.findViewById(R.id.tv_crowd_name);
                holder.holderChildrens[i].mCrowdProgressDetail = (CrowdProgressDetail) child.findViewById(R.id.progress_detail);
                holder.holderChildrens[i].mTvDisCount = (TextView) child.findViewById(R.id.tv_discount_tag);
            }
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.size() > 1) {
                holder.holderChildrens[0].root.setTag(list.get(0));
                holder.holderChildrens[0].root.setOnClickListener(this);
                holder.holderChildrens[1].root.setTag(list.get(1));
                holder.holderChildrens[1].root.setOnClickListener(this);
                holder.holderChildrens[1].root.setVisibility(View.VISIBLE);
            } else {
                holder.holderChildrens[0].root.setTag(list.get(0));
                holder.holderChildrens[0].root.setOnClickListener(this);
                holder.holderChildrens[1].root.setVisibility(View.INVISIBLE);
            }
            showChild(holder.holderChildrens[i], list.get(i));
        }
        return convertView;
    }


    private void showChild(Holder.HolderChild holder, final CsCrowd.Crowd crowd) {
        ImageLoader.getInstance().displayImage(crowd.getLogo() + Constants.ImgUrlSuffix.mob_list, holder.mCover, ImageLoaderHelper.getInstance(mContent).getDisplayOptions());
        List<CsCrowd.CrowdLadder> laddersList = crowd.getLaddersList();
        holder.mTvDisCount.setVisibility(View.INVISIBLE);

        String sDisCount = mContent.getResources().getString(R.string.String_discount_tag);
        String sDisCountInt = mContent.getResources().getString(R.string.String_discount_tag_int);
        boolean en = LgUitl.isOtherLanguage(AccountManager.getInstance().getLocaleCode());
        for (CsCrowd.CrowdLadder ladder : laddersList) {
            float discount = 0;
            if (en) {
                discount = ladder.getDiscount() * 100;
            } else {
                discount = (1 - ladder.getDiscount()) * 10;
            }
            if (discount != 0) {
                holder.mTvDisCount.setVisibility(View.VISIBLE);
                if (isInt(discount)) {
                    holder.mTvDisCount.setText(String.format(sDisCountInt, (int) discount));
                } else {
                    holder.mTvDisCount.setText(String.format(sDisCount, formateNum(discount)));
                }
            }
            if (en) {
                if (discount < 0 || ladder.getDiscountType() == 1) {
                    holder.mTvDisCount.setVisibility(View.INVISIBLE);
                }
            } else {
                if (discount >= 10 || ladder.getDiscountType() == 1) {
                    holder.mTvDisCount.setVisibility(View.INVISIBLE);
                }
            }
        }
        holder.mCrowdProgressDetail.setData(crowd);
        holder.mCrowdName.setText(crowd.getName());
       /* holde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, CrowdDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CrowdDetailActivity.CROWD_ID, crowd);
                intent.putExtra(CrowdDetailActivity.CROWD_ID, bundle);
                mContent.startActivity(intent);
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        CsCrowd.Crowd crowd = (CsCrowd.Crowd) v.getTag();
        Intent intent = new Intent(mContent, CrowdDetailActivity.class);
        intent.putExtra(CrowdDetailActivity.CROWD_ID, crowd.getCrowdId());
        mContent.startActivity(intent);
    }

    static class Holder {
        HolderChild[] holderChildrens = new HolderChild[]{new HolderChild(), new HolderChild()};

        class HolderChild {
            View root;
            SelectableRoundedImageView mCover;
            TextView mCrowdName;
            TextView mTvDisCount;
            CrowdProgressDetail mCrowdProgressDetail;
        }
    }

    private boolean isInt(float num) {
        String sNum = String.format(mContent.getResources().getString(R.string.String_float_1), num);
        String start = ((int) num) + "";
        String suffix = sNum.substring(start.length() + 1, sNum.length());
        Integer value = Integer.valueOf(suffix);
        if (value > 0) {
            return false;
        } else {
            return true;
        }
    }


    public void setData(List<CsCrowd.Crowd> crowdList) {
        this.mCrowds = crowdList;
    }

/*    public void notiRefresh() {
        this.notifyDataSetChanged();
    }*/


}
