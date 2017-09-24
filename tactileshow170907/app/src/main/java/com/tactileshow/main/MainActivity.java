package com.tactileshow.main;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tactileshow.helper.BluetoothHelper;
import com.tactileshow.helper.BroadcastModel;
import com.tactileshow.util.DataFile;
import com.tactileshow.util.Point3D;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;
import com.tactileshow.view.main.MainDialogHelper;
import com.tactileshow.view.main.MainListViewAdapter;
import com.yline.application.BaseApplication;
import com.yline.application.SDKConstant;
import com.yline.application.SDKManager;
import com.yline.log.LogFileUtil;
import com.yline.utils.PermissionUtil;

import java.util.List;

/*
 * 主界面的Activity。
 * 整个连接执行过程为：onMenuItemSelected里的macro.MENU_ITEMID_FRESH情况（当点击刷新时，进行设备扫描），scanLeDevice（真正开始扫描）
 * deviceListView.setOnItemClickListener（当点击设备时，进行连接）
 */
public class MainActivity extends Activity {
    private final static String TAG = "xxx-Main";

    private BluetoothHelper mBluetoothHelper;
    private MainDialogHelper mDialogHelper;

    private TextView helloTextView;
    private MenuItem freshMenuItem;

    private MainListViewAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.addActivity(this);

