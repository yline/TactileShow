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
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.tactileshow.util.macro;
import com.yline.application.SDKManager;
import com.yline.log.LogFileUtil;
import com.yline.utils.LogUtil;

import java.util.List;
import java.util.UUID;

/**
 * 为了蓝牙连接
 * Vivo手机，需要手动打开定位服务；才能扫描到
 *
 * @author yline 2017/9/12 -- 19:18
 * @version 1.0.0
 */
public class BluetoothHelper {
    private static final String TAG = "xxx-BluetoothHelper";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private boolean isScan;

    // 扫描时，回调
    private OnScanCallback onScanCallback;
    private OnConnectCallback onConnectCallback;

    /* --------------------------------------------- 初始化 ------------------------------------------------- */
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

    /* --------------------------------------------- 扫描 ------------------------------------------------- */

    /**
     * 开始扫描
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

    private void scanDevice(boolean enable, long milliSecond) {
        if (enable) {
            SDKManager.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isScan) {
                        isScan = false;

                        LogUtil.v("stop scan by outOfTime versionCode = " + Build.VERSION.SDK_INT);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                            BluetoothLeScanner leScanner = mBluetoothAdapter.getBluetoothLeScanner();
                            leScanner.stopScan(getScanCallback());
                        } else {
                            mBluetoothAdapter.stopLeScan(getLeScanCallback());
                        }

                        if (null != onScanCallback) {
                            onScanCallback.onFinish();
                        }
                    }
                }
            }, milliSecond);

            isScan = true;
            LogUtil.v("start scan versionCode = " + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                BluetoothLeScanner leScanner = mBluetoothAdapter.getBluetoothLeScanner();
                leScanner.startScan(getScanCallback());
            } else {
                mBluetoothAdapter.startLeScan(getLeScanCallback());
            }

            if (null != onScanCallback) {
                onScanCallback.onStart();
            }
        } else {
            isScan = false;
            LogUtil.v("stop scan by user versionCode = " + Build.VERSION.SDK_INT);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                BluetoothLeScanner leScanner = mBluetoothAdapter.getBluetoothLeScanner();
                leScanner.stopScan(getScanCallback());
            } else {
                mBluetoothAdapter.stopLeScan(getLeScanCallback());
            }
            if (null != onScanCallback) {
                onScanCallback.onBreak();
            }
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback;

    private BluetoothAdapter.LeScanCallback getLeScanCallback() {
        if (null == leScanCallback) {
            leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    LogUtil.v("scan callback device = " + device + ", rssi = " + rssi + ", scanRecord = " + scanRecord);

                    SDKManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (null != onScanCallback) {
                                onScanCallback.onScanning(device, rssi, scanRecord);
                            }
                        }
                    });
                }
            };
        }
        return leScanCallback;
    }

    private ScanCallback scanCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback getScanCallback() {
        if (null == scanCallback) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    LogUtil.v("scan callback callbackType = " + callbackType + ", device = " + result.getDevice() + ", rssi = " + result.getRssi() + ", scanRecord = " + (null == result.getScanRecord() ? null : result.getScanRecord().getBytes()));

                    SDKManager.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (null != onScanCallback) {
                                onScanCallback.onScanning(result.getDevice(), result.getRssi(), (null == result.getScanRecord() ? null : result.getScanRecord().getBytes()));
                            }
                        }
                    });
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);

                    LogUtil.v("scan callback results size = " + (null == results ? "null" : String.valueOf(results.size())));
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);

                    LogUtil.v("scan callback failed errorCode = " + errorCode);
                }
            };
        }
        return scanCallback;
    }

    /* --------------------------------------------- 链接 ------------------------------------------------- */
    private int mLogCharChanged = 0;

