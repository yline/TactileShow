package com.tactileshow;

import android.util.Log;

import org.junit.Test;

import java.nio.ByteBuffer;

public class BlueSimpleTest {
    private static final String TAG = "xxx-Blue";

    @Test
    public void testChange() throws Exception {
        float hum = 0;
        float temp = 0;
        float header = 0;

        int length = 100;


        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        byte[] originBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 'X', 1, 2, 3, 4, 5, 6, 7, 8, 10, 'X', 1, 2, 3, 4, 5, 6, 7, 8, 10, 'X', 1, 2, 3, 4, 5, 6, 7, 8, 10, 'X', 1, 2, 3, 4, 5, 6, 7, 8, 10, 'X', 1, 2, 3, 4, 5, 6, 7, 8, 10, 'X'};
        byteBuffer.put(originBytes);

        byteBuffer.put(originBytes);

        byteBuffer.put(originBytes);

        Log.i(TAG, "testChange: length = " + byteBuffer.position());

        byte[] bytes = new byte[1024];
    }

    private static final int CACHE_CAPACITY = 100;
    private byte[] mCacheBytes = new byte[CACHE_CAPACITY];
    private int mRealDataSize = 0;

    @Test
    public void testSingle() throws Exception {
        byte[] originBytes = new byte[]{'1', '2', '3', 'X', '4', '6', 'X'};

        for (int i = 0; i < 1234; i++) {
            readByte(mCacheBytes, originBytes, mRealDataSize, new OnReceiveCallback() {
                @Override
                public void onUpdateParam(byte[] cacheByte, int realSize) {
                    mCacheBytes = cacheByte;
                    mRealDataSize = realSize;
                }

                @Override
                public void onCalculateResult(float hum, float temp, float header) {
                    Log.i(TAG, "testSingle: hum = " + hum + ", temp = " + temp + ", header = " + header);
                }
            });
        }
    }

    private void readByte(byte[] byteBuffer, byte[] originBytes, int realDataSize, OnReceiveCallback callback) {
        // 计算是否符合规则，并返回偏移量
        int nextStartPosition = 0;
        int position;
        for (position = 0; position < realDataSize - 30; position++) {
            if (byteBuffer[position] == 'X') {
                float hum = (float) readStreamByte(byteBuffer, position);
                float temp = (float) readStreamByte(byteBuffer, position + 10);
                float header = (float) readStreamByte(byteBuffer, position + 20);

                if (null != callback) {
                    callback.onCalculateResult(hum, temp, header);
                }

                nextStartPosition = position + 30;
                break;
            }
        }
        nextStartPosition = (0 == nextStartPosition ? position : nextStartPosition);

        // 读取原始数据
        byte[] tempBytes = new byte[CACHE_CAPACITY];
        int remainderDataSize = realDataSize - nextStartPosition;
        // 将cacheBytes上的尾字节，搬移到tempBytes上
        System.arraycopy(byteBuffer, nextStartPosition, tempBytes, 0, remainderDataSize);
        // 将originBytes上的字节，添加tempBytes上
        System.arraycopy(originBytes, 0, tempBytes, remainderDataSize, originBytes.length);
        if (null != callback) {
            callback.onUpdateParam(tempBytes, originBytes.length + remainderDataSize);
        }
    }

    private double readStreamByte(byte[] bf, int position) {
        return (bf[position + 4] - '0') + 0.1 * (bf[position + 6] - '0') + 0.01 * (bf[position + 7] - '0') + 0.001 * (bf[position + 8] - '0') + 0.0001 * (bf[position + 9] - '0') + 0.00001 * (bf[position + 10] - '0');
    }

    public interface OnReceiveCallback {
        /**
         * 更新参数
         *
         * @param cacheByte 新的缓存byte数组
         * @param realSize  byte数组中真实数据的大小
         */
        void onUpdateParam(byte[] cacheByte, int realSize);

        /**
         * 解析出来的数据结果
         *
         * @param hum    温度
         * @param temp   湿度
         * @param header 第三渠道数据
         */
        void onCalculateResult(float hum, float temp, float header);
    }
}
