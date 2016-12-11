package com.tactileshow.activity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.tactileshow.application.IApplication;
import com.tactileshow.application.SDKConstant;
import com.tactileshow.base.BaseActivity;
import com.tactileshow.log.LogFileUtil;
import com.tactileshow.main.R;
import com.tactileshow.util.DataFile;
import com.tactileshow.util.Macro;
import com.tactileshow.util.StaticValue;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 主界面的Activity。
 * 整个连接执行过程为：onMenuItemSelected里的macro.MENU_ITEMID_FRESH情况（当点击刷新时，进行设备扫描），scanLeDevice（真正开始扫描）
 * lv_device.setOnItemClickListener（当点击设备时，进行连接）
 */
public class MainActivity extends BaseActivity
{
    // View
    private TextView tvHello;
    
    private ListView lvDevice;
    
    private ArrayAdapter<String> deviceApdater;
    
    private List<String> deviceList = new ArrayList<String>();
    
    private MenuItem freshMenuItem;
    
    // blueTooth
    BluetoothManager mBluetoothManager;
    
    BluetoothAdapter mBluetoothAdapter;
    
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    
    private boolean mIsScanning;
    
    private BluetoothGatt mBluetoothGatt;
    
    private BluetoothGattService mGattService;
    
    TextView tv_connect_info;
    
    AlertDialog dl_connect;
    
    private final static String TAG = "测试TAG";
    
    private int mConnectionState = STATE_DISCONNECTED;
    
    private static final int STATE_DISCONNECTED = 0; //设备无法连接
    
    private static final int STATE_CONNECTING = 1; //设备正在连接状态
    
    private static final int STATE_CONNECTED = 2; //设备连接完毕
    
    /**
     * 用于扫描时间设定
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == Macro.HANDLER_SCAN_STOPPED)
            {
                tvHello.setText(tvHello.getText() + "\n" + "扫描结束");
            }
            else if (msg.what == Macro.HANDLER_CONNECT_SUCCESS)
            {
                // IApplication.toast("连接已经建立");
                tv_connect_info.setText("连接成功，开始寻找服务");
            }
            else if (msg.what == Macro.HANDLER_CONNECT_FAILED)
            {
                // IApplication.toast("连接建立失败");
                tv_connect_info.setText("连接失败，请返回重连");
            }
            else if (msg.what == Macro.HANDLER_SERVICE_DISCOVERED)
            {
                // IApplication.toast("服务发现完毕");
                tv_connect_info.setText("成功发现服务，开始启动服务");
                boolean isHasValidData = false;
                ListServices();
                
                ListCharacters(Macro.UUID_BLE_SER);
                
                if (EnableConfig(Macro.UUID_BLE_CON) && EnableData(Macro.UUID_BLE_DAT))
                {
                    tv_connect_info.setText("蓝牙数据激活成功");
                    isHasValidData = true;
                }
                else
                {
                    tv_connect_info.setText("蓝牙数据激活失败");
                }
                
                if (isHasValidData == true)
                {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainTabActivity.class);
                    intent.putExtra("str", "come from first activity");
                    startActivityForResult(intent, Macro.INTENT_BLEACTIVITY_TESTSHOW);
                    dl_connect.dismiss();
                }
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ble);
        getActionBar().show();
        
        mBluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Macro.INTENT_REQUEST_ENABLE_BT);
        }
        else
        {
            IApplication.toast("蓝牙已开启");
        }
        
        initView();
        
        StaticValue.data_file = new DataFile();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(Macro.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);
    }
    
    /**
     * 初始化控件，并更新控件数据
     */
    private void initView()
    {
        tvHello = (TextView)findViewById(R.id.layout_ble_hello);
        tvHello.setText("请按刷新开始搜索");
        
        lvDevice = (ListView)findViewById(R.id.lv_ble_device);
        deviceApdater = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, deviceList);
        lvDevice.setAdapter(deviceApdater);
        
        lvDevice.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                BluetoothDevice tmpBleDevice = mLeDevices.get(position);
                
                IApplication.toast("连接" + tmpBleDevice.getName());
                
