package com.fuexpress.kr.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Longer on 2016/10/26.
 */
public class IOUtils {

    /** 关闭流 */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        return true;
    }
}
