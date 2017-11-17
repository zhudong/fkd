package com.fuexpress.kr.ui.uiutils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.fuexpress.kr.bean.AnimationBean;
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


    /**
     * Y坐标平移的动画
     *
     * @param helpItemViewBean item的View封装类
     * @param isOpen           开启Or关闭
     */
    public static void doTranslationYAnimation(AnimationBean helpItemViewBean, boolean isOpen) {
        int span = UIUtils.dip2px(helpItemViewBean.getSpan());
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(helpItemViewBean.getTranYView(), View.TRANSLATION_Y, isOpen ? span : 0, isOpen ? 0 : span);
        objectAnimator.setDuration(helpItemViewBean.getDuration());
        if (helpItemViewBean.getAnimatiorListener() != null)
            objectAnimator.addListener(helpItemViewBean.getAnimatiorListener());
        objectAnimator.start();

    }

    /**
     * Y坐标平移的动画
     *
     * @param helpItemViewBean item的View封装类
     */
    public static void doRotationAnimation(AnimationBean helpItemViewBean) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(helpItemViewBean.getRotateView(), "rotation", helpItemViewBean.getFromRotateAngle(), helpItemViewBean.getToRotateAngle());
        rotation.setDuration(helpItemViewBean.getDuration());
        if (helpItemViewBean.getAnimatiorListener() != null)
            rotation.addListener(helpItemViewBean.getAnimatiorListener());
        rotation.start();
    }


    public static void doHomeBtnAnimation(AnimationBean animationBean, boolean isOpen) {
        int span = UIUtils.dip2px(animationBean.getSpan());
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationBean.getTranYView(), View.TRANSLATION_Y, isOpen ? span : 0, isOpen ? 0 : span);

        ObjectAnimator rotation = ObjectAnimator.ofFloat(animationBean.getRotateView(), "rotation", 0f, 45f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, rotation);
        animatorSet.setDuration(animationBean.getDuration());
        if (animationBean.getAnimatiorListener() != null)
            animatorSet.addListener(animationBean.getAnimatiorListener());
        animatorSet.start();
    }


}
