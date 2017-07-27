package com.fuexpress.kr.utils;

import com.fuexpress.kr.ui.view.InputBoxView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Longer on 2016/12/8.
 * 专门用于生产InPutBox的工厂类
 */
public class InPutBoxViewFactory {

    private static final int MAX_NORMAL_SIZA = 15;
    private static final int MAX_WH_SIZA = 5;
    private static final int MAX_BELOW_SIZZ = 5;

    private static ArrayList<InputBoxView> mNormalIPList = new ArrayList<>();
    private static ArrayList<InputBoxView> mWareHouseIPList = new ArrayList<>();
    private static ArrayList<InputBoxView> mBelowIPList = new ArrayList<>();

    @Nullable
    private static InPutBoxViewFactory sInPutBoxViewFactory = null;

    @NonNull
    Context mContext;


    private InPutBoxViewFactory(Context context) {
        mContext = checkNotNull(context, "Context is Not Null!");

    }

    public static InPutBoxViewFactory getInstance(@NonNull Context context) {
        if (sInPutBoxViewFactory != null)
            return sInPutBoxViewFactory;
        else {
            sInPutBoxViewFactory = new InPutBoxViewFactory(context);
            return sInPutBoxViewFactory;
        }
    }

    /**
     * 生产普通InputBoxView的方法
     *
     * @param leftText 左边的文字
     * @param hinText  输入框的默认底纹
     * @return InputBoxView
     */
    public InputBoxView createNormalInputBoxView(String leftText, String hinText) {
        InputBoxView inputBoxView = new InputBoxView(mContext);
        inputBoxView.setIpTextAndHint(leftText, hinText);
        return inputBoxView;
    }


    /**
     * 生产带底部文字的InputBoxView的方法
     *
     * @param left    左边文字
     * @param below   下边的文字
     * @param hinText 输入框底纹
     * @return InputBoxView
     */
    public InputBoxView createBelowInputBoxView(String left, String below, String hinText) {
        InputBoxView inputBoxView = new InputBoxView(mContext);
        inputBoxView.changeIpType(InputBoxView.BELOW_SHOW_TYPE);
        inputBoxView.setIpTextAndHint(left, hinText);
        inputBoxView.setIpTextBelow(below);
        return inputBoxView;
    }


    /**
     * 生产仓库选择样式的InputBoxView的方法
     *
     * @param left       左边的文字
     * @param centerText 中间的文字
     * @return InputBoxView
     */
    public InputBoxView createWareHouseInputBoxView(String left, String centerText) {
        InputBoxView inputBoxView = new InputBoxView(mContext);
        inputBoxView.changeIpType(InputBoxView.WARE_HOUSE_TYPE);
        inputBoxView.setCenterTextView(centerText);
        inputBoxView.setIpTextLeft(left);
        return inputBoxView;
    }


    /**
     * 生产价格输入样式的InputBoxView的方法
     *
     * @param leftText     左边文字
     * @param hintText     输入框底纹
     * @param currencyCode 右边文字,这里是货币符号
     * @return InputBoxView
     */
    public InputBoxView createPriceInputBoxView(String leftText, String hintText, String currencyCode) {
        InputBoxView inputBoxView = new InputBoxView(mContext);
        inputBoxView.changeIpType(InputBoxView.PRICE_TYPE);
        inputBoxView.setIpTextAndHint(leftText, hintText);
        inputBoxView.setIpTextRight(currencyCode);
        return inputBoxView;
    }


}
