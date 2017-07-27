package com.fuexpress.kr.net;

import com.google.protobuf.GeneratedMessage;

/**
 * Created by alick on 2/29/16.
 */
public interface INetEngineListener<T extends GeneratedMessage> {
    void onSuccess(T response) ;
    void onFailure(int ret, String errMsg);
}
