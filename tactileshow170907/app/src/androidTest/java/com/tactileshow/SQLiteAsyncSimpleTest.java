package com.tactileshow;

import android.util.Log;

import com.tactileshow.manager.TactileModel;
import com.tactileshow.manager.SQLiteManager;
import com.tactileshow.manager.SQLiteManagerOpenHelper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SQLiteAsyncSimpleTest {
    private static final String TAG = "xxx-";

    private Random mRandom;

    @Before
    public void setUp() throws Exception {
        mRandom = new Random();
    }

    @Test
    public void testName() throws Exception {
        String sql = String.format(Locale.CHINA, "select * from %s where %s between %d and %d", SQLiteManagerOpenHelper.DefaultSQLiteName, SQLiteManagerOpenHelper.Table.stamp.name, 1506510999000l, 1506511999000l);

        Log.i(TAG, "testName: sql = " + sql);
    }

    @Test
    public void testSQLite() throws Exception {
        long start = System.currentTimeMillis();

        long beforeCount = SQLiteManager.getInstance().count();

        long rowId = -1;
        for (int i = 0; i < 20; i++) {
            rowId = SQLiteManager.getInstance().insert(createModel());
        }

        Assert.assertEquals(beforeCount + 20, SQLiteManager.getInstance().count());
        Log.i(TAG, "testSQLite: diff = " + (System.currentTimeMillis() - start));

        SQLiteManager.getInstance().loadAsync(Long.MIN_VALUE, Long.MAX_VALUE, new SQLiteManager.OnReadCallback() {
            @Override
            public void onFailure(String errorMsg) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onSuccess(List<TactileModel> modelList) {
                Log.i(TAG, "onSuccess: size = " + modelList.size());
            }
        });
    }

    private TactileModel createModel() {
        return new TactileModel(mRandom.nextLong(), mRandom.nextFloat(), mRandom.nextFloat());
    }
}
