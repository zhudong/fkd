package com.fuexpress.kr.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fuexpress.kr.bean.MaterialsBean;
import com.fuexpress.kr.conf.SqlParcelsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/1/17.
 */
public class MaterialsDao {

    static private SqlParcelsHelper sqlParcelsHelper;
    private static MaterialsDao mInstance;
    static private SQLiteDatabase mReadableDatabase;


    static public MaterialsDao getInstance(Context context) {
        sqlParcelsHelper = new SqlParcelsHelper(context, null, null, 0);
        mInstance = new MaterialsDao();
        mReadableDatabase = sqlParcelsHelper.getReadableDatabase();
        return mInstance;
    }


    public void deleteAll() {
        sqlParcelsHelper.getWritableDatabase().delete(MaterialsBean.Table.name, "", null);
    }

    public List<MaterialsBean> search(String key) {
//        SELECT 字段 FROM 表 WHERE 某字段 Like 条件
//        String sql = "SELECT * FROM " + MaterialsBean.Table.name + " WHERE u_name LIKE '%三%'"; 顺DESC  逆ASC
        List<MaterialsBean> list = new ArrayList<>();
        SQLiteDatabase db = sqlParcelsHelper.getWritableDatabase();
        Cursor c = db.query(MaterialsBean.Table.name, new String[]{MaterialsBean.Table.id, MaterialsBean.Table.materialsName, MaterialsBean.Table.time}
                , MaterialsBean.Table.materialsName + " LIKE '%" + key + "%'", null, null, null, MaterialsBean.Table.time + " DESC");
        while (c.moveToNext()) {
            list.add(new MaterialsBean(c.getInt(0), c.getString(1), c.getInt(2)));
        }
        c.close();
        db.close();
        return list;
    }


    public List<MaterialsBean> getAll() {
        List<MaterialsBean> list = new ArrayList<>();
        SQLiteDatabase db = sqlParcelsHelper.getWritableDatabase();
        Cursor c = db.query(MaterialsBean.Table.name, new String[]{MaterialsBean.Table.id, MaterialsBean.Table.materialsName, MaterialsBean.Table.time}, "", null, null, null, null);
        while (c.moveToNext()) {
            list.add(new MaterialsBean(c.getInt(0), c.getString(1), c.getInt(2)));
        }
        c.close();
        db.close();
        return list;
    }

    public void insert(List<MaterialsBean> materialsBeans) {
        SQLiteDatabase db = sqlParcelsHelper.getWritableDatabase();
        for (MaterialsBean bean : materialsBeans) {
            db.insert(MaterialsBean.Table.name, null, createValue(bean));
        }
        db.close();
    }

    public void update(MaterialsBean materialsBean) {
        SQLiteDatabase db = sqlParcelsHelper.getWritableDatabase();
        ContentValues value = createValue(materialsBean);
//        value.put(MaterialsBean.Table.time, materialsBean.getTime());
        db.update(MaterialsBean.Table.name, value, MaterialsBean.Table.id + "=?", new String[]{materialsBean.getId() + ""});
        db.close();
    }

    private ContentValues createValue(MaterialsBean bean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaterialsBean.Table.id, bean.getId());
        contentValues.put(MaterialsBean.Table.materialsName, bean.getName());
        contentValues.put(MaterialsBean.Table.time, bean.getTime());
        return contentValues;
    }
}
