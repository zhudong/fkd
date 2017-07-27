package com.fuexpress.kr.net;

import java.util.ArrayList;
import java.util.List;

import fksproto.CsAddress;
import fksproto.CsAlbum;
import fksproto.CsCard;
import fksproto.CsCart;
import fksproto.CsFeed;
import fksproto.CsInfo;
import fksproto.CsItem;
import fksproto.CsLogin;
import fksproto.CsMerchant;
import fksproto.CsMsgid;
import fksproto.CsMy;
import fksproto.CsNotice;
import fksproto.CsOrder;
import fksproto.CsParcel;
import fksproto.CsShipping;
import fksproto.CsUser;
import yssproto.CsPdu;


/**
 * Created by alick on 3/1/16.
 */
public class ProtoConfigManager {
    private static ProtoConfigManager ourInstance = new ProtoConfigManager();
    private List<ProtoConfig> mProtoConfigList;

    public static ProtoConfigManager getInstance() {
        return ourInstance;
    }

    public ProtoConfig getProtoConfig(Object object) {
        for (ProtoConfig protoConfig : mProtoConfigList) {
            if (object.getClass().isAssignableFrom(protoConfig.mRequestBuilderClazz)) {
                return protoConfig;
            }
        }
        return null;
    }

    private ProtoConfigManager() {
        mProtoConfigList = new ArrayList<>();
        // TODO: 3/1/16 put all Protocal HERE!
        //登录
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_LOGIN_REQUEST, CsLogin.LoginRequest.Builder.class, CsLogin.LoginResponse.class,
                CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_RSA));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_ACCOUNT_REQUEST, CsLogin.AccountRequest.Builder.class, CsLogin.AccountResponse.class,
                CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_RSA));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GIFT_CARD_BALANCE_LIST_REQUEST, CsCard.GiftCardBalanceListRequest.Builder.class, CsCard.GiftCardBalanceListReponse.class, true));
//        mProtoConfigList.add(new ProtoConfig(
//                CsMsgid.FksCSProtoMsgId.MSGID_GIFT_CARD_BALANCE_LIST_AJAX_REQUEST, CsCard.GiftCardBalanceListAjaxRequest.Builder.class, CsCard.GiftCardBalanceListAjaxReponse.class, true));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_GIFT_CARD_ORDER_LIST_REQUEST, CsCard.GetGiftCardOrderListRequest.Builder.class, CsCard.GetGiftCardOrderListReponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SET_USER_INFO_FIELD_REQUEST, CsUser.SetUserInfoFieldRequest.Builder.class, CsUser.SetUserInfoFieldResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_CUSTOMER_ADDRESS_LIST_REQUEST, CsAddress.GetCustomerAddressListRequest.Builder.class, CsAddress.GetCustomerAddressListResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SET_USER_INFO_FIELD_REQUEST, CsUser.SetUserInfoFieldRequest.Builder.class, CsUser.SetUserInfoFieldResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_DEL_CUSTOMER_ADDRESS_REQUEST, CsAddress.DelCustomerAddressRequest.Builder.class, CsAddress.DelCustomerAddressResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_EDIT_CUSTOMER_ADDRESS_REQUEST, CsAddress.EditCustomerAddressRequest.Builder.class, CsAddress.EditCustomerAddressResponse.class));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_ADD_CUSTOMER_ADDRESS_REQUEST, CsAddress.AddCustomerAddressRequest.Builder.class, CsAddress.AddCustomerAddressResponse.class));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_SIMPLE_USER_INFO_REQUEST, CsUser.GetSimpleUserInfoRequest.Builder.class, CsUser.GetSimpleUserInfoResponse.class,
                CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_THIRD_LOGIN_REQUEST, CsLogin.ThirdLoginRequest.Builder.class, CsLogin.ThirdLoginResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_RSA));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_USER_INFO_FIELD_REQUEST, CsUser.GetUserInfoFieldRequest.Builder.class, CsUser.GetUserInfoFieldResponse.class));


//        需求列表
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MY_REQUIRE_LIST_REQUEST, CsUser.GetMyRequireListRequest.Builder.class, CsUser.GetMyRequireListResponse.class));

//        获取材质
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MATERIALS_LIST_REQUEST, CsUser.GetMaterialsListRequest.Builder.class, CsUser.GetMaterialsListResponse.class));

