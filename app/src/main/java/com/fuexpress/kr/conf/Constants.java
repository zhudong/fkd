package com.fuexpress.kr.conf;

import android.app.Activity;
import android.text.TextUtils;

import com.fuexpress.kr.R;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.FileUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.TimeUtils;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Longer on 2016/10/26.
 */
public class Constants {
    public static int urlIndex = 0;


    public static String POST_URL;

    //正式环境
    //public final static String POST_URL_OFFICIAL = "http://app.fu-express.com:80";//正式域名地址app.fukuaisong.com:80
    public final static String POST_URL_OFFICIAL = "http://app.fu-express.com:80";
    public final static String POST_URL_TEST_ZUO = "http://61.153.100.19:9000";
    public final static String POST_PRE_OFFICIAL = "http://106.75.133.251:8000";
    public final static String POST_URL_TEST_ZUO2 = "http://106.75.132.57:80";
    public final static String POST_URL_TEST = "http://106.75.133.253:8000";
    public final static String POST_URL_TEST_TIAN = "http://106.75.133.253:8000";

    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;

    /*//支付Url
    //购物车订单
    public static final String CART_PAY_URL = "http://122.226.100.91/adyen/payment/orderType/0";
    //充值卡订单
    public static final String CARD_PAY_URL = "http://122.226.100.91/adyen/payment/orderType/1";*/

    //使用Adyen支付前，调用该接口
    public static String URL_ADYEN_IS_SAVE_USER_INFO;
    public static final String URL_ADYEN_IS_SAVE_USER_INFO_OFFICIAL = "http://wap.fu-express.com/adyen/listRecurringDetails/";
    public static final String URL_ADYEN_IS_SAVE_USER_INFO_TEST = "http://122.226.100.91/adyen/listRecurringDetails/";

    //支付Url
    //购物车订单
    public static String CART_PAY_URL;
    public static final String CART_PAY_URL_OFFICIAL = "http://wap.fu-express.com/adyen/payment/orderType/0";
    public static final String CART_PAY_URL_TEST = "http://122.226.100.91/adyen/payment/orderType/0";
    //充值卡订单
    public static String CARD_PAY_URL;
    public static final String CARD_PAY_URL_OFFICIAL = "http://wap.fu-express.com/adyen/payment/orderType/1";
    public static final String CARD_PAY_URL_TEST = "http://122.226.100.91/adyen/payment/orderType/1";

    //已保存Adyen信息的支付链接
    public static String CART_PAY_SAVED_URL;
    public static final String CART_PAY_SAVED_URL_OFFICIAL = "http://wap.fu-express.com/adyen/recurringPay/orderType/0";
    public static final String CART_PAY_SAVED_URL_TEST = "http://122.226.100.91/adyen/recurringPay/orderType/0";
    public static String CARD_PAY_SAVED_URL;
    public static final String CARD_PAY_SAVED_URL_OFFICIAL = "http://wap.fu-express.com/adyen/recurringPay/orderType/1";
    public static final String CARD_PAY_SAVED_URL_TEST = "http://122.226.100.91/adyen/recurringPay/orderType/1";

    //正式环境
    public static final String ADYEN_PUBLIC_KEY_OFFICIAL = "10001|B1CDA457C08FB0FF945916E2DCDDB1790C9F043C46C6621F28EFF89648F05DF0E634AC8A75C128897A62F66AABDCD7405A2D9D36A2F42EA183DAFC356468BD22209CC720AFAD1B615CBAB89CC6857E2D3546874E7B80EF970666457A062C009ACBB09DFC22EAB4E0348A269805784D0695D058B66F1BE78082D9260552DED242F48C747127A7776A138481445E374163A1E846D07C377C05C27A24062F69969336B4FE6EFDEB19B866B9B46E47B89FE4B7F947B29E74D232BEDADE10565EB73EBF4E1CD68264A8678A288275903CC47B0BB8E5D216AADBC40741621E9F74C8869535600AA306608AEFB405B1AAC8E03343E5B6392D4D9F6E173090F41EA96B35";
    public static final String CART_PAY_TOKEN_OFFICIAL = "2614624419150117";
    //测试环境
    public static final String ADYEN_PUBLIC_KEY_TEST = "10001|E507658201297D760908A26F2B3B78A95ADC64E571380DD3D8E5BC559A44D94C70633C7E5F13E8D47B435DBDE8D779658F6BE04B4961F5EFA9B675ABDEA0E021FA8143B84E6764F014B2FFE4BA74FD658606D74CF06705CA59706721CA3322B3BC0352C2315466F905ADF256B8CD919980C09146D7EE2F79605F0B271E58E0ECA7924B9C239D69B4E926084EDE661D6A7BD6036FC7588B31444CFDF5EB4656D8D5187DF65A6B4F6A5481394B823224E6E6CD162FC846EBF17ADEA0AE651D1AA47524AF3C1F02684B5BD8EA1EF21FF40CCF9896350782970DED031BA4697FF5A8B222FB7F38E1B94F4D83F04C17C6A562456297CC39D96D98BCD5DC985A962BCF";
    public static final String CART_PAY_TOKEN_TEST = "7614623465784315";
    //信用卡支付
    public static String ADYEN_PUBLIC_KEY;
    public static String CART_PAY_TOKEN;

