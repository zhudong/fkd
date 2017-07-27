package com.fuexpress.kr.ui.uiutils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;


import com.fuexpress.kr.bean.HelpItemViewBean;

/**
 * Created by Longer on 2016/12/5.
 * 动画的相关工具类,需要动画的可以在这里找或者添加
 */
public class AnimationUtils {

    /**
     * 专门给帮我系列里面的item做删除动画的工具
     *
     * @param helpItemViewBean item的View封装类
     * @param isOpen           开启Or关闭
     */
    public static void doDeletAnimation(HelpItemViewBean helpItemViewBean, boolean isOpen) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(helpItemViewBean.getLinearLayout(), View.TRANSLATION_X, isOpen ? 0 : -50, isOpen ? -50 : 0);
        objectAnimator.setDuration(helpItemViewBean.getDuration());
        objectAnimator.start();

        ObjectAnimator rotation = ObjectAnimator.ofFloat(helpItemViewBean.getImageView(), "rotation", 0f, 180f);
        //ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(helpItemViewBean.getImageView(), View.SCALE_X, isOpen ? 0 : 1, isOpen ? 1 : 0);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(helpItemViewBean.getImageView(), View.SCALE_Y, isOpen ? 0 : 1, isOpen ? 1 : 0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2, rotation);
        animatorSet.setDuration(helpItemViewBean.getDuration());
        helpItemViewBean.getImageView().setVisibility(View.VISIBLE);
        animatorSet.addListener(helpItemViewBean.getAnimatiorListener());
        animatorSet.start();
    }

}