                scanLeDevice(false);
                mBluetoothGatt = tmpBleDevice.connectGatt(MainActivity.this, false, mGattCallback);
                showConnectDialog();
            }
        });
        
        updateDeviceList();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(SDKConstant.MENU_GROUPID_BLE, SDKConstant.MENU_ITEMID_DEBUG, 0, "测试");
        
        freshMenuItem = menu.add(SDKConstant.MENU_GROUPID_BLE, SDKConstant.MENU_ITEMID_FRESH, 1, "搜索");
        freshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        menu.add(SDKConstant.MENU_GROUPID_BLE, SDKConstant.MENU_ITEMID_EXIT, 2, "退出")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        if (item.getItemId() == SDKConstant.MENU_ITEMID_FRESH)
        {
            closeBle();
            mLeDevices.clear();
            updateDeviceList();
            tvHello.setText("开始搜索");
            scanLeDevice(true);
        }
        else if (item.getItemId() == SDKConstant.MENU_ITEMID_EXIT)
        {
            LogFileUtil.v(TAG, "退出");
            IApplication.finishActivity();
        }
        else if (item.getItemId() == SDKConstant.MENU_ITEMID_DEBUG)
        {
            LogFileUtil.v(TAG, "测试模式");
            
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainTabActivity.class);
            intent.putExtra("str", "come from first activity TEST");
            startActivityForResult(intent, Macro.INTENT_BLEACTIVITY_TESTSHOW);
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        unregisterReceiver(mGattUpdateReceiver);
        closeBle();
    }
    
    private void showConnectDialog()
    {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_ble_connect, null);
        
        AlertDialog.Builder builder_dl_connect = new AlertDialog.Builder(this);
        builder_dl_connect.setTitle("连接状态");
        builder_dl_connect.setView(view);
        builder_dl_connect.setNegativeButton("取消连接", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                closeBle();
                dialog.dismiss();
            }
        });
        tv_connect_info = (TextView)view.findViewById(R.id.tv_ble_connect_info);
        if (tv_connect_info == null)
        {
            Log.w(TAG, "NULL");
        }
        tv_connect_info.setText("正在连接中");
        
        dl_connect = builder_dl_connect.show();
    }
    
    private void closeBle()
    {
        if (mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    
    private void beginScanUI()
    {
        MenuItemCompat.setActionView(freshMenuItem, R.layout.activity_ble_progressbar);
    }
    
    private void finishScanUI()
    {
        MenuItemCompat.setActionView(freshMenuItem, null);
    }
    
    private void updateDeviceList()
    {
        LogFileUtil.v(TAG, "更新数据");
        deviceList.clear();
        if (mLeDevices.size() == 0)
        {
            deviceList.add("暂时没有搜索到BLE设备");
            lvDevice.setEnabled(false);
        }
        else
        {
            lvDevice.setEnabled(true);
            Iterator<BluetoothDevice> iterator = mLeDevices.iterator();
            while (iterator.hasNext())
            {
                BluetoothDevice device = iterator.next();
                deviceList.add(device.getName() + " " + device.getAddress());
            }
        }
        deviceApdater.notifyDataSetChanged();
    }
    
    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            // 经过预定扫描期后停止扫描
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mIsScanning == false)
                    {
                        return; //已经终止扫描
                    }
                    mIsScanning = false;
                    finishScanUI();
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Log.i(TAG, "扫描结束");
                    mHandler.obtainMessage(Macro.HANDLER_SCAN_STOPPED).sendToTarget();
                }
            }, Macro.BLE_SCAN_PERIOD);
            
            mIsScanning = true;
            beginScanUI();
            mBluetoothAdapter.startLeScan(mLeScanCallback);//蓝牙适配器开始进行扫描设备，回调mLeScanCallBack变量
        }
        else
        {
            mIsScanning = false;
            finishScanUI();
            Log.i(TAG, "终止扫描");
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mLeDevices.add(device);
                    updateDeviceList();
                    
                    Toast.makeText(getApplicationContext(), "扫描到新BLE设备 " + device.getName(), Toast.LENGTH_SHORT).show();
                    String out_info = device.getAddress() + " " + device.getBondState() + " " + device.getName();
                    tvHello.setText(tvHello.getText() + "\n" + out_info);
                    
                }
            });
        }
    };
    
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = Macro.ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                
                mHandler.obtainMessage(Macro.HANDLER_CONNECT_SUCCESS).sendToTarget();
                Log.i(TAG, "Connected to GATT server.");
                mBluetoothGatt.discoverServices(); //先去发现服务
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {//当设备无法连接
                intentAction = Macro.ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                mHandler.obtainMessage(Macro.HANDLER_CONNECT_FAILED).sendToTarget();
                Log.i(TAG, "Disconnected from GATT server.");
                
            }
        }
        
        /**
         * 发现新服务端
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                mHandler.obtainMessage(Macro.HANDLER_SERVICE_DISCOVERED).sendToTarget();
            }
            else
            {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
        
        @Override
        // 读写特性
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.w("TAG", "READ!!");
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);  
            }
        }
        
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            // TODO Auto-generated method stub
            super.onCharacteristicChanged(gatt, characteristic);
            
            String uuid = characteristic.getUuid().toString();
            
            if (uuid.equals(Macro.UUID_BLE_DAT))
            {
                String data = blueRead(characteristic.getValue());
                String[] datas = data.split("\n");
                
                for (int i = 0; i < datas.length; ++i)
                {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ms");
                    String s = sdf.format(date);
                    Log.w(TAG, "改变是 " + data);
                    broadcastUpdate("#" + "BLE" + "#" + s + "#" + datas[i]);
                }
            }
        }
    };
    
    void broadcastUpdate(String str_intent)
    {
        Intent intent = new Intent(Macro.BROADCAST_ADDRESS);
        intent.putExtra("msg", str_intent);
        sendBroadcast(intent);
    }
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
            // TODO Auto-generated method stub
            final String action = arg1.getStringExtra("msg");
            Log.w(TAG, "广播来了 " + action);
            tvHello.setText(action);
        }
        
    };
    
    String ListServices()
    {
        String ser_str = "";
        if (mBluetoothGatt != null)
        {
            List<BluetoothGattService> mLeServices = mBluetoothGatt.getServices();
            for (int i = 0; i < mLeServices.size(); i++)
            {
                Log.w(TAG, "找到的服务为: " + i + mLeServices.get(i).getUuid());
                ser_str += "\n" + mLeServices.get(i).getUuid();
            }
            return ser_str;
        }
        else
        {
            return "Error Gatt";
        }
    }
    
    String ListCharacters(String uuid)
    {
        String ser_str = "";
        mGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
        List<BluetoothGattCharacteristic> tmp_listcha;
        if (mGattService == null)
        {
            Toast.makeText(getApplicationContext(), "服务获取失败", Toast.LENGTH_SHORT).show();
            return "Error Service";
        }
        else
        {
            Toast.makeText(getApplicationContext(), "服务获取成功", Toast.LENGTH_SHORT).show();
            
            tmp_listcha = mGattService.getCharacteristics();
            ser_str = "";
            for (int i = 0; i < tmp_listcha.size(); i++)
            {
                Log.w(TAG, "找到的特征为: " + i + tmp_listcha.get(i).getUuid() + " " + tmp_listcha.get(i).getValue());
                ser_str += "\n" + tmp_listcha.get(i).getUuid() + " " + tmp_listcha.get(i).getValue();
            }
            
            return ser_str;
        }
    }
    
    boolean EnableConfig(String uuid)
    {
        //此处开始按照协议对内容进行获取
        byte[] val = new byte[1];
        val[0] = 1;
        
        if (mGattService == null)
            return false;
        
        BluetoothGattCharacteristic charac = mGattService.getCharacteristic(UUID.fromString(uuid));
        
        if (charac == null)
            return false;
        
        charac.setValue(val); //conf
        mBluetoothGatt.writeCharacteristic(charac);
        
        //		try {
        //			Thread.sleep(200);
        //		} catch (InterruptedException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		}
        
        return true;
        
    }
    
    /*
     * 读取数据前的配置工作，温度和压力传感器的读取都要执行这个方法
     */
    boolean EnableData(String uuid)
    {
        if (mGattService == null)
        {
            return false;
        }
        
        BluetoothGattCharacteristic charac = mGattService.getCharacteristic(UUID.fromString(uuid));
        
        if (charac == null)
        {
            return false;
        }
        
        boolean noti = mBluetoothGatt.setCharacteristicNotification(charac, true);
        Log.w(TAG, "noti " + noti);
        BluetoothGattDescriptor clientConfig = charac.getDescriptor(UUID.fromString(Macro.UUID_CLIENT_CONFIG));
        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(clientConfig);
        
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.w("TAG", "success" + Macro.INTENT_BLEACTIVITY_TESTSHOW);
        
        if (requestCode == Macro.INTENT_BLEACTIVITY_TESTSHOW)
        {
            closeBle();
            if (Macro.SETTING_EXIT_DIRECTLY == true)
            { //上一个activity要求直接退出。
                finish();
            }
        }
        
    }
    
    public String blueRead(byte[] value)
    {
        String tmp = "";
        try
        {
            tmp = new String(value, "GB2312");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        Log.w("tmp", "tmp = " + tmp);
        return tmp;
    }
}
