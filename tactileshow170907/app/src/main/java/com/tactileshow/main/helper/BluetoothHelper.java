package com.tactileshow.main.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tactileshow.util.macro;
import com.yline.application.SDKManager;
import com.yline.log.LogFileUtil;

import java.util.List;
import java.util.UUID;

/**
 * 为了蓝牙连接
 *
 * @author yline 2017/9/12 -- 19:18
 * @version 1.0.0
 */
public class BluetoothHelper {
    private static final String TAG = "xxx-BluetoothHelper";

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattService mGattService;

    private boolean isScan;

    // 扫描时，回调
    private OnScanCallback onScanCallback;

    private OnConnectCallback onConnectCallback;

    public BluetoothHelper(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void enableBluetoothForResult(Activity activity) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, macro.INTENT_REQUEST_ENABLE_BT);
        }
    }

    /**
     * 开始扫描
     *
     * @param milliSecond 单位 毫秒
     */
    public void startScanDevice(long milliSecond) {
        scanDevice(true, milliSecond);
    }

    /**
     * 结束扫描
     */
    public void stopScanDevice() {
        scanDevice(false, 0);
    }

    /**
     * 连接蓝牙
     *
     * @param context         上下文
     * @param bluetoothDevice 连接的设备
     * @param autoConnect     是否自动连接
     * @return
     */
    public String connectGatt(Context context, final BluetoothDevice bluetoothDevice, boolean autoConnect) {
        if (null != bluetoothDevice) {
            mBluetoothGatt = bluetoothDevice.connectGatt(context, autoConnect, new BluetoothGattCallback() {

                // 蓝牙连接状态
                @Override
                public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    LogFileUtil.i(TAG, "onConnectionStateChange status = " + status + ", newState = " + newState);
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        // 发现服务
                        discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断掉重连，就在这里了
                        mBluetoothGatt.connect();
                    }

                    SDKManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (null != onConnectCallback) {
                                onConnectCallback.onConnectionStateChangeHandler(gatt, status, newState);
                            }
                        }
                    });
                }

                // 发现新的服务端
                @Override
                public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                    super.onServicesDiscovered(gatt, status);

                    LogFileUtil.i(TAG, "onServicesDiscovered status = " + status);

                    SDKManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (null != onConnectCallback) {
                                onConnectCallback.onServicesDiscoveredHandler(gatt, status);
                            }
                        }
                    });
                }

                // 读入数据
                @Override
                public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    LogFileUtil.i(TAG, "onCharacteristicRead characteristic = " + characteristic + ", status = " + status);

                    if (null != onConnectCallback) {
                        onConnectCallback.onCharacteristicRead(gatt, characteristic, status);
                    }
                }

                // 字符改变
                @Override
                public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    LogFileUtil.i(TAG, "onCharacteristicChanged characteristic = " + characteristic);

                    if (null != onConnectCallback) {
                        onConnectCallback.onCharacteristicChanged(gatt, characteristic);
                    }
                }
            });
            return bluetoothDevice.getName();
        }
        return null;
    }

    /* 发现服务 */
    public void discoverServices() {
        if (null != mBluetoothGatt) {
            mBluetoothGatt.discoverServices();
        }
    }

    public boolean enableConfig(String uuid, byte[] value) {
        if (null != mGattService) {
            BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(uuid));
            if (null != characteristic) {
                characteristic.setValue(value);
                if (null != mBluetoothGatt) {
                    mBluetoothGatt.writeCharacteristic(characteristic);

                    return true;
                }
            }
        }
        return false;
    }

    public boolean enableData(String uuid) {
        if (null != mGattService) {
            BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(uuid));
            if (null != characteristic && null != mBluetoothGatt) {
                boolean notify = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                Log.i(TAG, "enableData: notify = " + notify);

                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(macro.UUID_CLIENT_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);

                return true;
            }
        }

        return false;
    }

    public void closeBluetooth() {
        if (null == mBluetoothGatt) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public String logServiceInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != mBluetoothGatt) {
            List<BluetoothGattService> gattServiceList = mBluetoothGatt.getServices();
            UUID serviceUUID;
            for (int i = 0; i < gattServiceList.size(); i++) {
                serviceUUID = gattServiceList.get(i).getUuid();
                Log.i(TAG, "找到的服务为: " + i + serviceUUID);
                stringBuilder.append('\n');
                stringBuilder.append(serviceUUID);
            }
        } else {
            Log.e(TAG, "getServiceInfo: mBluetoothGatt is null");
        }
        return stringBuilder.toString();
    }

    public String logService(String uuid) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != mBluetoothGatt) {
            mGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
            List<BluetoothGattCharacteristic> tempCharList;
            if (null != mGattService) {
                tempCharList = mGattService.getCharacteristics();
                UUID charUuid;
                byte[] valueByte;
                for (int i = 0; i < tempCharList.size(); i++) {
                    charUuid = tempCharList.get(i).getUuid();
                    valueByte = tempCharList.get(i).getValue();

                    Log.i(TAG, "找到的特征为: " + i + charUuid + " " + valueByte);
                    stringBuilder.append('\n');
                    stringBuilder.append(charUuid);
                    stringBuilder.append(" ");
                    stringBuilder.append(valueByte);
                }
            } else {
                Log.e(TAG, "getServiceInfo: mGattService is null");
            }
        } else {
            Log.e(TAG, "getServiceInfo: mBluetoothGatt is null");
        }
        return stringBuilder.toString();
    }

    public void setOnScanCallback(OnScanCallback onScanCallback) {
        this.onScanCallback = onScanCallback;
    }

    /**
     * 暂时只实现了四个方法
     *
     * @param callback 回调
     */
    public void setOnConnectCallback(OnConnectCallback callback) {
        this.onConnectCallback = callback;
    }

    private void scanDevice(boolean enable, long milliSecond) {
        if (enable) {
            SDKManager.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isScan) {
                        isScan = false;

                        mBluetoothAdapter.stopLeScan(leScanCallback);
                        if (null != onScanCallback) {
                            LogFileUtil.i(TAG, "onScanCallback onFinish");
                            onScanCallback.onFinish();
                        }
                    }
                }
            }, milliSecond);

            isScan = true;
            mBluetoothAdapter.startLeScan(leScanCallback);
            if (null != onScanCallback) {
                LogFileUtil.i(TAG, "onScanCallback onStart");
                onScanCallback.onStart();
            }
        } else {
            isScan = false;

            mBluetoothAdapter.stopLeScan(leScanCallback);
            if (null != onScanCallback) {
                LogFileUtil.i(TAG, "onScanCallback onBreak");
                onScanCallback.onBreak();
            }
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            SDKManager.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (null != onScanCallback) {
                        LogFileUtil.i(TAG, "onScanCallback onScanning device = " + device + ", rssi = " + rssi + ", scanRecord = " + scanRecord);
                        onScanCallback.onScanning(device, rssi, scanRecord);
                    }
                }
            });
        }
    };

    // 蓝牙扫描 回调
    public interface OnScanCallback {
        /**
         * 扫描开始
         */
        void onStart();

        /**
         * 扫描到结果时回调
         *
         * @param device     远程设备
         * @param rssi       远程设备的RSSI值，0 if no RSSI value is available.
         * @param scanRecord 远程设备提供的记录功能
         */
        void onScanning(final BluetoothDevice device, int rssi, byte[] scanRecord);

        /**
         * 扫描 人工结束
         */
        void onBreak();

        /**
         * 扫描结束
         */
        void onFinish();
    }

    public interface OnConnectCallback {
        /**
         * 连接状态改变
         *
         * @param gatt
         * @param status
         * @param newState
         */
        void onConnectionStateChangeHandler(BluetoothGatt gatt, int status, int newState);

        /**
         * 发现 服务端
         *
         * @param gatt
         * @param status
         */
        void onServicesDiscoveredHandler(BluetoothGatt gatt, int status);

        /**
         * 读取 字节
         *
         * @param gatt
         * @param characteristic
         * @param status
         */
        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        /**
         * 传输 字节断开
         *
         * @param gatt
         * @param characteristic
         */
        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }
}
