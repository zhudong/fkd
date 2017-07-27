package com.fuexpress.kr.net;


import com.fuexpress.kr.base.SysApplication;
import com.fuexpress.kr.conf.Constants;
import com.fuexpress.kr.ui.uiutils.UIUtils;
import com.fuexpress.kr.utils.FileUtils;
import com.fuexpress.kr.utils.IOUtils;
import com.fuexpress.kr.utils.LogUtils;
import com.fuexpress.kr.utils.MD5Util;
import com.google.protobuf.GeneratedMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;



public class BaseProtocol<T extends GeneratedMessage.Builder, V extends GeneratedMessage> {
    private static BaseProtocol mInstance = new BaseProtocol();
    private String mKey;
    private FileInputStream mInputStream;
    private int mTimeLong;

    public static BaseProtocol getInstance() {
        return mInstance;
    }

    private GeneratedMessage mResponse;

    public void loadData(T t, INetEngineListener listener) {
        loadData(t, listener, true,60*5);
    }

    public void loadData(T t, INetEngineListener listener, boolean isFromCache,int timeLong) {
        mTimeLong = timeLong;
        mKey = MD5Util.getMD5(t.toString() + Constants.POST_URL);
        if (isFromCache) {
            SysApplication app = (SysApplication) UIUtils.getContext();
            Map<String, GeneratedMessage> protocolMap = app.getProtocolMap();
            mResponse = protocolMap.get(mKey);
            if (mResponse != null) {
                listener.onSuccess(mResponse);
                return;
            }
            loadDataFromLocal(t, mKey, listener,true);
        } else {
            loadDataFromNet(t, listener);
        }

    }


    private void loadDataFromLocal(T t, String key, final INetEngineListener listener,boolean isFromNet) {

        File cacheFile = getCacheFile(mKey);
        if (cacheFile.exists()) {
            try {
                if(mInputStream==null){
                    mInputStream = new FileInputStream(cacheFile);
                }
                int available = mInputStream.available() - 4;
                byte[] buf = new byte[available];
                mInputStream.read(buf);

                byte[] byteTime = new byte[4];
                mInputStream.read(byteTime, mInputStream.available() - 4, mInputStream.available());
                int time = bytesToInt(byteTime);
                LogUtils.d(time + "time");
                int currentTime = (int) (new Date().getTime() / 1000);
                if (time < currentTime&&isFromNet) {
                    loadDataFromNet(t, listener);
//                    cacheFile.delete();
                } else {
                    NetEngine.unpackFormLocal(t, buf, new INetEngineListener() {
                        @Override
                        public void onSuccess(GeneratedMessage response) {
                            SysApplication app = (SysApplication) UIUtils.getContext();
                            app.getProtocolMap().put(mKey, response);

                            listener.onSuccess(response);
                        }

                        @Override
                        public void onFailure(int ret, String errMsg) {
                            listener.onFailure(ret, errMsg);
                        }
                    });
                }

            } catch (Exception e) {
                cacheFile.delete();
                loadDataFromNet(t,listener);
                e.printStackTrace();
            } finally {
                IOUtils.close(mInputStream);
            }
        } else {
            if(isFromNet){
                loadDataFromNet(t, listener);
            }else{
                listener.onFailure(1000,"load data from net fail and no local cache");
            }
        }
    }

    private File getCacheFile(String key) {
        String dir = FileUtils.getDir("response");
        String name = key;
        return new File(dir, name);
    }


    private void loadDataFromNet(final T t, final INetEngineListener listener) {
        NetEngine.postRequest(t, new INetEngineListener() {
            @Override
            public void onSuccess(GeneratedMessage response) {
                listener.onSuccess(response);
                SysApplication app = (SysApplication) UIUtils.getContext();
                app.getProtocolMap().put(mKey, response);
                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(getCacheFile(mKey));
                    response.writeTo(outputStream);
                    int writeTime = (int) (new Date().getTime() / 1000);
                    int validTime = writeTime + mTimeLong;
                    outputStream.write(intToByte(validTime));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(outputStream);
                }
            }

            @Override
            public void onFailure(int ret, String errMsg) {
                if(ret==-11){
                    loadDataFromLocal(t,mKey,listener,false);
                }else{
                    listener.onFailure(ret, errMsg);
                }
            }
        });
    }

    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 24) & 0xFF000000);
        return addr;
    }

    public byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

}
