package com.fuexpress.kr.bean;

import com.fuexpress.kr.R;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.UIUtils;

import fksproto.CsBase;
import fksproto.CsOrder;

/**
 * Created by andy on 2017/7/6.
 */

public class OrderConstants {
    public static String getOrderState(int i, SalesOrderItemBean item) {
        String state = "";
        switch (i) {
            case 1:
                state = UIUtils.getString(R.string.pending_adapter);
                break;
            case 2:
                state = UIUtils.getString(R.string.wait_to_do);
                break;

            case 3:
                state = UIUtils.getString(R.string.check_item);
                break;

            case 4:
                state = UIUtils.getString(R.string.to_store);
                break;

            case 5:
                state = UIUtils.getString(R.string.package_attend_Warehouse);
                break;

            case 6:
                state = UIUtils.getString(R.string.package_waite_send);
                break;

            case 7:
                state = UIUtils.getString(R.string.package_has_sended);
                break;

            case 8:
                state = UIUtils.getString(R.string.wait_cancel);
                break;

            case 9:
                state = UIUtils.getString(R.string.card_order_detail_title_bar_canceled);
                break;

            case 10:
                state = UIUtils.getString(R.string.crowding);
                break;
        }

        if (item != null && item.state == CsOrder.SalesOrderItemState.SALES_ORDER_ITEM_STATE_PREORDERING_VALUE) {
            state += " " + item.prompt;
            return state;
        }

        if (item != null && i == 5 && item.qty_pack < item.qty) {
            state += " " + UIUtils.getString(R.string.packing_num, item.qty_pack);
        }
        return state;
    }

    public static String getPayMethod(int i) {
        switch (i) {
            case 2:
                return UIUtils.getContext().getString(R.string.String_wechat_pay);
            case 1:
                return UIUtils.getString(R.string.String_ali_pay);
            case 3:
                return UIUtils.getString(R.string.gift_pay);
            case 4:
                return UIUtils.getContext().getString(R.string.credit_card);
            case 5:
                return UIUtils.getContext().getString(R.string.String_krbank_pay2);
            case 6:
                return "DAOUPAY";
        }
        return "unknown";
    }

    public static final String[] PAY_METHOD = {"0",
            "alipay",
            "wechat",
            "giftcard",
            "Adyen",
            "krbank",
    };


  /*  GIFT_CARD_PAYMENT_ADYEN =
    GIFT_CARD_PAYMENT_KRBANK

     =
    GIFT_CARD_PAYMENT_BALANCE
            GIFT_CARD_PAYMENT_DAOUPAY*/

    public static String getPayCode(int i) {
        switch (i) {
            case CsBase.PayMethod.PAY_METHOD_ALIPAY_VALUE:
                return Constants.GIFT_CARD_PAYMENT_ALIPAY;
            case CsBase.PayMethod.PAY_METHOD_WXPAY_VALUE:
                return Constants.GIFT_CARD_PAYMENT_WXPAY;
            case CsBase.PayMethod.PAY_METHOD_GIFTCARD_VALUE:
                return Constants.GIFT_CARD_PAYMENT_BALANCE;
            case CsBase.PayMethod.PAY_METHOD_ADYEN_VALUE:
                return Constants.GIFT_CARD_PAYMENT_ADYEN;
            case CsBase.PayMethod.PAY_METHOD_KRBANK_VALUE:
                return Constants.GIFT_CARD_PAYMENT_KRBANK;
            case CsBase.PayMethod.PAY_METHOD_DAOUPAY_VALUE:
                return Constants.GIFT_CARD_PAYMENT_DAOUPAY;
        }
        return "unknown";
    }

    public static int getPayType(String code, int defaultType) {
        if (Constants.GIFT_CARD_PAYMENT_ALIPAY.equals(code)) {
            return CsBase.PayMethod.PAY_METHOD_ALIPAY_VALUE;
        }
        if (Constants.GIFT_CARD_PAYMENT_WXPAY.equals(code)) {
            return CsBase.PayMethod.PAY_METHOD_WXPAY_VALUE;
        }
        if (Constants.GIFT_CARD_PAYMENT_BALANCE.equals(code)) {
            return CsBase.PayMethod.PAY_METHOD_GIFTCARD_VALUE;
        }
        if (Constants.GIFT_CARD_PAYMENT_ADYEN.equals(code)) {
            return CsBase.PayMethod.PAY_METHOD_ADYEN_VALUE;
        }
        if (Constants.GIFT_CARD_PAYMENT_KRBANK.equals(code)) {
            return CsBase.PayMethod.PAY_METHOD_KRBANK_VALUE;
        }
        if (Constants.GIFT_CARD_PAYMENT_DAOUPAY.equals(code)) {
            return CsBase.PayMethod.PAY_METHOD_DAOUPAY_VALUE;
        }
        return defaultType;
    }


}