//        包裹
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_PARCEL_LIST_REQUEST, CsParcel.SendParcelListRequest.Builder.class, CsParcel.SendParcleListResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SEND_INIT_REQUEST, CsParcel.SendInitRequest.Builder.class, CsParcel.SendInitReponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_SELECT_ADDRESS_AJAX_REQUEST, CsParcel.GetSelectAddressAjaxRequest.Builder.class, CsParcel.GetSelectAddressAjaxReponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_SHIPPING_INFO_REQUEST, CsParcel.GetShippingInfoRequest.Builder.class, CsParcel.GetShippingInfoReponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SEND_SUBMIT_REQUEST, CsParcel.SendSubmitRequest.Builder.class, CsParcel.SendSubmitReponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_DO_DIRECT_GIFT_CARD_REQUSET, CsParcel.DoDirectGiftCardRequest.Builder.class, CsParcel.DoDirectGiftCardResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_PAY_GIFT_CARD_ORDER_REQUEST, CsParcel.PayGiftCardOrderRequest.Builder.class, CsParcel.PayGiftCardOrderResponse.class));
        /*提交多个包裹，多个单品的*/
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SEND_FOR_ITEM_REQUEST, CsParcel.SendForItemRequest.Builder.class, CsParcel.SendForItemResponse.class));
        /*包裹单品操作*/
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_ADD_ITEM_REQUEST, CsParcel.AddItemRequest.Builder.class, CsParcel.AddItemResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SEND_EDITITEM_REQUEST, CsParcel.EditItemRequest.Builder.class, CsParcel.EditItemResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_DEL_ITEM_REQUEST, CsParcel.DelItemRequest.Builder.class, CsParcel.DelItemResponse.class));
//        包裹详情
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GEG_PARCEL_DETAIL_REQREST, CsParcel.GetParcelDetailRequest.Builder.class, CsParcel.GetParcelDetailResponse.class));
      /*  mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSG_SELECT_SHIPPING_METHOD_REQUEST, CsParcel.GetSelectAddressAjaxRequest.Builder.class, CsParcel.GetSelectAddressAjaxReponse.class));*/
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_CHECK_TO_PAY_REQUEST, CsMy.CheckToPayRequest.Builder.class, CsMy.CheckToPayResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_PAY_DO_DIRECT_GIFT_CARD_APP_SINGLE_REQUEST, CsParcel.PayDoDirectGiftCardAppSingleRequest.Builder.class, CsParcel.PayDoDirectGiftCardAppSingleResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SELECT_SHIPPING_METHOD_REQUEST, CsParcel.SelectShippingMethodRequest.Builder.class, CsParcel.SelectShippingMethodReponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_WAREHOUSELIST_REQUEST, CsUser.GetWarehouseListRequest.Builder.class, CsUser.GetWarehouseListResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_PARCEL_NOTICE_SHIPPING_FEE_REQUEST, CsParcel.ParcelNoticeShippingFeeRequest.Builder.class, CsParcel.ParcelNoticeShippingFeeResponse.class));
        /*订单包裹提交发货*/
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SUBMIT_PARCEL_NOTICE_REQUEST, CsParcel.SubmitParcelNoticeRequest.Builder.class, CsParcel.SubmitParcelNoticeResponse.class));

//        拆分包裹初始化
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_INIT_REPARCEL_REQUEST, CsParcel.InitReparcelRequest.Builder.class, CsParcel.InitReparcelResponse.class));
//        拆分包裹
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SAVE_REPARCEL_REQUEST, CsParcel.SaveReparcelRequest.Builder.class, CsParcel.SaveReparcelResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_REPARCEL_DO_DIRECT_GIFT_CARD_REQUSET, CsParcel.ReparcelDoDirectGiftCardRequest.Builder.class, CsParcel.ReparcelDoDirectGiftCardResponse.class));

//保险申报
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_INIT_INSURANCE_DECLARATION_REQUEST, CsParcel.InitInsuranceDeclarationRequest.Builder.class, CsParcel.InitInsuranceDeclarationResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_SAVE_INSURANCE_DECLARATION_REQUEST, CsParcel.SaveInsuranceDeclarationRequest.Builder.class, CsParcel.SaveInsuranceDeclarationResponse.class));


