package com.fuexpress.kr.bean;

import java.util.List;

/**
 * Created by yuan on 2016-5-17.
 */
public class KuaiDiBaen {

    /**
     * message : ok
     * nu : 070625101451
     * ischeck : 1
     * com : shunfeng
     * status : 200
     * condition : F00
     * state : 3
     * data : [{"time":"2016-05-14 11:54:27","context":"已签收,感谢使用顺丰,期待再次为您服务","ftime":"2016-05-14 11:54:27"},{"time":"2016-05-14 10:17:27","context":"正在派送途中,请您准备签收(派件人:叶茂,电话:15168037470)","ftime":"2016-05-14 10:17:27"},{"time":"2016-05-14 09:59:16","context":"快件到达 【丽水莲都水阁营业点】","ftime":"2016-05-14 09:59:16"},{"time":"2016-05-14 09:35:40","context":"快件离开【丽水水阁集散中心】,正发往 【丽水庆元五都营业点】","ftime":"2016-05-14 09:35:40"},{"time":"2016-05-14 08:17:18","context":"快件到达 【丽水水阁集散中心】","ftime":"2016-05-14 08:17:18"},{"time":"2016-05-14 01:52:50","context":"快件离开【杭州总集散中心】,正发往 【丽水水阁集散中心】","ftime":"2016-05-14 01:52:50"},{"time":"2016-05-14 01:49:26","context":"快件到达 【杭州总集散中心】","ftime":"2016-05-14 01:49:26"},{"time":"2016-05-13 20:36:56","context":"快件离开【青岛总集散中心】,正发往下一站","ftime":"2016-05-13 20:36:56"},{"time":"2016-05-13 19:58:11","context":"快件到达 【青岛总集散中心】","ftime":"2016-05-13 19:58:11"},{"time":"2016-05-13 19:37:44","context":"快件离开【青岛流亭集散中心】,正发往 【青岛总集散中心】","ftime":"2016-05-13 19:37:44"},{"time":"2016-05-13 11:29:27","context":"快件到达 【青岛流亭集散中心】","ftime":"2016-05-13 11:29:27"},{"time":"2016-05-12 18:30:44","context":"快件离开【韩国首尔(仁川)口岸】,正发往 【青岛流亭集散中心】","ftime":"2016-05-12 18:30:44"},{"time":"2016-05-12 18:29:58","context":"快件到达 【韩国首尔(仁川)口岸】","ftime":"2016-05-12 18:29:58"},{"time":"2016-05-12 15:27:14","context":"快件离开【韩国首尔营运中心】,正发往 【韩国首尔(仁川)口岸】","ftime":"2016-05-12 15:27:14"},{"time":"2016-05-12 11:35:42","context":"顺丰速运 已收取快件","ftime":"2016-05-12 11:35:42"}]
     */

    public String message;
    public String nu;
    public String ischeck;
    public String com;
    public String status;
    public String condition;
    public String state;
    /**
     * time : 2016-05-14 11:54:27
     * context : 已签收,感谢使用顺丰,期待再次为您服务
     * ftime : 2016-05-14 11:54:27
     */

    public List<DataBean> data;

    public static class DataBean {
        public String time;
        public String context;
        public String ftime;
    }
}
