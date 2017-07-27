package com.fuexpress.kr.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fuexpress.kr.bean.BrandBean;
import com.fuexpress.kr.conf.SqlParcelsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/3/6.
 */
public class BrandManager {
    static BrandManager brandManager = new BrandManager();
    static SqlParcelsHelper helper;
    private static SQLiteDatabase writableDatabase;

    private BrandManager() {
    }

    public static BrandManager getInstance(Context context) {
        if (helper == null) {
            helper = new SqlParcelsHelper(context, null, null, 0);
        }
        if (writableDatabase == null) {
            writableDatabase = helper.getWritableDatabase();
        }
        return brandManager;
    }

    public List<String> getBrands(String key) {

        Cursor query = writableDatabase.query(BrandBean.Table.tb_name, new String[]{BrandBean.Table.name}
                , BrandBean.Table.name + " LIKE '%" + key + "%'", null, null, null, BrandBean.Table.time + " DESC");

//        Cursor query = writableDatabase.query(BrandBean.Table.tb_name, new String[]{BrandBean.Table.name}, null, null, null, null, BrandBean.Table.time + " DESC");
        List<String> list = new ArrayList<>();
        int i = 0;
        while (query.moveToNext()) {
            String string = query.getString(0);
            i++;
            if (i > 10) {
                deleteBrand(string);
            } else {
                list.add(string);
            }
        }
        return list;
    }

    public BrandBean getBrand(String brandName) {
        Cursor query = writableDatabase.query(BrandBean.Table.tb_name,
                new String[]{BrandBean.Table.name}, BrandBean.Table.name + "=?",
                new String[]{brandName}, null, null, BrandBean.Table.time + " DESC");
        BrandBean brand = null;
        while (query.moveToNext()) {
            String string = query.getString(0);
            brand = new BrandBean();
            brand.setName(string);
            return brand;
        }
        return brand;
    }


    public void deleteBrand(String name) {
        writableDatabase.delete(BrandBean.Table.tb_name, BrandBean.Table.name + "=?", new String[]{name});

    }


    public void appendBrand(String brandName) {
        if (TextUtils.isEmpty(brandName)) return;
        int cTime = (int) System.currentTimeMillis() / 1000;
        ContentValues contentValues = new ContentValues();
        contentValues.put(BrandBean.Table.name, brandName);
        contentValues.put(BrandBean.Table.time, cTime);
        writableDatabase.insert(BrandBean.Table.tb_name, null, contentValues);
    }


    public void updateBrand(String brandName) {
        int cTime = (int) System.currentTimeMillis() / 1000;
        ContentValues contentValues = new ContentValues();
        contentValues.put(BrandBean.Table.name, brandName);
        contentValues.put(BrandBean.Table.time, cTime);
        writableDatabase.update(BrandBean.Table.tb_name, contentValues, BrandBean.Table.name + "=?", new String[]{brandName});
    }
}