//       获取余额
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_ACCOUNT_BALANCE_REQUEST, CsMy.GetAccountBalanceRequest.Builder.class, CsMy.GetAccountBalanceResponse.class));
//        首页跑马灯
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_INDEX_ADIMAGE_REQUEST, CsFeed.IndexAdImageRequest.Builder.class, CsFeed.IndexAdImageResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_RECHARGE_ORDER_DETAIL_REQUEST, CsCard.GetRechargeOrderDetailRequest.Builder.class, CsCard.GetRechargeOrderDetialResponse.class));

        //优惠券
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_USEABLE_COUPONLIST_REQUEST, CsUser.UseableCouponListRequest.Builder.class, CsUser.UseableCouponListResponse.class
                /*,CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE*/));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_MYSHIPPING_COUPONLIST_REQUEST, CsUser.MyShippingCouponListRequest.Builder.class, CsUser.MyShippingCouponListResponse.class
                /*,CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE*/));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_EXCHANGE_SHIPPING_COUPON_REQUEST, CsUser.ExchangeShippingCouponRequest.Builder.class, CsUser.ExchangeShippingCouponResponse.class
                /*,CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE*/));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CANCEL_GIFT_CARD_ORDER_REQUEST, CsCard.CancelGiftCardOrderRequest.Builder.class, CsCard.CancelGiftCardOrderResponse.class));
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_SYS_NOTICE_MSG_REQUEST, CsNotice.GetSysNoticeMsgRequest.Builder.class, CsNotice.GetSysNoticeMsgResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_PAY_GIFT_CARD_ORDER_REQUEST, CsParcel.PayGiftCardOrderRequest.Builder.class, CsParcel.PayGiftCardOrderResponse.class));


        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_CURRENCY_LIST_REQUEST, CsUser.GetCurrencyListRequest.Builder.class, CsUser.GetCurrencyListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CHANGE_LOCALE_REQUEST, CsUser.ChangeLocaleRequest.Builder.class, CsUser.ChangeLocaleResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CHANGE_CURRENCY_REQUEST, CsUser.ChangeCurrencyRequest.Builder.class, CsUser.ChangeCurrencyResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_FKDPAYMENT_LIST_REQUEST, CsUser.GetFKDPaymentListRequest.Builder.class, CsUser.GetFKDPaymentListResponse.class));
        //获取友利银行信息
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_KRBANK_INFO_REQUEST, CsUser.GetKrBankInfoRequest.Builder.class, CsUser.GetKrBankInfoResponse.class));
        //获取红点消息
        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MY_RED_POINT_REQUEST, CsUser.GetMyRedPointRequest.Builder.class, CsUser.GetMyRedPointResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_HELP_MY_GET_INIT_REQUEST, CsParcel.HelpMyGetInitRequest.Builder.class, CsParcel.HelpMyGetInitResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_SAVE_HELP_MY_GET_REQUEST, CsParcel.SaveHelpMyGetRequest.Builder.class, CsParcel.SaveHelpMyGetResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_HELP_MY_RECEIVE_INIT_REQUEST, CsParcel.HelpMyReceiveInitRequest.Builder.class, CsParcel.HelpMyReceiveInitResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_SAVE_HELP_RECEIVE_REQUEST, CsParcel.SaveHelpReceiveRequest.Builder.class, CsParcel.SaveHelpReceiveResponse.class));

        //帮我补货详情
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_HELP_MY_BUYINFO_REQUEST, CsUser.helpMyBuyInfoRequest.Builder.class, CsUser.helpMyBuyInfoResponse.class));
        //帮我取货详情
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_HELP_MY_GETINFO_REQUEST, CsUser.HelpMyGetInfoRequest.Builder.class, CsUser.HelpMyGetInfoResponse.class));
        //帮我收货详情
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_HELP_MY_RECEIVEINFO_REQUEST, CsUser.helpMyReceiveInfoRequest.Builder.class, CsUser.helpMyReceiveInfoResponse.class));

        //帮我补货/买货初始化
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_HELP_MY_BUY_INIT_REQUEST, CsParcel.HelpMyBuyInitRequest.Builder.class, CsParcel.HelpMyBuyInitResponse.class));
        //帮我补货/买货提交
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_SAVE_HELP_MY_BUY_REQUEST, CsParcel.SaveHelpMyBuyRequest.Builder.class, CsParcel.SaveHelpMyBuyResponse.class));
        //获取订单数量:
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_MYREQUIRE_PARCELNUM_REQUEST, CsUser.MyRequireParcelNumRequest.Builder.class, CsUser.MyRequireParcelNumResponse.class));
        //请求检查支付
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CHECK_TO_PAY_IN_APPREQUIRE_REQUEST, CsCard.GetCheckToPayInApprequireRequest.Builder.class, CsCard.GetCheckToPayInApprequireResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_PAY_DO_DIRECT_GIFT_CARD_APP_SINGLE_REQUEST, CsParcel.PayDoDirectGiftCardAppSingleRequest.Builder.class, CsParcel.PayDoDirectGiftCardAppSingleResponse.class));
        //取消申请
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CANCEL_REQUIRE_REQUEST, CsUser.CancelRequireRequest.Builder.class, CsUser.CancelRequireResponse.class));
        //请求分类接口:
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_CATEGORY_LIST_REQUEST, CsUser.GetCategoryListRequest.Builder.class, CsUser.GetCategoryListResponse.class));
        //绑定充值卡:
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_BIND_GIFT_CARD_REQUEST, CsCard.BindGiftCardRequest.Builder.class, CsCard.BindGiftCardResponse.class));
        //请求充值卡来源:
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_GIFT_CARD_STORESITE_REQUEST, CsCard.GetGiftCardStoresiteRequest.Builder.class, CsCard.GetGiftCardStoresiteResponse.class));

        //切换语种
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CHANGE_LOCALE_REQUEST, CsUser.ChangeLocaleRequest.Builder.class, CsUser.ChangeLocaleResponse.class));
        //更新地址数据
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_UPDATE_COUNTRY_AND_REGION_REQUEST, CsCard.GetUpdateCountryAndRegionRequest.Builder.class, CsCard.GetUpdateCountryAndRegionResponse.class));

        //获取积分详情
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_CREDITS_DETAIL_REQUEST, CsUser.GetCreditsDetailRequest.Builder.class, CsUser.GetCreditsDetailResponse.class));
        //获取邀请的好友列表
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_INVITEES_LIST_REQUEST, CsUser.GetInviteesListRequest.Builder.class, CsUser.GetInviteesListResponse.class));
        //获取分享的连接
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_INVITE_CONFIG_REQUEST, CsUser.GetInviteConfigRequest.Builder.class, CsUser.GetInviteConfigResponse.class));
        //切换城市
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_WAREHOUSE_REGION_REQUEST, CsAddress.GetWarehouseRegionRequest.Builder.class, CsAddress.GetWarehouseRegionResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_SAVE_WAREHOUSE_REGION_REQUEST, CsAddress.SaveWarehouseRegionRequest.Builder.class, CsAddress.SaveWarehouseRegionResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ACCOUNTINFO_REQUEST, CsUser.GetAccountInfoRequest.Builder.class, CsUser.GetAccountInfoResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_VERSION_INFO_REQUEST, CsInfo.GetVersionInfoRequest.Builder.class, CsInfo.GetVersionInfoResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CREDIT_SWITHDRAW_REQUEST, CsMy.CreditsWithdrawRequest.Builder.class, CsMy.CreditsWithdrawResponse.class));

        //地址的联动系列:
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_COUNTRY_LIST_REQUEST, CsAddress.GetCountryListRequest.Builder.class, CsAddress.GetCountryListResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_REGION_LIST_REQUEST, CsAddress.GetRegionListRequest.Builder.class, CsAddress.GetRegionListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_REGION_LIST_BY_REGION_REQUEST, CsAddress.GetRegionListByRegionRequest.Builder.class, CsAddress.GetRegionListByRegionResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CREDITS_INVITE_INFO_REQUEST, CsUser.CreditsInviteInfoRequest.Builder.class, CsUser.CreditsInviteInfoResponse.class));

        //购买充值卡
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_INIT_GIFT_CARD_REQUEST, CsCard.InitGiftCardRequest.Builder.class, CsCard.InitGiftCardReponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_ADD_TO_GIFT_CARD_CART_REQUEST, CsCard.AddToGiftCardCartRequest.Builder.class, CsCard.AddToGiftCardCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_CART_INFO_NUM_REQUEST, CsCard.GetCartInfoNumRequest.Builder.class, CsCard.GetCartInfoNumResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_GIFT_CARD_ALONG_CART_REQUEST, CsCard.GetGiftCardAlongCartRequest.Builder.class, CsCard.GetGiftCardAlongCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_DELETE_GIFT_CARD_CART_REQUEST, CsCard.DeleteGiftCardCartNumberRequest.Builder.class, CsCard.DeleteGiftCardCartNumberResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_MODIFY_GIFT_CARD_CART_NUMBER_REQUEST, CsCard.ModifyGiftCardCartNumberRequest.Builder.class, CsCard.ModifyGiftCardCartNumberResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_GIFT_CARD_ALONG_CART_PLAY_REQUEST, CsCard.GiftCardAlongCartPlayRequest.Builder.class, CsCard.GiftCardAlongCartPlayResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_PLACE_GIFT_CARD_ORDER_REQUEST, CsCard.PlaceGiftCardOrderRequest.Builder.class, CsCard.PlacetGiftCardOrderResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_REDEEM_GIFT_CARD_REQUEST, CsCard.RedeemGiftCardRequest.Builder.class, CsCard.RedeemGiftCardReponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_GIFT_CARD_ORDER_DETAIL_REQUEST, CsCard.GetGiftCardOrderDetailRequest.Builder.class, CsCard.GetGiftCardOrderDetialResponse.class));


        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_COUNTRY_WAREHOUSE_LIST_REQUEST, CsUser.GetCountryWarehouseListRequest.Builder.class, CsUser.GetCountryWarehouseListResponse.class));


        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_MEMBER_GROUP_NAME_REQUEST, CsUser.GetMemberGroupNameRequest.Builder.class, CsUser.GetMemberGroupNameResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_MERCHANT_ITEM_LIST_REQUEST, CsMerchant.GetMerchantItemListRequest.Builder.class, CsMerchant.GetMerchantItemListResponse.class, true));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_HOT_MERCHANT_LIST2_REQUEST, CsMerchant.GetHotMerchantList2Request.Builder.class, CsMerchant.GetHotMerchantList2Response.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_FOLLOW_MERCHANT_LIST_REQUEST, CsMerchant.GetFollowMerchantListRequest.Builder.class, CsMerchant.GetFollowMerchantListResponse.class));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MERCHANT_ITEM_LIST_REQUEST, CsMerchant.GetMerchantItemListRequest.Builder.class, CsMerchant.GetMerchantItemListResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE, true));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MERCHANT_FOLLOWER_LIST_REQUEST, CsMerchant.GetMerchantFollowerListRequest.Builder.class, CsMerchant.GetMerchantFollowerListResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_LIKE_ITEM_REQUEST, CsItem.LikeItemRequest.Builder.class, CsItem.LikeItemResponse.class));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_FOLLOW_MERCHANT_REQUEST, CsMerchant.FollowMerchantRequest.Builder.class, CsMerchant.FollowMerchantResponse.class));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_FOLLOWERS_REQUEST, CsMerchant.FollowersRequest.Builder.class, CsMerchant.FollowersReponse.class,
                CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MY_ITEM_LIST_REQUEST, CsItem.GetMyItemListRequest.Builder.class, CsItem.GetMyItemListResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE, true));

        mProtoConfigList.add(new ProtoConfig(
                CsMsgid.FksCSProtoMsgId.MSGID_GET_MERCHANT_DETAIL_REQUEST, CsMerchant.GetMerchantDetailRequest.Builder.class, CsMerchant.GetMerchantDetailResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ITEM_IMAGELIST_REQUEST, CsItem.GetItemImageListRequest.Builder.class, CsItem.GetItemImageListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_SALES_CART_LIST_REQUEST, CsCart.GetSalesCartListRequest.Builder.class, CsCart.GetSalesCartListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_SELECTED_SALES_CART_REQUEST, CsCart.SelectedSalesCartRequest.Builder.class, CsCart.SelectedSalesCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_REMOVE_SALES_CART_REQUEST, CsCart.RemoveSalesCartRequest.Builder.class, CsCart.RemoveSalesCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_REMOVE_BATCH_SALES_CART_REQUEST, CsCart.RemoveBatchSalesCartRequest.Builder.class, CsCart.RemoveBatchSalesCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_MODIFY_SALES_CART_REQUEST, CsCart.ModifySalesCartRequest.Builder.class, CsCart.ModifySalesCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_SUBMIT_SALES_ORDER_REQUEST, CsOrder.SubmitSalesOrderRequest.Builder.class, CsOrder.SubmitSalesOrderResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_APPEND_SALES_CART_REQUEST, CsCart.AppendSalesCartRequest.Builder.class, CsCart.AppendSalesCartResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_SHIPPING_LIST_REQUEST, CsShipping.GetShippingListRequest.Builder.class, CsShipping.GetShippingListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_CUSTOMER_ADDRESS_LIST_EX_REQUEST, CsAddress.GetCustomerAddressListExRequest.Builder.class, CsAddress.GetCustomerAddressListExResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_APPLY_FOR_SALES_ORDER_PAY_REQUEST, CsOrder.ApplyForSalesOrderPayRequest.Builder.class, CsOrder.ApplyForSalesOrderPayResponse.class));

        /*单品详情*/
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ITEM_BASIC_DETAIL_REQUEST, CsItem.GetItemBasicDetailRequest.Builder.class, CsItem.GetItemBasicDetailResponse.class, CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE, CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_NONE));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ITEM_LIKER_LIST_REQUEST, CsItem.GetItemLikerListRequest.Builder.class, CsItem.GetItemLikerListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_MY_ITEM_LIST_REQUEST, CsItem.GetMyItemListRequest.Builder.class, CsItem.GetMyItemListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_POST_COMMENT_REQUEST, CsItem.PostCommentRequest.Builder.class, CsItem.PostCommentResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_COMMENT_LIST_REQUEST, CsItem.GetCommentListRequest.Builder.class, CsItem.GetCommentListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ITEM_PURCHASE_DETAIL_REQUEST, CsItem.GetItemPurchaseDetailRequest.Builder.class, CsItem.GetItemPurchaseDetailResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ITEM_LIST_BY_PLACE_REQUEST, CsItem.GetItemListByPlaceRequest.Builder.class, CsItem.GetItemListByPlaceResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ITEM_LIST_BY_WEBSITE_REQUEST, CsItem.GetItemListByWebsiteRequest.Builder.class, CsItem.GetItemListByWebsiteResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_MATCH_SHIPPING_INFO_REQUEST, CsItem.GetMatchShippingInfoRequest.Builder.class, CsItem.GetMatchShippingInfoResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_MATCH_SHIPPING_LIST_REQUEST, CsItem.GetMatchShippingListRequest.Builder.class, CsItem.GetMatchShippingListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_REVIEW_LIST_REQUEST, CsItem.GetReviewListRequest.Builder.class, CsItem.GetReviewListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_DO_RECOMMEND_REVIEW_REQUEST, CsItem.DoReCommendReviewRequest.Builder.class, CsItem.DoReCommendReviewResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_SALES_ORDER_DETAIL_REQUEST, CsOrder.GetSalesOrderDetailRequest.Builder.class, CsOrder.GetSalesOrderDetailResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CANCEL_SALES_ORDER_REQUEST, CsOrder.CancelSalesOrderRequest.Builder.class, CsOrder.CancelSalesOrderResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_SALES_ORDER_LIST_REQUEST, CsOrder.GetSalesOrderListRequest.Builder.class, CsOrder.GetSalesOrderListResponse.class));
        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_APPLY_FOR_SALES_ORDER_PAY_REQUEST, CsOrder.ApplyForSalesOrderPayRequest.Builder.class, CsOrder.ApplyForSalesOrderPayResponse.class));


        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_ADD_IMAGE_REVIEW_REQUEST, CsItem.AddImageReviewRequest.Builder.class, CsItem.AddImageReviewResponse.class));


        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_ALBUM_LIST_REQUEST, CsAlbum.GetAlbumListRequest.Builder.class, CsAlbum.GetAlbumListResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_CREATE_ALBUM_REQUEST, CsAlbum.CreateAlbumRequest.Builder.class, CsAlbum.CreateAlbumResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_ADD_ALBUM_ELEMENT_REQUEST, CsAlbum.AddAlbumElementRequest.Builder.class, CsAlbum.AddAlbumElementResponse.class));

        mProtoConfigList.add(new ProtoConfig(CsMsgid.FksCSProtoMsgId.MSGID_GET_SALES_ORDER_DETAIL_REQUEST, CsOrder.GetSalesOrderListRequest.Builder.class, CsOrder.GetSalesOrderListResponse.class));


    }
}
