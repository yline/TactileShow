package com.record;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "xxx-Test";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.record", appContext.getPackageName());
    }

    @Test
    public void testBuild() throws Exception {
        StringBuilder sb = new StringBuilder("ffmpeg");
        sb.append(" -i");
        sb.append(" " + "path");
        sb.append(" -vcodec");
        sb.append(" copy");
        sb.append(" -acodec");
        sb.append(" copy");
        sb.append(" -ss");
        sb.append(" " + "startStr");
        sb.append(" -t");
        sb.append(" " + "endStr");
        sb.append(" " + "output");
        //
        Log.i(TAG, "testBuild: sb:" + sb.toString());
    }
}
