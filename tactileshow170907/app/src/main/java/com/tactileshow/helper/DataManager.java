package com.tactileshow.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.tactileshow.main.IApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * 将 数据写入文件；按照现有规则
 * 日期为文件，
 *
 * @author yline 2017/9/24 -- 16:51
 * @version 1.0.0
 */
public class DataManager {
    public static final int Error = -1;

    private final DataOpenHelper openHelper;
    private final SQLiteDatabase sqLiteDatabase;
    private final String[] mAllColumns;

    private SQLiteStatement insertOrReplaceStatement;

    private DataManager(Context context) {
        this.openHelper = new DataOpenHelper(context, 1);
        this.sqLiteDatabase = openHelper.getWritableDatabase();
        this.mAllColumns = new String[]{DataOpenHelper.Table.stamp.name, DataOpenHelper.Table.hum.name, DataOpenHelper.Table.temp.name, DataOpenHelper.Table.header.name, DataOpenHelper.Table.footer.name};
    }

    public static DataManager getInstance() {
        return DataManagerHolder.getInstance();
    }

    private static class DataManagerHolder {
        private static DataManager sInstance;

        private static DataManager getInstance() {
            if (null == sInstance) {
                sInstance = new DataManager(IApplication.getApplication());
            }
            return sInstance;
        }
    }

    public long insert(BroadcastModel model) {
        long rowId;
        if (sqLiteDatabase.isDbLockedByCurrentThread()) {
            rowId = executeInsert(model);
        } else {
            // Do TX to acquire a connection before locking the stmt to avoid deadlocks
            sqLiteDatabase.beginTransaction();
            try {
                rowId = executeInsert(model);
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
        }

        return rowId;
    }

    private long executeInsert(BroadcastModel model) {
        // sql insert
        insertOrReplaceStatement = getInsertOrReplaceStatement(sqLiteDatabase);
        synchronized (insertOrReplaceStatement) {
            insertOrReplaceStatement.clearBindings();
            boolean bindResult = bindValues(insertOrReplaceStatement, model);
            return bindResult ? insertOrReplaceStatement.executeInsert() : Error;
        }
    }

    private boolean bindValues(SQLiteStatement stmt, BroadcastModel model) {
        stmt.bindLong(1 + DataOpenHelper.Table.stamp.ordinal, model.getTime());
        stmt.bindDouble(1 + DataOpenHelper.Table.hum.ordinal, model.getHum());
        stmt.bindDouble(1 + DataOpenHelper.Table.temp.ordinal, model.getTemp());

        return true;
    }

    public void loadAsync(final long fromStamp, final long toStamp, final OnReadCallback callback) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Log.i("xxx-", "run: loadAsync");

                if (fromStamp >= toStamp) {
                    if (null != callback) {
                        callback.onFailure("输入参数不合法");
                    }
                }

                List<BroadcastModel> resultList = load(fromStamp, toStamp);
                if (null != callback) {
                    callback.onSuccess(resultList);
                }
            }
        });
    }

    public long count() {
        long count = -1;
        Cursor cursor = sqLiteDatabase.query(DataOpenHelper.DefaultSQLiteName, null, null, null, null, null, null);
        try {
            if (null != cursor) {
                count = cursor.getCount();
            }
            return count;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    private List<BroadcastModel> load(long fromStamp, long toStamp) {
        String sql = String.format(Locale.CHINA, "select * from %s where %s between %d and %d order by %s", DataOpenHelper.DefaultSQLiteName, DataOpenHelper.Table.stamp.name, fromStamp, toStamp, DataOpenHelper.Table.stamp.name);
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        return loadAndCloseCursor(cursor);
    }

    private List<BroadcastModel> loadAndCloseCursor(Cursor cursor) {
        try {
            int count = cursor.getCount();
            if (0 == count) {
                return new ArrayList<>();
            }

            List<BroadcastModel> resultList = new ArrayList<>();
            BroadcastModel model;
            if (cursor.moveToFirst()) {
                do {
                    model = readModel(cursor);
                    if (null != model) {
                        resultList.add(readModel(cursor));
                    }
                } while (cursor.moveToNext());
            }
            return resultList;
        } finally {
            cursor.close();
        }
    }

    private BroadcastModel readModel(Cursor cursor) {
        long stamp = cursor.isNull(DataOpenHelper.Table.stamp.ordinal) ? BroadcastModel.Empty : cursor.getLong(DataOpenHelper.Table.stamp.ordinal);
        float hum = cursor.isNull(DataOpenHelper.Table.hum.ordinal) ? BroadcastModel.Empty : cursor.getLong(DataOpenHelper.Table.hum.ordinal);
        float temp = cursor.isNull(DataOpenHelper.Table.temp.ordinal) ? BroadcastModel.Empty : cursor.getLong(DataOpenHelper.Table.temp.ordinal);

        if (BroadcastModel.Empty == stamp || (BroadcastModel.Empty == hum && BroadcastModel.Empty == temp)) {
            return null;
        } else {
            return new BroadcastModel(stamp, hum, temp);
        }
    }

    private SQLiteStatement getInsertOrReplaceStatement(SQLiteDatabase db) {
        if (insertOrReplaceStatement == null) {
            String sql = genInsertSql("insert or replace into");
            SQLiteStatement newInsertOrReplaceStatement = db.compileStatement(sql);
            synchronized (this) {
                if (insertOrReplaceStatement == null) {
                    insertOrReplaceStatement = newInsertOrReplaceStatement;
                }
            }
            if (insertOrReplaceStatement != newInsertOrReplaceStatement) {
                newInsertOrReplaceStatement.close();
            }
        }
        return insertOrReplaceStatement;
    }

    private String genInsertSql(String header) {
        StringBuilder stringBuilder = new StringBuilder(header + " ");
        stringBuilder.append('"').append(DataOpenHelper.DefaultSQLiteName).append('"').append(" (");

        int length = mAllColumns.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append('"').append(mAllColumns[i]).append('"');
            if (i < length - 1) {
                stringBuilder.append(',');
            }
        }

        stringBuilder.append(") values(");

        for (int i = 0; i < length; i++) {
            stringBuilder.append('?');
            if (i < length - 1) {
                stringBuilder.append(',');
            }
        }
        stringBuilder.append(')');

        return stringBuilder.toString();
    }

    public interface OnReadCallback {
        /**
         * 读取本地信息失败
         */
        void onFailure(String errorMsg);

        /**
         * 读取本地信息成功
         */
        void onSuccess(List<BroadcastModel> modelList);
    }
}
