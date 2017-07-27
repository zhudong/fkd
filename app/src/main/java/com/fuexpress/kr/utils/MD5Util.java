package com.fuexpress.kr.utils;

import com.yiss.ddm.packer.utils.HexUtil;

import java.security.MessageDigest;

/**
 * Created by alick on 2/29/16.
 */
public class MD5Util {
    public static String getMD5(String val) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();//加密
            return HexUtil.encodeHex(m, false);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
        }
        return null;
    }

}
