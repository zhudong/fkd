package com.fuexpress.kr.ui.activity.my_package.content;

/**
 * Created by yuan on 2016-6-15.
 */
public class PackageDelivered extends PackageToSendNow {
    @Override
    protected String getTab() {
        return "Shipped";
    }

    @Override
    protected String getType() {
        return "all";
    }
}