    //上传头像需要用到的
    public static String secret = "qHTwFO1qPb+9BzAIpv5bQmtAc34=";
    public static String bucket = "fkdimage";
    public static String IMAGE_URL_OFFICIAL = "http://fkdimage.b0.upaiyun.com";
    public static final String UPLOAD_IMG_FOR_ADD_ITEM = "/fksapp/item/" + FileUtils.getTimeFileSuffix(TimeUtils.getDateStyleStringEndHour());
    public static final String UPLOAD_IMG_FOR_ADD_PACKAGE = "/fksapp/parcel/" + FileUtils.getTimeFileSuffix(TimeUtils.getDateStyleStringEndHour());
    public static final String UPLOAD_IMG_FOR_ORDER_SHOW = "/fksapp/review/" + FileUtils.getTimeFileSuffix(TimeUtils.getDateStyleStringEndHour());
    public static final String UPLOAD_IMG_FOR_MERCHANT_USERINFO = "/fksapp/userinfo/";
    public static final String UPLOAD_IMG_FOR_MERCHANT_COVER = "/fksapp/merchant/";

    //Chatra URL
    public static String URL_CHATRA_OFFICIAL;
    public static final String URL_CHATRA_Test = "http://122.226.100.91/m/chatra?decorator=empty&localeCode=";
    public static final String URL_CHATRA_OFFICIAL_release = "http://wap.fu-express.com/m/chatra?decorator=empty&localeCode=";

    //充值卡id的key
    public static final String GIFT_CARD_ORDER_ID = "gift_card_order_id";

    //充值卡状态的key
    public static final String GIFT_CARD_ORDER_STATE = "gift_card_order_state";
    //充值卡类型的key
    public static final String GIFT_CARD_ORDER_TYPE = "gift_card_order_type";

    //充值卡类型  充值卡的标记
    public static final int GIFT_CARD_ORDER_TYPE_RECHARGE = 0x1017;

    //充值卡类型  直充值卡的标记
    public static final int GIFT_CARD_ORDER_TYPE_PREPAID = 0x1018;

    public static final int CARD_ORDER_PAYMENT_REQUEST_CODE = 0x1016;
    public static final int CONFIRM_REQUEST_CODE = 0x1013;
    public static final int PAY_TYPE_SHARE_CODE = 0x2010;

    public static final String GIFT_CARD_PAYMENT_ADYEN = "adyen";
    public static final String GIFT_CARD_PAYMENT_KRBANK = "krbank";
    public static final String GIFT_CARD_PAYMENT_ALIPAY = "alipay";
    public static final String GIFT_CARD_PAYMENT_WXPAY = "wxap";
    public static final String GIFT_CARD_PAYMENT_BALANCE = "balance";
    public static final String GIFT_CARD_PAYMENT_DAOUPAY = "daoupay";

    public static final String SP_REPLENISH_LIST = "sp_replenish_list";

    public static final int PAYMENT_BANLANCE = 0;
    public static final int PAYMENT_ALIPAY = 1;
    public static final int PAYMENT_WECHAT = 2;
    public static final int PAYMENT_ADYEN = 4;
    public static final int PAYMENT_KRBANK = 5;
    public static final int PAYMENT_DAOUPAY = 6;
    public static final int PAYMENT_REQUEST_CODE = 0x1011;
    //充值卡状态  待付款的标记
    public static final int GIFT_CARD_ORDER_STATE_PENDING = 0x1019;
    //充值卡状态  已取消的标记
    public static final int GIFT_CARD_ORDER_STATE_CANCELED = 0x1020;
    //充值卡状态  已支付的标记
    public static final int GIFT_CARD_ORDER_STATE_PAID = 0x1021;