    /**
     * 连接蓝牙
     *
     * @param context         上下文
     * @param bluetoothDevice 连接的设备
     * @param autoConnect     是否自动连接
     * @return 蓝牙名称
     */
    public String connectGatt(Context context, final BluetoothDevice bluetoothDevice, boolean autoConnect) {
        LogUtil.v("connect bluetoothDevice: " + (null == bluetoothDevice ? "null" : "name = " + bluetoothDevice.getName() + ", address = " + bluetoothDevice.getAddress()));
        if (null != bluetoothDevice) {
            if (null != mBluetoothGatt) {
                mBluetoothGatt.close();
            }
            mBluetoothGatt = bluetoothDevice.connectGatt(context, autoConnect, new BluetoothGattCallback() {

                // 蓝牙连接状态
                @Override
                public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    LogFileUtil.v("connect stateChange gatt = " + gatt + ",status = " + status + ", newState = " + newState);

                    if (newState == BluetoothProfile.STATE_CONNECTED) { // {2}
                        discoverServices(gatt);
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断掉重连，就在这里了 {0}
                        if (null != gatt) {
                            gatt.connect();
                        }
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
                    LogFileUtil.v("connect ServiceDisCovered status = " + status);

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
                    LogFileUtil.v("connect characteristicRead characteristic = " + (null == characteristic ? "null" : new String(characteristic.getValue())) + ", status = " + status);

                    if (null != onConnectCallback) {
                        onConnectCallback.onCharacteristicRead(gatt, characteristic, status);
                    }
                }

                // 字符改变
                @Override
                public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);

                    boolean isLog = mLogCharChanged % 100 == 0;
                    if (isLog) {
                        mLogCharChanged = 1;
                        LogFileUtil.v("connect characteristicChanged characteristic = " + (null == characteristic ? "null" : new String(characteristic.getValue())));
                    } else {
                        mLogCharChanged++;
                    }

                    if (null != onConnectCallback) {
                        onConnectCallback.onCharacteristicChanged(gatt, characteristic, isLog);
                    }
                }
            });
            return bluetoothDevice.getName();
        }
        return null;
    }

    /* --------------------------------------------- 发现服务，并通信 ------------------------------------------------- */
    private void discoverServices(BluetoothGatt gatt) {
        if (null != gatt) {
            boolean discoverResult = gatt.discoverServices();
            LogFileUtil.v("discoverService result = " + discoverResult);
        }
        LogFileUtil.v("discoverService mBluetoothGatt is null; failed");
    }

    public boolean enableConfig(BluetoothGatt gatt, BluetoothGattService gattService, String uuid, byte[] value) {
        LogFileUtil.v("enableConfig inputUuid = " + uuid + ",value = " + new String(value));
        if (null != gattService) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(uuid));
            if (null != characteristic) {
                characteristic.setValue(value);
                if (null != gatt) {
                    gatt.writeCharacteristic(characteristic);

                    return true;
                }
            }
        }
        return false;
    }

    public boolean enableData(BluetoothGatt gatt, BluetoothGattService gattService, String uuid) {
        LogFileUtil.v("enableData inputUuid = " + uuid);
        if (null != gattService) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(uuid));
            if (null != characteristic && null != gatt) {
                boolean notify = gatt.setCharacteristicNotification(characteristic, true);
                LogUtil.v("enableData: notify = " + notify);

                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(macro.UUID_CLIENT_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);

                return true;
            }
        }

        return false;
    }

    /**
     * close和disconnect的区别: close除了断开连接外，还会释放掉所有资源，导致不可以直接在后面的操作中用gatt对象的connect直接连接，
     * 而disconnect并不释放资源 ，所以，所有的资源还保存着，就可以用Gatt的connect进行简单恢复连接，
     * 而不是在device那一层进行操作
     * 调用disconnect断开连接，然后在回调函数中使用close()释放资源
     */
    public void closeBluetooth() {
        if (null == mBluetoothGatt) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public String printServiceInfo(BluetoothGatt gatt) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != gatt) {
            List<BluetoothGattService> gattServiceList = gatt.getServices();

            BluetoothGattService gattService;
            UUID serviceUUID;
            for (int i = 0; i < gattServiceList.size(); i++) {
                gattService = gattServiceList.get(i);
                serviceUUID = gattService.getUuid();

                stringBuilder.append("Main:");
                stringBuilder.append(serviceUUID);
                stringBuilder.append('\n');
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    stringBuilder.append("\tsub:");
                    stringBuilder.append(gattCharacteristic.getUuid());
                    stringBuilder.append('\n');
                }
            }
        } else {
            LogFileUtil.e(TAG, "printServiceInfo: gatt is null");
        }
        String serviceInfo = stringBuilder.toString();
        LogFileUtil.v("gatt ServiceInfo UUID = \n" + serviceInfo);
        return serviceInfo;
    }

    public BluetoothGattService printService(BluetoothGatt gatt, String uuid) {
        LogFileUtil.v("printService inputUUid = " + uuid);

        StringBuilder stringBuilder = new StringBuilder();
        BluetoothGattService gattService = null;
        if (null != gatt) {
            gattService = gatt.getService(UUID.fromString(uuid));
            if (null != gattService) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    stringBuilder.append("uuid:");
                    stringBuilder.append(gattCharacteristic.getUuid());
                    stringBuilder.append(";value:");
                    stringBuilder.append(new String(gattCharacteristic.getValue()));
                    stringBuilder.append('\n');
                }
                LogFileUtil.v("gatt Service Characteristic = \n" + stringBuilder.toString());
            } else {
                LogFileUtil.e(TAG, "getServiceInfo: mGattService is null");
            }
        } else {
            LogFileUtil.e(TAG, "getServiceInfo: mBluetoothGatt is null");
        }
        return gattService;
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
        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean isLog);
    }
}
