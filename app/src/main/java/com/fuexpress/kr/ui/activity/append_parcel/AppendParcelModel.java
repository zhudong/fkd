package com.fuexpress.kr.ui.activity.append_parcel;

import java.util.List;

import fksproto.CsBase;

/**
 * Created by yuan on 2016-6-14.
 */
public class AppendParcelModel implements ParcelAppendContract.Model {
    @Override
    public List<CsBase.ShippingMethod> getShippings() {
        return null;
    }

    @Override
    public float getShippingFee() {
        return 0;
    }
}