    //支付相关
    public static final int PAYMENT_REQUEST_CODE_CARD_CART = 0x1015;
    public static final int PAYMENT_REQUEST_CODE_SHOP_CART = 0x1014;
    public static final int PAYMENT_REQUEST_CODE_PICK_UP = 0x1016;
    public static final int PAYMENT_REQUEST_CODE_PACKAGE_DETAIL = 0x1023;
    public static final int PAYMENT_REQUEST_CODE_PARCEL_DETAIL_EMPTY = 0x1022;
    public static final String WX_APP_ID = "wx9bbbc9ca8d6fe854";

    public static final int SHIPPING_SCHEME_DIRECT = 1;//直邮订单
    public static final int SHIPPING_SCHEME_MERGE = 2;//合并订单
    public static final int PURCHASE_SCHEME_STOCK = 1;//仅采购现货
    public static final int PURCHASE_SCHEME_BOOK = 2; //可预订
    public static final int DELIVERY_REQUEST_CODE = 0x1010;
    public static final String GIFT_CARD_PAYMENT_WXCHAT = "wxap";
    public static final int CODE_ADD_TO_CART_ERROR = -72;


    //选择分类需要传的参数:
    public static final String KEY_PARENTID = "PARENTID";
    public static final String KEY_SUBID = "SUBID";
    public static final String KEY_WHERE_FROM = "WHEREFROM";

    //网易七鱼的appkey:
    public static final String WYQY_APP_KEY = "879d6db69a4ee55d765c0cdcc0e6d001";

    //是否读取过地址信息标记:
    public static final String KEY_IS_ADDRESS_DB_READY = "IS_ADDRESS_DB_READY";
    public static final String KEY_ADDRESS_UPDATE_TIME = "DDRESS_UPDATE_TIME";

    //是否需要显示首页的切换城市蒙版:
    public static final String KEY_IS_SHOW_HM_MASK = "IS_SHOW_HM_MASK";

    //是否需要显示帮我收的蒙版
    public static final String KEY_IS_SHOW_HS_MASK = "IS_SHOW_HS_MASK";

    //返回标题栏的标记:
    public static final String RETURN_TITLE = "return_title";


    public static Map<String, String> sStatusMap = new HashMap<>();
    public static Map<String, String> sPaymentMap = new HashMap<>();
    public static Map<String, Integer> sPayTypeMap = new HashMap<>();

    public class LOGIN_TYPE {
        public static final String LOGIN_TYPE = "login_type";
        public static final String LOGIN_ENAME = "login_name";
        public static final String LOGIN_PASSWORD = "login_password";

    }

    public static String[] REQUIRETYPE = {"",
            UIUtils.getString(R.string.home_fg_help_01),
            UIUtils.getString(R.string.help_me_find),
            UIUtils.getString(R.string.home_fg_help_03),
            UIUtils.getString(R.string.home_fg_help_02)
    };

    public static String getType(Activity activity, int type) {
        if (type < 1 || type > 4) {
            return "";
        } else {
            switch (type) {
                case 1:
                    return activity.getString(R.string.home_fg_help_01);
                case 2:
                    return activity.getString(R.string.help_me_find);
                case 3:
                    return activity.getString(R.string.home_fg_help_03);
                case 4:
                    return activity.getString(R.string.home_fg_help_02);
            }
        }
        return "";
    }

    public static final int ADDRESS_REQUEST_CODE = 0x1012;

    public interface ImgUrlSuffix {
        //用户头像
        String small_9 = "!small9";
        //单品列表
        String dp_tall = "!dptall";

        //单品列表 正方形
        String dp_list = "!dplist";
        //详情大图
        String small = "!small";
        //图集小图
        String dp_small = "!dpsmall";
        //拼单列表
        String crowd_list = "!crowdlist";
        //feed列表:
        String feed_list = "!feedlist";
        //moblist:
        String mob_list = "!moblist";

        //bizlist
        String biz_list = "!bizlist";

        //crowd_order
        String crowd_order = "!crowdorder";
    }


