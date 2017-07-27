package com.fuexpress.kr.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017-4-12.
 */

public class AdyenUserInfoBean implements Serializable{
    public String creationDate;
    public List<Details> details;
    public String shopperReference;
    public String invalidOneclickContracts;
    private String cvc;

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public static class Details implements Serializable{

        public RecurringDetail RecurringDetail;
        public static class RecurringDetail implements Serializable{
            public String acquirer;
            public String acquirerAccount;

            public AdditionalData additionalData;
            public String alias;
            public String aliasType;
            public Card card;
            public List<String> contractTypes;
            public String creationDate;
            public String firstPspReference;
            public String paymentMethodVariant;
            public String recurringDetailReference;
            public String variant;

            public static class AdditionalData implements Serializable{
                public String cardBin;
            }

            public static class Card implements Serializable{
                public String expiryMonth;
                public String expiryYear;
                public String holderName;
                public String number;
            }

//            public static class ContractTypes{
//                public String ONECLICK;
//                public String RECURRING;
//            }
        }

    }
}