        String[] strings = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION};
        PermissionUtil.request(this, SDKConstant.REQUEST_CODE_PERMISSION, strings); // 权限申请

        LogFileUtil.i(TAG, "Fuck");

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        mBluetoothHelper = new BluetoothHelper(this);
        mBluetoothHelper.enableBluetoothForResult(this);

        mDialogHelper = new MainDialogHelper(this);

        // 初始化 控件
        initView();

        // Problem
        SDKManager.toast("蓝牙已开启");

        StaticValue.data_file = new DataFile();

        IntentFilter filter = new IntentFilter();
        filter.addAction(macro.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);

        helloTextView.setText("请按刷新开始搜索");

        // 蓝牙 所有事件 触发点
        initListener();
    }

    private void initView() {
        helloTextView = findViewById(R.id.layout_ble_hello);

        ListView deviceListView = findViewById(R.id.lv_ble_device);

        viewAdapter = new MainListViewAdapter(this);
        deviceListView.setAdapter(viewAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice bluetoothDevice = viewAdapter.getItem(i);

                LogFileUtil.i(TAG, "bluetoothDevice = " + bluetoothDevice);
                if (null != bluetoothDevice) {
                    String deviceName = mBluetoothHelper.connectGatt(MainActivity.this, bluetoothDevice, true);

                    if (!TextUtils.isEmpty(deviceName)) {
                        SDKManager.toast("连接" + deviceName);

                        mBluetoothHelper.stopScanDevice();

                        mDialogHelper.show();
                        mDialogHelper.setOnNegativeClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBluetoothHelper.closeBluetooth();
                            }
                        });
                    }
                }
            }
        });
    }

    private void initListener() {
        // 设定蓝牙扫描监听事件
        mBluetoothHelper.setOnScanCallback(new BluetoothHelper.OnScanCallback() {
            @Override
            public void onStart() {
                MenuItemCompat.setActionView(freshMenuItem, R.layout.activity_main_dialog_progressbar);
            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                boolean isSuccess = viewAdapter.addData(device); // 这里做了 去重复的操作
                if (isSuccess) {
                    SDKManager.toast("扫描到新BLE设备 " + device.getName());
                    String out_info = device.getAddress() + " " + device.getName() + " " + device.getBondState();
                    helloTextView.setText(helloTextView.getText() + "\n" + out_info);
                }
            }

            @Override
            public void onBreak() {
                MenuItemCompat.setActionView(freshMenuItem, null);
            }

            @Override
            public void onFinish() {
                MenuItemCompat.setActionView(freshMenuItem, null);
                helloTextView.setText(helloTextView.getText() + "\n" + "扫描结束");
            }
        });

        // 设定蓝牙连接监听事件
        mBluetoothHelper.setOnConnectCallback(new BluetoothHelper.OnConnectCallback() {
            @Override
            public void onConnectionStateChangeHandler(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mDialogHelper.setText("连接成功，开始寻找服务");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) // 当设备无法连接
                {
                    mDialogHelper.setText("连接失败，请返回重连");
                }
            }

            @Override
            public void onServicesDiscoveredHandler(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    mDialogHelper.setText("成功发现服务，开始启动服务");
                    mBluetoothHelper.logServiceInfo();

                    String ser_str = mBluetoothHelper.logService(macro.UUID_MAG_SER);
                    if (TextUtils.isEmpty(ser_str)) {
                        SDKManager.toast("服务获取失败");
                    } else {
                        SDKManager.toast("服务获取成功");
                    }

                    boolean isMAGValid = false;
                    if (enableConfig(macro.UUID_MAG_CON) && enableData(macro.UUID_MAG_DAT)) {
                        mDialogHelper.setText("温度数据激活成功");
                        isMAGValid = true;
                    } else {
                        mDialogHelper.setText("温度数据激活失败");
                    }

                    ser_str = mBluetoothHelper.logService(macro.UUID_HUM_SER);
                    if (TextUtils.isEmpty(ser_str)) {
                        SDKManager.toast("服务获取失败");
                    } else {
                        SDKManager.toast("服务获取成功");
                    }

                    boolean isHUMValid = false;
                    if (enableConfig(macro.UUID_HUM_CON) && enableData(macro.UUID_HUM_DAT)) {
                        mDialogHelper.setText("湿度数据激活成功");
                        isHUMValid = true;
                    } else {
                        mDialogHelper.setText("湿度数据激活失败");
                    }

                    LogFileUtil.i(TAG, "isMAGValid = " + isMAGValid + ", isMAGValid = " + isMAGValid);
                    if (isMAGValid || isHUMValid) {
                        SDKManager.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, TabActivity.class);
                                intent.putExtra("str", "come from first activity");
                                startActivityForResult(intent, macro.INTENT_BLEACTIVITY_TESTSHOW);
                                mDialogHelper.dismiss();
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                // TODO
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                /*
                Time t = new Time();
                t.setToNow();
                String str_time = t.format2445();*/

                String uuid = characteristic.getUuid().toString();

                Log.i(TAG, "onCharacteristicChanged: onCharacteristicChanged");

                if (uuid.equals(macro.UUID_HUM_DAT)) {
                    Point3D p3d_hum = convertHum(characteristic.getValue());
                    float hum = (float) p3d_hum.x;

                    Point3D p3d_temp = convertTemp(characteristic.getValue());
                    float temp = (float) p3d_temp.x;

                    BroadcastModel model = new BroadcastModel(System.currentTimeMillis(), hum, temp);

                    String actionMsg = BroadcastModel.toJson(model);
                    Intent broadIntent = new Intent(macro.BROADCAST_ADDRESS);
                    broadIntent.putExtra("msg", actionMsg);
                    sendBroadcast(broadIntent);

                    // updateBroadcast("#" + "TEMP" + "#" + str_time + "#" + p3d_temp.x);
                    // updateBroadcast("#" + "PRESS" + "#" + str_time + "#" + p3d_hum.x);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem debugMenuItem = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_DEBUG, 0, "测试");
        freshMenuItem = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_FRESH, 1, "搜索");
        freshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuItem exitMenuItem = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_EXIT, 2, "退出");
        exitMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == macro.MENU_ITEMID_FRESH) {
            mBluetoothHelper.closeBluetooth();
            viewAdapter.clear();

            helloTextView.setText("开始搜索");
            mBluetoothHelper.startScanDevice(macro.BLE_SCAN_PERIOD);
        } else if (item.getItemId() == macro.MENU_ITEMID_EXIT) {
            Log.w(TAG, "滚");
            finish();
        } else if (item.getItemId() == macro.MENU_ITEMID_DEBUG) {
            Log.w(TAG, "测试模式");
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TabActivity.class);
            intent.putExtra("str", "come from first activity TEST");
            startActivityForResult(intent, macro.INTENT_BLEACTIVITY_TESTSHOW);
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /*
     * 读取数据前的配置工作，温度和湿度传感器的读取都要执行这个方法
     */
    private boolean enableData(String uuid) {
        boolean result = mBluetoothHelper.enableData(uuid);

        // 传输协议，需要一定的时间
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean enableConfig(String uuid) {
        // 此处开始按照协议对内容进行获取
        byte[] val = new byte[1];
        val[0] = 1;

        boolean result = mBluetoothHelper.enableConfig(uuid, val);

        // 传输协议，需要一定的时间
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode = " + requestCode + ", resultCode = " + ", data = " + data);
        if (requestCode == macro.INTENT_BLEACTIVITY_TESTSHOW) {
            mBluetoothHelper.closeBluetooth();
            if (macro.SETTING_EXIT_DIRECTLY) // 上一个activity要求直接退出。
            {
                finish();
            }
        }
    }

    public Point3D convertHum(final byte[] value) {
        int a = shortUnsignedAtOffset(value, 2);   // 湿度

        // bits [1..0] are status bits and need to be cleared according
        // to the user guide, but the iOS code doesn't bother. It should
        // have minimal impact.
        a = a - (a % 4);

        return new Point3D((-6f) + 125f * (a / 65535f), 0, 0);    //湿度
    }

    public Point3D convertTemp(final byte[] value) {
        int a = shortUnsignedAtOffset(value, 0);     // 温度
        return new Point3D(-46.85f + 175.72f * (a / 65535f), 0, 0);    // 温度
    }

    public Point3D convertMag(final byte[] value) {    // 压力，暂时没用到该数据
        Point3D mcal = MagnetometerCalibrationCoefficients.INSTANCE.val;
        // Multiply x and y with -1 so that the values correspond with the image in the app
        float x = shortSignedAtOffset(value, 0) * (2000f / 65536f) * -1;
        float y = shortSignedAtOffset(value, 2) * (2000f / 65536f) * -1;
        float z = shortSignedAtOffset(value, 4) * (2000f / 65536f);

        return new Point3D(x - mcal.x, y - mcal.y, z - mcal.z);
    }

    private enum MagnetometerCalibrationCoefficients {
        INSTANCE;

        Point3D val = new Point3D(0.0, 0.0, 0.0);
    }

    private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset + 1] & 0xFF; // // Interpret MSB as signed
        return (upperByte << 8) + lowerByte;
    }

    private static Integer shortSignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset + 1]; // // Interpret MSB as signed
        return (upperByte << 8) + lowerByte;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> result = PermissionUtil.requestHandle(SDKConstant.REQUEST_CODE_PERMISSION, requestCode, permissions, grantResults);
        LogFileUtil.i(SDKConstant.TAG_HANDLE_PERMISSION, result.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.removeActivity(this);

        unregisterReceiver(mGattUpdateReceiver);
        mBluetoothHelper.closeBluetooth();
    }

    // 广播 A，只是打个日志
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getStringExtra("msg");
            Log.i(TAG, "广播来了 " + action);
            helloTextView.setText(action);
        }
    };
}