    public class USER_INFO {
        public static final String USER_MUIN = "user_muin";
        public static final String USER_H5TICKET = "user_h5ticket";
        public static final String USER_ITCKET = "user_ticket";
        public static final String USER_SESSION_KEY = "user_sessionkey";
        public static final String USER_ACCOUNT = "user_account";
        public static final String USER_PWD = "user_pwd";
        public static final String USER_ACCOUNT_TYPE = "user_account_type";
        public static final String USER_ACCOUNT_EXTRA = "account_extra";
        public static final String USER_LOCELA_CODE = "locale_code";
        public static final String USER_CURRENCY_CODE = "currency_code";
        public static final String USER_CURRENCY_ID = "currency_id";
        public static final String USER_CURRENCY_STRING = "user_currency_string";
        public static final String USER_CURRENCY_SIGN = "currency_sign";
        public static final String USER_CURRENCY_NAME = "currency_name";
        public static final String USER_IS_DEFAULT_LANGUAGE = "is_default_language";
        public static final String HELP_SIGNED_DATA = "help_signed_data";
    }


    //我的订单请求类型
    public static final String ORDER_STATUS_PROCESSING = "Processing";
    public static final String ORDER_STATUS_PREORDER = "Preorder";
    public static final int ALBUM_RESULT_CODE = 0x1000;
    public static final int ALBUM_GET_BITMAP_REQUEST_CODE = 0x1001;

    public static final int CREATE_ALBUM_CLASSIFY_REQUEST_CODE = 0x1002;
    public static final int CREATE_ALBUM_REQUEST_CODE = 0x1003;

    //商品分类的来源ID
    public static final int CLASSIFY_FROM_EDIT = 0x1005;
    public static final int CLASSIFY_FROM_ADD = 0x1006;
    //编辑商品页面  Intent key
    public static final String KEY_EDIT_MATCH_ITEM = "matchitemid";
    public static final String KEY_EDIT_ALBUM_ID = "albumId";
    public static final int EDIT_GET_BITMAP_REQUEST_CODE = 0x1004;

    public static class WebWiewUrl {
        public static String H5_URL;
        public static String H5_URL_OFFICIAL = "http://wap.fu-express.com";
        public static String H5_TEST = "http://122.226.100.91";
        public static String IMAGE_URL_OFFICIAL = "http://dimage.yissimg.com";
        //        static String H5_TEST = "http://115.231.94.200:8080";

        public static String ITEM_DETAIL = "/app/item/";
        public static String CROWD_PRODUCT_DETAIL = "/crowdProductDetail/";
        public static String CROWD_DETAIL = "/crowdDetail/";
        public static String SHOW_CART = "/onepage/delivery/";
        public static String ADD_ADDRESS = "/app/addCustomerAddress";
        public static String EDIT_ADDRESS = "/app/editCustomerAddress";
        public static String GIFT_CARD_TERMS_AND_CONDITIONS = "/m/gift-card-terms-and-conditions";


        public static String SHARE_ITEM = "/items/1%/2%";
        public static String SHARE_ALBUM = "/collections/1%";
        public static String SHARE_CROWD_DETAIL = "/crowdorders/1%";
        public static String SHARE_CROWD_ATTEND = "/crowdorders/1%";
        public static String SHARE_SHOP = "/bizs/1%";
        public static String SHARE_URL_SUFFIX = "?in=";

        public static String NO_TITLE = "?decorator=empty";
        public static String NO_NAV = "&isAppH5=1";

        public static String getUrl(String url) {
            switch (urlIndex) {
                case 0:
                    H5_URL = WebWiewUrl.H5_TEST + url;
                    break;
                case 1:
                    H5_URL = WebWiewUrl.H5_URL_OFFICIAL + url;
                    break;
                case 2:
                    H5_URL = WebWiewUrl.H5_URL_OFFICIAL + url;
                    break;
            }
            return H5_URL;
        }

        public static String getH5Url() {
            switch (urlIndex) {
                case 0:
                    H5_URL = WebWiewUrl.H5_TEST;
                    break;
                case 1:
                    H5_URL = WebWiewUrl.H5_URL_OFFICIAL;
                    break;
                case 2:
                    H5_URL = WebWiewUrl.H5_URL_OFFICIAL;
                    break;
            }
            return H5_URL;
        }
    }

    //美洽APP Key
    public static final String MEIQIA_APP_KEY = "2edb7d1e1ac0416d3303f397309b0abb";
    public static final String MEIQIA_APP_GROUP = "cacac65641a7b92ca94d99c5dfc202e3";

