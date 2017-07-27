package com.fuexpress.kr.net;


import fksproto.CsMsgid;
import yssproto.CsPdu;

/**
 * Created by chenyuelong on 3/1/16.
 */
public class ProtoConfig {
    public CsMsgid.FksCSProtoMsgId mMsgId;
    public Class mRequestBuilderClazz;
    public Class mResponseClazz;
    public CsPdu.CSPDUCompressMethod mCompressMethod;
    public CsPdu.CSPDUEncryptMethod mEncryptMethod;
    public boolean mNeedMoneyType;

    public ProtoConfig(CsMsgid.FksCSProtoMsgId msgId, Class requestBuilderClazz, Class responseClazz) {
        mMsgId = msgId;
        mRequestBuilderClazz = requestBuilderClazz;
        mResponseClazz = responseClazz;
        mCompressMethod = CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE;
        mEncryptMethod = CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_AES;
    }

    public ProtoConfig(CsMsgid.FksCSProtoMsgId msgId, Class requestBuilderClazz, Class responseClazz, boolean needMoneyType) {
        mMsgId = msgId;
        mRequestBuilderClazz = requestBuilderClazz;
        mResponseClazz = responseClazz;
        mCompressMethod = CsPdu.CSPDUCompressMethod.COMPRESS_METHOD_NONE;
        mEncryptMethod = CsPdu.CSPDUEncryptMethod.ENCRYPT_METHOD_AES;
        mNeedMoneyType = needMoneyType;
    }

    public ProtoConfig(CsMsgid.FksCSProtoMsgId msgId, Class requestBuilderClazz, Class responseClazz, CsPdu.CSPDUCompressMethod compressMethod, CsPdu.CSPDUEncryptMethod encryptMethod, boolean needMoneyType) {
        mRequestBuilderClazz = requestBuilderClazz;
        mMsgId = msgId;
        mResponseClazz = responseClazz;
        mCompressMethod = compressMethod;
        mEncryptMethod = encryptMethod;
        mNeedMoneyType = needMoneyType;
    }

    public ProtoConfig(CsMsgid.FksCSProtoMsgId msgId, Class requestBuilderClazz, Class responseClazz, CsPdu.CSPDUCompressMethod compressMethod, CsPdu.CSPDUEncryptMethod encryptMethod) {
        mRequestBuilderClazz = requestBuilderClazz;
        mMsgId = msgId;
        mResponseClazz = responseClazz;
        mCompressMethod = compressMethod;
        mEncryptMethod = encryptMethod;
    }
}
