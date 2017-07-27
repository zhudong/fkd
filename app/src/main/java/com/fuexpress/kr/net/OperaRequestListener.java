package com.fuexpress.kr.net;

/**
 * @创建者 陈粤龙
 * @创时间 2016/4/5
 * @描述 是否喜欢或者关注的操作接口回调
 * @版本 $Rev:
 * @更新者 $Author:
 * @更新时间 $Date:
 * @更新描述 TODO
 */

public interface OperaRequestListener {
    void onOperaSuccess();

    void onOperaFailure();
}