    //微信第三方登录、分享配置_福快递
    public class WeiXin {
        public static final String WX_APPID_SHARE = "wx9bbbc9ca8d6fe854";
        public static final String WX_APPID = "wx9bbbc9ca8d6fe854";//wx373c6c9af1c34815
        public static final String WX_APPSECRET = "75df3284d9203b71dc7d90bcc7187cfe";
        public static final String WX_APPSECRET_SHARE = "75df3284d9203b71dc7d90bcc7187cfe";
    }

    //QQ第三方登录，分享配置_福快递
    public class QQ {
        // 在QQ开发者平台申请的APPID
        public static final String QQ_APPID = "1105716549";
        public static final String QQ_APPKEY = "S9JNdXQVqj64UVz4";
        public static final String QQ_OPENDID = "QQ_openid";
        public static final String QQ_ACCESS_TOKEN = "QQ_access_token";
        public static final String QQ_EXPIRES_IN = "QQ_expires_in";
        public static final String QQ_START_TIME = "QQ_start_time";
        public static final String QQ_END_TIME = "QQ_end_time";
    }

    //微信支付失败
    public static final int CODE_WECHAT_PAY_FAILED = -1;
    //微信支付成功
    public static final int CODE_WECHAT_PAY_SUCCESS = 0;
    //微信支付取消
    public static final int CODE_WECHAT_PAY_CANCEL = -2;

    public static class SHARE_CLEINT_NAME {
        //QQ分享
        public static final String QQ_SHARE_PACKAGE_NAME = "com.tencent.mobileqq";
        public static final String QQ_SHARE_ACTIVITY_NAME = "com.tencent.mobileqq.activity.JumpActivity";
        //QQ空间分享
        public static final String QQ_ZONE_SHARE_PACKAGE_NAME = "com.qzone";
        public static final String QQ_ZONE_SHARE_ACTIVITY_NAME = "com.qzonex.module.operation.ui.QZonePublishMoodActivity";
        //QQ收藏
        public static final String QQ_ADD_SHARE_PACKAGE_NAME = "com.tencent.mobileqq";
        public static final String QQ_ADD_SHARE_ACTIVITY_NAME = "cooperation.qqfav.widget.QfavJumpActivity";
        //微信好友分享
        public static final String WX_FRIEND_SHARE_PACKAGE_NAME = "com.tencent.mm";
        public static final String WX_FRIEND_SHARE_ACTIVITY_NAME = "com.tencent.mm.ui.tools.ShareImgUI";
        //微信朋友圈分享
        public static final String WX_TIMELINE_SHARE_PACKAGE_NAME = "com.tencent.mm";
        public static final String WX_TIMELINE_SHARE_ACTIVITY_NAME = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
        //微信收藏
        public static final String WX_ADD_SHARE_PACKAGE_NAME = "com.tencent.mm";
        public static final String WX_ADD_SHARE_ACTIVITY_NAME = "com.tencent.mm.ui.tools.AddFavoriteUI";


    }

    public static class DemandStatus {
        public static String STATUS_PENDING = "Pending";
        public static String STATUS_ALLOCATING = "Allocating";
        public static String STATUS_GRABBING = "Grabbing";
        public static String STATUS_PROCESSING = "Processing";
        public static String STATUS_PREORDER = "Preorder";
        public static String STATUS_PACKING = "Packing";
        public static String STATUS_SHIPPED = "Shipped";
        public static String STATUS_AWAITINGCANCEL = "AWaitingCancel";
        public static String STATUS_CANCELED = "Canceled";
    }

    public static class DemandPayment {
        public static String ALIPAY = "alipay";
        public static String NETPAY = "netpay";
        public static String TENPAY = "tenpay";
        public static String FASTPAY = "fastpay";
        public static String WXAP = "wxap";
        public static String ADYEN = "adyen";
        public static String KRBANK = "krbank";
        public static String BALANCE = "balance";
        public static String DAOUPAY = "daoupay";
    }

    public static class Coupon {
        public static final String DEFAULT_CODE = "CNY";
        public static final String NO_MORE = "nomore";
    }

