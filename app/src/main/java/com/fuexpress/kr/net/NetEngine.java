package com.fuexpress.kr.net;

import android.util.Log;

import com.fuexpress.kr.base.BusEvent;
import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.model.AccountManager;
import com.fuexpress.kr.utils.ClassUtil;
import com.fuexpress.kr.utils.CommonUtils;
import com.fuexpress.kr.utils.RandomUtil;
import com.fuexpress.kr.utils.RequestBodyUtil;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.yiss.ddm.packer.proto.SSPDU;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;
import fksproto.CsHead;
import fksproto.CsLogin;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import yssproto.CsPdu;

/**
 * Created by alick on 2/26/16.
 */
public class NetEngine {
    public static String sRandomKey = RandomUtil.randomString(16);
    public static String TAG = NetEngine.class.getSimpleName();
    //添加线程池
    public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private static boolean sIsHasRandomKey = false;

    public static <T extends GeneratedMessage.Builder> int postRequest(final T requestBuilder, final INetEngineListener listener, boolean isAutoCall) {
        //protoConfig;
        final ProtoConfig protoConfig = ProtoConfigManager.getInstance().getProtoConfig(requestBuilder);
        //if you has logged on, feed data will change, so we need another protoConfig
        //   final ProtoConfig protoConfig02 = ProtoConfigManager02.getInstance().getProtoConfig(requestBuilder);
        if (protoConfig == null) {
            return -1;
        } else {
            if (protoConfig.mEncryptMethod == CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_AES) {
                //如果
                if (AccountManager.getInstance().mSessionKey == null) {
                    return -2;
                }
            }
        }

        if (protoConfig.mNeedMoneyType) {
            if (AccountManager.isLogin) {
                ClassUtil.invoke(requestBuilder, "setLocalecode", AccountManager.getInstance().getLocaleCode());
                ClassUtil.invoke(requestBuilder, "setCurrencycode", AccountManager.getInstance().getCurrencyCode());
                ClassUtil.invokeInt(requestBuilder, "setCurrencyid", AccountManager.getInstance().getCurrencyId());
            } else {
                ClassUtil.invoke(requestBuilder, "setLocalecode", SysApplication.DEFAULT_LANGUAGE);
                ClassUtil.invoke(requestBuilder, "setCurrencycode", "USD");
                ClassUtil.invokeInt(requestBuilder, "setCurrencyid", 10);
            }
        }

        final CsPdu.CSPDUPBHead.Builder builder = CsPdu.CSPDUPBHead.newBuilder();
        CsPdu.CSPDUPBHeadBase.Builder baseBuilder = builder.getBaseBuilder();
        CsPdu.CSPDUPBHeadExt.Builder extBuilder = builder.getExtBuilder();

        baseBuilder.setMsgid(protoConfig.mMsgId.getNumber());
        baseBuilder.setCompressMethod(protoConfig.mCompressMethod.getNumber());
        baseBuilder.setEncryptMethod(protoConfig.mEncryptMethod.getNumber());

        extBuilder.setUin(AccountManager.getInstance().mUin);

        extBuilder.setTicket(AccountManager.getInstance().mTicket == null ? "" : AccountManager.getInstance().mTicket);

        CsHead.BaseRequest.Builder baseRequestBuilder = CsHead.BaseRequest.newBuilder();
        baseRequestBuilder.setLang(CsHead.LANG.LANG_ZH_CN_VALUE);
        //#define kUA @"ddm_213/iOS/%@/%@/%@/%@"    //ddm_200/iOS/设备型号/系统版本/软件版本号/渠道号
        String uaCode = "ddm_213/Android/" + mModel + "/" + mSystemVersion + "/" + mAppVersion + "/" + mChannelName;
        //baseRequestBuilder.setUa("ddm_213/Android/Simulater/5.1/2.0/default");
        baseRequestBuilder.setUa(uaCode);
        baseRequestBuilder.setUuid("123456");
//        baseRequestBuilder.setUuid(mUuid);
        baseRequestBuilder.setAutoCall(isAutoCall);
        ClassUtil.invoke(requestBuilder, "setHead", baseRequestBuilder);
        /*if (requestBuilder.getClass() == CsLogin.AccountRequest.Builder.class) {//当请求是与账户有关的操作时,这里我们进行判断是否设置了RandomKey: edit by Longer
            CsLogin.AccountRequest.Builder requestBuilder1 = (CsLogin.AccountRequest.Builder) requestBuilder;
            sIsHasRandomKey = requestBuilder1.hasRandomKey();//把其作为成员变量储存起来 edit by Longer
        } else {
            if (sIsHasRandomKey) {
                sIsHasRandomKey = false;
            }
        }*/
        cachedThreadPool.execute(new Runnable() {
                                     @Override
                                     public void run() {
                                         post(pack(requestBuilder.build(), builder.build()), listener, protoConfig);

                                     }
                                 }

        );
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                post(pack(requestBuilder.build(), builder.build()), listener, protoConfig);
            }
        }).start();*/
        //LogUtils.e("请求:" + requestBuilder.getClass().getName());
        return 0;
    }

    public static int postRequest(final GeneratedMessage.Builder requestBuilder, final INetEngineListener listener) {
        boolean defultAutoCall = false;
        return postRequest(requestBuilder, listener, defultAutoCall);
    }


    private static byte[] pack(Message request, CsPdu.CSPDUPBHead pbHead) {

        SSPDU sspdu = new SSPDU();
        if (AccountManager.getInstance().mSessionKey != null) {

            sspdu.mKey = AccountManager.getInstance().mSessionKey;
        } else {
            sspdu.mKey = sRandomKey;
        }
        sspdu.mPbHead = pbHead;
        sspdu.mBodyBytes = request.toByteArray();

        return sspdu.toByteArray();
    }

    private static void unpack(byte[] responseBytes, INetEngineListener listener, ProtoConfig protoConfig) {
        SSPDU sspdu = new SSPDU();
        /*if (AccountManager.getInstance().mSessionKey != null && AccountManager.getInstance().mSessionKey.length() > 0 && !sIsHasRandomKey) {
            sspdu.mKey = AccountManager.getInstance().mSessionKey;
        } else {
            sspdu.mKey = sRandomKey;
        }*/
        if (AccountManager.getInstance().mSessionKey != null && AccountManager.getInstance().mSessionKey.length() > 0) {
            sspdu.mKey = AccountManager.getInstance().mSessionKey;
        } else {
            sspdu.mKey = sRandomKey;
        }
        sspdu.fromByteArray(responseBytes);

        CsPdu.CSPDUPBHead pbHead = sspdu.mPbHead;

        if (pbHead.getBase().getResult() != 0) {
            //// TODO: 2/29/16 proto error!
            Log.e(TAG, "server error,result = " + pbHead.getBase().getResult());
            Log.e(TAG, "server error,errMsg = " + CommonUtils.getErrMsg(pbHead.getBase().getResult()));
            listener.onFailure(pbHead.getBase().getResult(), CommonUtils.getErrMsg(pbHead.getBase().getResult()));
            if (pbHead.getBase().getResult() == -6) {
                //协议票据无效
                EventBus.getDefault().post(new BusEvent(BusEvent.LOGOUT, null));
                if (AccountManager.isLogin)
                    AccountManager.getInstance().logout(null);
            }
            return;
        }

        try {
            GeneratedMessage response = (GeneratedMessage) ClassUtil.invoke(protoConfig.mResponseClazz, "parseFrom", sspdu.mBodyBytes);
            CsHead.BaseResponse baseResponse = (CsHead.BaseResponse) ClassUtil.invoke(response, "getHead");
            //Log.i("PB", response.toString());
            if (baseResponse.getRet() == 1) {
                if (listener != null) {
                    listener.onSuccess(response);
                }
            } else {
                if (listener != null) {
                    Log.i("PB", response.getClass().getSimpleName() + ":" + CommonUtils.getErrMsg(baseResponse.getRet()));
                    listener.onFailure(baseResponse.getRet(), baseResponse.getErrmsg());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void post(byte[] requestBytes, INetEngineListener<CsLogin.LoginResponse> listener, ProtoConfig protoConfig) {
        OkHttpClient client = new OkHttpClient();
        final MediaType PROTOBUF = MediaType.parse("application/x-protobuf");

        InputStream inputStream = new ByteArrayInputStream(requestBytes);
//        KLog.i("netEngine Url = "+Constants.POST_URL);
        RequestBody requestBody = RequestBodyUtil.create(PROTOBUF, inputStream);
        Request request = new Request.Builder()
                .url(Constants.POST_URL)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                // TODO: 3/1/16  http status error
                Log.e(TAG, "http status=" + response.code());
            } else {
                byte[] responseBytes = response.body().bytes();
                unpack(responseBytes, listener, protoConfig);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (listener != null) {
                listener.onFailure(404, "time out");
            }
        }
    }


    public static int unpackFormLocal(final GeneratedMessage.Builder requestBuilder, byte[] responseBytes, final INetEngineListener listener) {
        final ProtoConfig protoConfig = ProtoConfigManager.getInstance().getProtoConfig(requestBuilder);
        if (protoConfig == null) {
            return -1;
        } else {
            if (protoConfig.mEncryptMethod == CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_AES) {
                //如果
                if (AccountManager.getInstance().mSessionKey == null) {
                    return -2;
                }
            }
        }


        try {
            GeneratedMessage response = (GeneratedMessage) ClassUtil.invoke(protoConfig.mResponseClazz, "parseFrom", responseBytes);
            CsHead.BaseResponse baseResponse = (CsHead.BaseResponse) ClassUtil.invoke(response, "getHead");

            //    Log.i("PB", response.toString());
            if (baseResponse.getRet() == 0) {
                if (listener != null) {
                    listener.onSuccess(response);
                }
            } else {
                if (listener != null) {
                    listener.onFailure(baseResponse.getRet(), baseResponse.getErrmsg());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static void setUuid(String uuid) {
        mUuid = uuid;
    }

    private static String mUuid = "12345";
    private static String mModel;
    private static String mSystemVersion;
    private static String mAppVersion;
    private static String mChannelName;

    public static void setUaValue(String model, String version, String appVersion, String channelName) {
        mModel = model;
        mSystemVersion = version;
        mAppVersion = appVersion;
        //        mChannelName = channelName;
    }

    public static void setmChannelName(String channelName) {
        mChannelName = channelName;
    }

}
