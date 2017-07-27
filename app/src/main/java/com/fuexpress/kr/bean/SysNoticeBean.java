package com.fuexpress.kr.bean;

import java.io.Serializable;

/**
 * Created by k550 on 5/4/2016.
 */
public class SysNoticeBean implements Serializable {
    public SysNoticeBean(){}
    public long coreMsgId;
    public String title;
    public String sendTimeStr;
    public int isRead;
}
