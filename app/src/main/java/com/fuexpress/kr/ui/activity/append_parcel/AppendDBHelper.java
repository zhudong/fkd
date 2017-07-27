package com.fuexpress.kr.ui.activity.append_parcel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuan on 2016-6-21.
 */
public class AppendDBHelper extends SQLiteOpenHelper {
    public static String DB_NAME ="DB_NAME";
    public AppendDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
