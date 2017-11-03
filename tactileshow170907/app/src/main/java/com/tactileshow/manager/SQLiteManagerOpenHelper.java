package com.tactileshow.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yline.log.LogFileUtil;

import java.util.Locale;

/**
 * 数据库
 *
 * @author yline 2017/9/27 -- 17:35
 * @version 1.0.0
 */
public class SQLiteManagerOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "xxx-";

    public static final String DefaultSQLiteName = "tactileShow";

    public static class Table {
        public final static SQLiteManagerOpenHelper.Property stamp = new SQLiteManagerOpenHelper.Property(0, Long.class, "stamp", true);
        public final static SQLiteManagerOpenHelper.Property hum = new SQLiteManagerOpenHelper.Property(1, Float.class, "hum", false);
        public final static SQLiteManagerOpenHelper.Property temp = new SQLiteManagerOpenHelper.Property(2, Float.class, "temp", false);
        public final static SQLiteManagerOpenHelper.Property header = new SQLiteManagerOpenHelper.Property(3, Float.class, "header", false);
        public final static SQLiteManagerOpenHelper.Property footer = new SQLiteManagerOpenHelper.Property(4, Float.class, "footer", false);
    }

    public SQLiteManagerOpenHelper(Context context, int version) {
        super(context, DefaultSQLiteName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogFileUtil.i(TAG, "onCreate: ");

        createTable(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        LogFileUtil.i(TAG, "onOpen: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogFileUtil.i(TAG, "onUpgrade: oldVersion = " + oldVersion + ", newVersion = " + newVersion);
    }

    private static void createTable(SQLiteDatabase db) {
        String sql = String.format(Locale.CHINA, "create table if not exists %s(%s long primary key, %s float, %s float, %s float, %s float)",
                DefaultSQLiteName, Table.stamp.name, Table.hum.name, Table.temp.name, Table.header.name, Table.footer.name);
        db.execSQL(sql);
    }

    public static class Property {
        public final int ordinal;

        public final Class<?> type;

        public final String name;

        public final boolean pKey;

        public Property(int ordinal, Class<?> type, String name, boolean pKey) {
            this.ordinal = ordinal;
            this.type = type;
            this.name = name;
            this.pKey = pKey;
        }
    }
}
