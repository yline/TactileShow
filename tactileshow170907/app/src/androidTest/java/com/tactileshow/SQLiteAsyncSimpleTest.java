package com.tactileshow;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

public class SQLiteAsyncSimpleTest {
    private static final String TAG = "xxx-";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testName() throws Exception {
        long time = System.currentTimeMillis();
        Log.i(TAG, "setUp: time = " + time);

        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        Log.i(TAG, "testName: dateStr = " + dateStr);
    }
}
