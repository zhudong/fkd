package com.fuexpress.kr.base;

import android.content.Context;

/*import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;*/

/**
 * Created by yuan on 2016-6-26.
 */
public class DbConfig {

    /*private static DbConfig mConfig;
    private Context mContext;
    private RealmConfiguration Config;

    private DbConfig(Context context) {
        this.mContext = context;
    }

    public static DbConfig getInstance(Context context) {
        if (mConfig == null) {
            mConfig = new DbConfig(context);
        }
        return mConfig;
    }


    class MyMigrate implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();
            *//*if (oldVersion == 0) {
                schema.get("HelpSendParcelBean").addField("shippingTitle", String.class);
                oldVersion++;
            }
            if (oldVersion == 1) {
                RealmObjectSchema helpSendParcelBean = schema.get("HelpSendParcelBean");
                helpSendParcelBean.addField("saved", Boolean.class).setNullable("saved", true);
                oldVersion++;
            }
            if (oldVersion == 2) {
                RealmObjectSchema helpSendParcelBean = schema.get("HelpSendParcelBean");
                helpSendParcelBean.addField("defaultWeight", Float.class);
                oldVersion++;
            }*//*
        }
    }

    Realm realm = null;

    public Realm getRealm() {
        if (realm == null) {
            if (Config == null) {
                Config = new RealmConfiguration.Builder(mContext).name("ydkdb.realm").schemaVersion(0) // Must be bumped when the schema changes
                        .migration(new MyMigrate()).build();
            }
            realm = Realm.getInstance(Config);
        }
        return realm;
    }*/
}