    public static String getPaymentString(String key) {
        KLog.i("key =" + key);
        if (TextUtils.isEmpty(key)) {
            return UIUtils.getString(R.string.please_select_payment_method);
        }

        sPaymentMap.put(DemandPayment.ALIPAY, UIUtils.getString(R.string.alipay));
        sPaymentMap.put(DemandPayment.NETPAY, UIUtils.getString(R.string.netpay));
        sPaymentMap.put(DemandPayment.TENPAY, UIUtils.getString(R.string.tenpay));
        sPaymentMap.put(DemandPayment.FASTPAY, UIUtils.getString(R.string.fastpay));

        sPaymentMap.put(DemandPayment.WXAP, UIUtils.getString(R.string.wxap));
        sPaymentMap.put(DemandPayment.ADYEN, UIUtils.getString(R.string.String_adyen_pay));
        sPaymentMap.put(DemandPayment.KRBANK, UIUtils.getString(R.string.krbank));
        sPaymentMap.put(DemandPayment.BALANCE, UIUtils.getString(R.string.pay_by_gift_card));
        sPaymentMap.put(DemandPayment.DAOUPAY, UIUtils.getString(R.string.dao_u_pay));
        String s = sPaymentMap.get(key);
        if (TextUtils.isEmpty(s)) {
            return key;
        } else {
            return s;
        }
    }

    public static String getStatusString(String key) {
        if (TextUtils.isEmpty(key)) {
            return "";
        }

        sStatusMap.put(DemandStatus.STATUS_PENDING, UIUtils.getString(R.string.pending));
        sStatusMap.put(DemandStatus.STATUS_ALLOCATING, UIUtils.getString(R.string.allocating));
        sStatusMap.put(DemandStatus.STATUS_GRABBING, UIUtils.getString(R.string.grabbing));

        sStatusMap.put(DemandStatus.STATUS_PROCESSING, UIUtils.getString(R.string.processing));
        sStatusMap.put(DemandStatus.STATUS_PREORDER, UIUtils.getString(R.string.preorder));
        sStatusMap.put(DemandStatus.STATUS_PACKING, UIUtils.getString(R.string.packing));

        sStatusMap.put(DemandStatus.STATUS_SHIPPED, UIUtils.getString(R.string.shipped));
        sStatusMap.put(DemandStatus.STATUS_AWAITINGCANCEL, UIUtils.getString(R.string.awaitingcancel));
        sStatusMap.put(DemandStatus.STATUS_CANCELED, UIUtils.getString(R.string.canceled));

        return sStatusMap.get(key);
    }

    public static int getTyptByCode(String key) {
        if (sPayTypeMap.size() == 0) {
            sPayTypeMap.put(GIFT_CARD_PAYMENT_ADYEN, PAYMENT_ADYEN);
            sPayTypeMap.put(GIFT_CARD_PAYMENT_KRBANK, PAYMENT_KRBANK);
            sPayTypeMap.put(GIFT_CARD_PAYMENT_ALIPAY, PAYMENT_ALIPAY);
            sPayTypeMap.put(GIFT_CARD_PAYMENT_WXPAY, PAYMENT_WECHAT);
            sPayTypeMap.put(GIFT_CARD_PAYMENT_BALANCE, PAYMENT_BANLANCE);
        }
        if (TextUtils.isEmpty(key) || sPayTypeMap.get(key) == null) {
            return -1;
        } else {
            return sPayTypeMap.get(key);
        }
    }

