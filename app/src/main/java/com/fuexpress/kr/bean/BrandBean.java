package com.fuexpress.kr.bean;

/**
 * Created by andy on 2017/3/6.
 */
public class BrandBean {
    String name;
    int updateTime;

    interface TYPE {
        String NUM = "NULL";       // 空值
        String INT = "INTEGER";   // 带符号整数，根据存入的数值的大小占据1,2,3,4,6或者8个字节
        String REAL = "REAL";   // 浮点数，采用8byte（即双精度）的IEEE格式表示
        String TEXT = "TEXT";  // 字符串文本，采用数据库的编码（UTF-8，UTF-16BE 或者UTF-
        String BLOB = "BLOB";
    }

    public interface Table {
        String tb_name = "BRANDS";
        String name = "NAME";
        String time = "TIME";
    }

    public static String sql = "CREATE TABLE " + Table.tb_name + "(" +
            Table.name + " " + TYPE.TEXT +","+
            Table.time + " " + TYPE.INT +
            ")";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
}
