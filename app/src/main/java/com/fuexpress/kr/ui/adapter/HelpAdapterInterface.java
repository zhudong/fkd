package com.fuexpress.kr.ui.adapter;

import android.widget.EditText;

import com.fuexpress.kr.ui.view.InputBoxView;

/**
 * Created by Longer on 2016/12/5.
 * 用来让Adapter给调用的Activity交互的接口
 */
public interface HelpAdapterInterface {

    /**
     * 当点击了删除按钮
     *
     * @param index 被点击的数据在集合中的下标
     */
    void deletData(int index);

    /**
     * 添加
     * 请自行去调用的Context中添加ProductDetailList
     */
    void addData();

    /**
     * 修改数据
     *
     * @param editText 被点击的EditText
     * @param index    被编辑的数据在集合中的下标
     * @param key      被点击的ipBoxView在你传进来的map中的key(由此来产生一一对应关系)
     *                 最好是对应的String资源ID
     */
    void editData(EditText editText, int index, int key);


    /**
     * 其中一种图片被点击了,按照需求是进入预览界面
     *
     * @param index    被点击的数据在集合中的下标
     * @param position 该图片在其集合中的位置
     */
    void editImage(int index, int position);


    /**
     * 添加图片按钮被点击
     * 请在调用的Activity或者Fragment中自行打开图片选择器
     *
     * @param index 被点击的数据在集合中的下标
     */
    void addImage(int index);


    /**
     * 这个是中间的TextView按钮被点击之后的回调方法
     *
     * @param inputBoxView 被点击的inputBox对象
     * @param index        被点击的数据在其集合中的下标
     */
    void centerTvOnClick(InputBoxView inputBoxView, int index);

    /**
     * 这个是中间的TextView按钮被点击之后的回调方法
     *
     * @param index 被点击的数据在其集合中的下标
     */
    void centerTvOnClick(int index);


}