    public static Map<String, String> getPayInfoFromDirectUrl(String url) {
        String url1 = url.substring(url.indexOf("?") + 1);
        String[] urls = url1.split("&");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < urls.length; i++) {
            map.put(getKeyFromString(urls[i]), getValueFromString(urls[i]));
        }
        return map;
    }

    private static String getKeyFromString(String url) {
        if (url.contains("=")) {
            return url.substring(0, url.indexOf("="));
        } else {
            return url;
        }
    }

    private static String getValueFromString(String url) {
        if (url.contains("=")) {
            return url.substring(url.indexOf("=") + 1);
        } else {
            return url;
        }
    }

    public class PayGift {
        public static final String TOTAL = "total";
        public static final String SALESREQIREID = "salesRequireId";
        public static final String PAYMENTCODE = "paymentCode";
        public static final String UIN = "uin";
        public static final String APPTYPE = "appType";
        public static final String TYPE = "type";
    }

    //微博第三方登录分享配置
    public class Weibo {
        /**
         * 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY
         */
        public static final String APP_KEY = "632300441";
        public static final String REDIRECT_URL = "http://d.yiss.com/";
        public static final String APP_SECRET = "9ff75e976977691af0eb6f891a46d25b";
        public static final String SCOPE =
                "email,direct_messages_read,direct_messages_write,"
                        + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                        + "follow_app_official_microblog," + "invitation_write";
    }


    static {
        switch (urlIndex) {
            case 0:
//                服务器主机
                POST_URL = POST_URL_TEST_ZUO;
                ADYEN_PUBLIC_KEY = ADYEN_PUBLIC_KEY_TEST;
                CART_PAY_TOKEN = CART_PAY_TOKEN_TEST;
                CART_PAY_URL = CART_PAY_URL_TEST;
                CARD_PAY_URL = CARD_PAY_URL_TEST;
                URL_ADYEN_IS_SAVE_USER_INFO = URL_ADYEN_IS_SAVE_USER_INFO_TEST;
                CART_PAY_SAVED_URL = CART_PAY_SAVED_URL_TEST;
                CARD_PAY_SAVED_URL = CARD_PAY_SAVED_URL_TEST;
                URL_CHATRA_OFFICIAL = URL_CHATRA_Test;//客服

                break;
            case 1:
                POST_URL = POST_PRE_OFFICIAL;
                ADYEN_PUBLIC_KEY = ADYEN_PUBLIC_KEY_OFFICIAL;
                CART_PAY_TOKEN = CART_PAY_TOKEN_OFFICIAL;
                CART_PAY_URL = CART_PAY_URL_OFFICIAL;
                CARD_PAY_URL = CARD_PAY_URL_OFFICIAL;
                URL_ADYEN_IS_SAVE_USER_INFO = URL_ADYEN_IS_SAVE_USER_INFO_OFFICIAL;
                CART_PAY_SAVED_URL = CART_PAY_SAVED_URL_OFFICIAL;
                CARD_PAY_SAVED_URL = CARD_PAY_SAVED_URL_OFFICIAL;
                URL_CHATRA_OFFICIAL = URL_CHATRA_OFFICIAL_release;//客服
                break;
            case 2:
                POST_URL = POST_URL_OFFICIAL;
                ADYEN_PUBLIC_KEY = ADYEN_PUBLIC_KEY_OFFICIAL;
                CART_PAY_TOKEN = CART_PAY_TOKEN_OFFICIAL;
                CART_PAY_URL = CART_PAY_URL_OFFICIAL;
                CARD_PAY_URL = CARD_PAY_URL_OFFICIAL;
                URL_ADYEN_IS_SAVE_USER_INFO = URL_ADYEN_IS_SAVE_USER_INFO_OFFICIAL;
                CART_PAY_SAVED_URL = CART_PAY_SAVED_URL_OFFICIAL;
                CARD_PAY_SAVED_URL = CARD_PAY_SAVED_URL_OFFICIAL;
                URL_CHATRA_OFFICIAL = URL_CHATRA_OFFICIAL_release;//客服

                break;
        }
    }

    public static class DaoUPayString {
        //eg: http://122.226.100.91/app/getDaoupayVaccountUrl?orderId=123&orderType=1&amount=10000
        public static String S_URL_DAOUPAY_INFO_PRO = "http://wap.fu-express.com/app/getDaoupayVaccountUrl?";
        public static String S_URL_DAOUPAY_INFO_TEST = "http://122.226.100.91/app/getDaoupayVaccountUrl?";
        //eg "http://d.yiss.com/daoupay/successRedirect?orderId=" + orderId + "&orderType=" + orderType, "EUC-KR";
        //用户点击成功
        public static String S_SUCCESS_DAOUPAY = "http://d.yiss.com/daoupay/successRedirect?";
        //用户点击成功
        //eg "http://d.yiss.com/daoupay/closeRedirect?orderId=" + orderId + "&orderType=" + orderType, "EUC-KR";
        public static String S_FAIL_DAOUPAY = "http://d.yiss.com/daoupay/closeRedirect?";
        //用户点击成功
        //eg "http://d.yiss.com/daoupay/failRedirect?orderId=" + orderId + "&orderType=" + orderType, "EUC-KR";
        public static String S_CLOSE_DAOUPAY = "http://d.yiss.com/daoupay/closeRedirect?";

        //生成第一步的链接
        public static String getDaoUPayUrl(long orderId, long orderType, long amount) {
            if (urlIndex == 2 || urlIndex == 1) {
                return S_URL_DAOUPAY_INFO_PRO + "orderId=" + orderId + "&orderType=" + orderType + "&amount=" + amount;
            } else {
                return S_URL_DAOUPAY_INFO_TEST + "orderId=" + orderId + "&orderType=" + orderType + "&amount=" + amount;
            }

        }

    }
}
