package com.fuexpress.kr.bean;

import java.io.Serializable;

/**
 * Created by andy on 2017/1/17.
 */
public class MaterialsBean implements Serializable {
    int id;
    String materialsName;
    int time;

    interface TYPE {
        String NUM = "NULL";       // 空值
        String INT = "INTEGER";   // 带符号整数，根据存入的数值的大小占据1,2,3,4,6或者8个字节
        String REAL = "REAL";   // 浮点数，采用8byte（即双精度）的IEEE格式表示
        String TEXT = "TEXT";  // 字符串文本，采用数据库的编码（UTF-8，UTF-16BE 或者UTF-
        String BLOB = "BLOB";
    }

    public  interface Table {
        String primaryKey = "PRIMARYKEY";
        String name = "MATERIALS_NAME";
        String id = "MATERIALS_ID";
        String materialsName = "MATERIALS_NAME";
        String time = "TIME";
    }

   public static String sql = "CREATE TABLE " + Table.name + "(" +
            Table.primaryKey + " INTEGER primary key autoincrement," +
            Table.id + " " + TYPE.INT + "," +
            Table.materialsName + " " + TYPE.TEXT +"," +
            Table.time + " " + TYPE.INT+
            ")";

    public MaterialsBean() {
    }

    public MaterialsBean(int id, String name,int time) {
        this.id = id;
        this.materialsName = name;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return materialsName;
    }

    public void setName(String name) {
        this.materialsName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaterialsBean that = (MaterialsBean) o;

        if (id != that.id) return false;
        return !(materialsName != null ? !materialsName.equals(that.materialsName) : that.materialsName != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (materialsName != null ? materialsName.hashCode() : 0);
        return result;
    }
}
