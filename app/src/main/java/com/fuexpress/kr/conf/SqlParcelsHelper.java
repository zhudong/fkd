package com.fuexpress.kr.conf;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fuexpress.kr.bean.BrandBean;
import com.fuexpress.kr.bean.MaterialsBean;

/**
 * Created by Administrator on 2016-10-31.
 */
public class SqlParcelsHelper extends SQLiteOpenHelper {

    public SqlParcelsHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "fkdData", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Parcel.sql);
        db.execSQL(MaterialsBean.sql);
        db.execSQL(BrandBean.sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            tabbleIsExist(db, "parcels");
            tabbleIsExist(db, MaterialsBean.Table.name);
            tabbleIsExist(db, BrandBean.Table.tb_name);
            onCreate(db);
        }
    }

    public boolean tabbleIsExist(SQLiteDatabase db, String tableName) {
//        "select sql from sqlite_master where type = 'table' and name = '%s'";
        Cursor c = null;
        try {
            String queryStr = "select * from sqlite_master where type = 'table' and name = '%s'";
            queryStr = String.format(queryStr, tableName);
            c = db.rawQuery(queryStr, null);
            String tableCreateSql = null;
            if (c != null && c.moveToNext()) {
                db.execSQL("DROP TABLE " + tableName);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return false;
    }


    public interface Parcel {
      /*  *//* 建立table *//*
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + FIELD_id
                + " INTEGER primary key autoincrement, " + " " + FIELD_TEXT
                + " text)";
        db.execSQL(sql);*/

        String TAB_NAME = "parcels";
        String NO = "no";
        String PARCELID = "parcelid";
        String parcelName = "parcelName";
        String parcelCount = "parcelCount";//包裹数量
        String productdescription = "productdescription";//
        String productprice = "productprice";//  = 3;//
        String weight = "weight";// = 4;//总重量
        String customeraddressid = "customeraddressid";// = 5;
        String customeraddress = "customeraddress";// = 5
        String qty = "qty";//= 6;//数量
        String shippingTitle = "shippingTitle";
        String shippingmethodid = "shippingmethodid";// = 7;/
        String image_num = "image_num";// = 8;//图片数量
        String wareHouseID = "wareHouseID";//
        String shippingFee = "shippingFee";//= INT;//图
        String images = "images";//= INT;//图片路径结
        String imagePaths = "imagePaths";//= INT;//图
        String defaultWeight = "defaultWeight";// 默认重量
        String saved = "saved";
        String currencyCode = "currencyCode";//币种
        String idCadInfo = "idCadInfo";//身份信息

        String pureShippingFee = "PRUESHIPPINGFEE";// 纯运费
        String ext1 = "EXT1";
        String ext2 = "EXT2";
        String ext3 = "EXT3";


        interface TYPE {
            String NUM = "NULL";       // 空值
            String INT = "INTEGER";   // 带符号整数，根据存入的数值的大小占据1,2,3,4,6或者8个字节
            String REAL = "REAL";   // 浮点数，采用8byte（即双精度）的IEEE格式表示
            String TEXT = "TEXT";  // 字符串文本，采用数据库的编码（UTF-8，UTF-16BE 或者UTF-
            String BLOB = "BLOB";
        }

        String sql = "CREATE TABLE " + TAB_NAME + "(" +
                NO + " INTEGER primary key autoincrement," +
                PARCELID + " " + TYPE.INT + "," +
                parcelName + " " + TYPE.TEXT + "," +
                parcelCount + " " + TYPE.INT + "," +
                productprice + " " + TYPE.REAL + "," +
                productdescription + " " + TYPE.TEXT + "," +
                weight + " " + TYPE.REAL + "," +
                customeraddressid + " " + TYPE.INT + "," +
                customeraddress + " " + TYPE.TEXT + "," +
                qty + " " + TYPE.INT + "," +
                shippingTitle + " " + TYPE.TEXT + "," +
                shippingmethodid + " " + TYPE.INT + "," +
                image_num + " " + TYPE.INT + "," +
                wareHouseID + " " + TYPE.INT + "," +
                shippingFee + " " + TYPE.REAL + "," +
                images + " " + TYPE.TEXT + "," +
                imagePaths + " " + TYPE.TEXT + "," +
                defaultWeight + " " + TYPE.REAL + "," +
                saved + " " + TYPE.INT + "," +
                currencyCode + " " + TYPE.TEXT + "," +
                idCadInfo + " " + TYPE.TEXT + "," +
                pureShippingFee + " " + TYPE.REAL + "," +
                ext1 + " " + TYPE.TEXT + "," +
                ext2 + " " + TYPE.TEXT + "," +
                ext3 + " " + TYPE.TEXT +
                ")";
    }



  /*  private int parcelid;
    private String parcelName;
    private int parcelCount;//包裹数量
    private String productdescription;//        = 2;//物品信息
    private float productprice;//  = 3;//申报价
    private float weight;// = 4;//总重量
    private int customeraddressid;// = 5;//地址id
    private String customeraddress;// = 5;//地址id
    private int qty;//= 6;//数量
    private String shippingTitle;
    private int shippingmethodid;// = 7;//物流id
    private int image_num;// = 8;//图片数量
    private int wareHouseID;//
    private float shippingFee;//= INT;//图片路径结构体数组
    private String images;//= INT;//图片路径结构体数组
    private String imagePaths;//= INT;//图片路径结构体数组
    private float defaultWeight;// 默认重量
    private boolean saved;
*/
}
