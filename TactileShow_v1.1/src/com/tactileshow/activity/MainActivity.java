package com.tactileshow.activity;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.tactileshow.base.BaseActivity;
import com.tactileshow.main.R;
import com.tactileshow.util.DataFile;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.Macro;

import android.app.ActionBar;
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
import android.os.Looper;
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
    BluetoothManager mBluetoothManager;
    
    BluetoothAdapter mBluetoothAdapter;
    
    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
    
    private List<BluetoothGattService> mLeServices;
    
    private boolean mIsScanning;
    
    private Handler mHandler = new Handler();
    
    private MyHandler myHandler;
    
    private BluetoothGatt mBluetoothGatt;
    
    private BluetoothGattService mGattService;
    
    TextView tv_hello;
    
    ListView lv_device;
    
    TextView tv_connect_info;
    
    Menu me_globle;
    
    MenuItem mi_fresh;
    
    MenuItem mi_exit;
    
    MenuItem mi_debug;
    
    AlertDialog.Builder builder_dl_connect;
    
    AlertDialog dl_connect;
    
    ArrayAdapter<String> lvaa_device;
    
    List<String> mLeDevices_lvdata = new ArrayList<String>();
    
    private final static String TAG = "测试TAG";
    
    private int mConnectionState = STATE_DISCONNECTED;
    
    private static final int STATE_DISCONNECTED = 0; //设备无法连接
    
    private static final int STATE_CONNECTING = 1; //设备正在连接状态
    
    private static final int STATE_CONNECTED = 2; //设备连接完毕
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_ble);
        Log.i(TAG, "MainActivity Starting...");
        ActionBar actionBar = getActionBar();
        actionBar.show();
        
        mBluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Macro.INTENT_REQUEST_ENABLE_BT);
        }
        
        Toast.makeText(getApplicationContext(), "蓝牙已开启", Toast.LENGTH_SHORT).show();
        
        tv_hello = (TextView)findViewById(R.id.layout_ble_hello);
        
        lv_device = (ListView)findViewById(R.id.lv_ble_device);
        lvaa_device = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mLeDevices_lvdata);
        UpdateDeviceList();
        
        lv_device.setAdapter(lvaa_device);
        lv_device.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                // TODO Auto-generated method stub
                BluetoothDevice tmpBleDevice = mLeDevices.get(arg2);
                Toast.makeText(getApplicationContext(), "连接" + tmpBleDevice.getName(), Toast.LENGTH_SHORT).show();
                scanLeDevice(false);
                mBluetoothGatt = tmpBleDevice.connectGatt(MainActivity.this, false, mGattCallback);
                ShowConnectDialog();
            }
        });
        
        myHandler = new MyHandler(); //用于扫描时间设定
        
        StaticValue.data_file = new DataFile();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(Macro.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);
        
        tv_hello.setText("请按刷新开始搜索");
        
    }
    
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        unregisterReceiver(mGattUpdateReceiver);
        closeBle();
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO Auto-generated method stub
        me_globle = menu;
        mi_debug = menu.add(Macro.MENU_GROUPID_BLE, Macro.MENU_ITEMID_DEBUG, 0, "测试");
        mi_fresh = menu.add(Macro.MENU_GROUPID_BLE, Macro.MENU_ITEMID_FRESH, 1, "搜索");
        mi_exit = menu.add(Macro.MENU_GROUPID_BLE, Macro.MENU_ITEMID_EXIT, 2, "退出");
        mi_fresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        mi_exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        return true;
        //return super.onCreateOptionsMenu(menu);	
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        // TODO Auto-generated method stub
        if (item.getItemId() == Macro.MENU_ITEMID_FRESH)
        {
            closeBle();
            mLeDevices.clear();
            UpdateDeviceList();
            tv_hello.setText("开始搜索");
            scanLeDevice(true);
        }
        else if (item.getItemId() == Macro.MENU_ITEMID_EXIT)
        {
            Log.w(TAG, "退出");
            finish();
        }
        else if (item.getItemId() == Macro.MENU_ITEMID_DEBUG)
        {
            Log.w(TAG, "测试模式");
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainTabActivity.class);
            intent.putExtra("str", "come from first activity TEST");
            startActivityForResult(intent, Macro.INTENT_BLEACTIVITY_TESTSHOW);
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    void ShowConnectDialog()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.activity_ble_connect, null);
        
        builder_dl_connect = new AlertDialog.Builder(this);
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
            Log.w(TAG, "NULL");
        tv_connect_info.setText("正在连接中");
        
        dl_connect = builder_dl_connect.show();
    }
    
    public void closeBle()
    {
        if (mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    
    void beginScanUI()
    {
        MenuItemCompat.setActionView(mi_fresh, R.layout.activity_ble_progressbar);
    }
    
    void endScanUI()
    {
        MenuItemCompat.setActionView(mi_fresh, null);
    }
    
    void UpdateDeviceList()
    {
        Log.w(TAG, "更新数据");
        mLeDevices_lvdata.clear();
        if (mLeDevices.size() == 0)
        {
            mLeDevices_lvdata.add("暂时没有搜索到BLE设备");
            lv_device.setEnabled(false);
        }
        else
        {
            lv_device.setEnabled(true);
            Iterator<BluetoothDevice> it = mLeDevices.iterator();
            while (it.hasNext())
            {
                BluetoothDevice bd_it = it.next();
                mLeDevices_lvdata.add(bd_it.getName() + " " + bd_it.getAddress());
            }
        }
        lvaa_device.notifyDataSetChanged();
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
                        return; //已经终止扫描
                    mIsScanning = false;
                    endScanUI();
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Log.i(TAG, "扫描结束");
                    SendHandleMsg("Scan", Macro.HANDLER_SCAN_STOPPED);
                }
            }, Macro.BLE_SCAN_PERIOD);
            
            mIsScanning = true;
            beginScanUI();
            mBluetoothAdapter.startLeScan(mLeScanCallback);//蓝牙适配器开始进行扫描设备，回调mLeScanCallBack变量
        }
        else
        {
            mIsScanning = false;
            endScanUI();
            Log.i(TAG, "终止扫描");
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    
    void SendHandleMsg(String tag, int content)
    {
        Message msg = new Message();
        msg.what = content;
        myHandler.sendMessage(msg);
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
                    UpdateDeviceList();
                    
                    Toast.makeText(getApplicationContext(), "扫描到新BLE设备 " + device.getName(), Toast.LENGTH_SHORT).show();
                    String out_info = device.getAddress() + " " + device.getBondState() + " " + device.getName();
                    tv_hello.setText(tv_hello.getText() + "\n" + out_info);
                    
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
                
                SendHandleMsg("Device", Macro.HANDLER_CONNECT_SUCCESS);
                Log.i(TAG, "Connected to GATT server.");
                mBluetoothGatt.discoverServices(); //先去发现服务
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {//当设备无法连接
                intentAction = Macro.ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                SendHandleMsg("Device", Macro.HANDLER_CONNECT_FAILED);
                Log.i(TAG, "Disconnected from GATT server.");
                
            }
        }
        
        @Override
        // 发现新服务端
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                SendHandleMsg("service", Macro.HANDLER_SERVICE_DISCOVERED);
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
            //			try {
            //				Thread.sleep(50);
            //			} catch (InterruptedException e) {
            //				// TODO Auto-generated catch block
            //				e.printStackTrace();
            //			}
            
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
            tv_hello.setText(action);
        }
        
    };
    
    String ListServices()
    {
        String ser_str = "";
        if (mBluetoothGatt != null)
        {
            mLeServices = mBluetoothGatt.getServices();
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
            return false;
        
        BluetoothGattCharacteristic charac = mGattService.getCharacteristic(UUID.fromString(uuid));
        
        if (charac == null)
            return false;
        
        boolean noti = mBluetoothGatt.setCharacteristicNotification(charac, true);
        Log.w(TAG, "noti " + noti);
        BluetoothGattDescriptor clientConfig = charac.getDescriptor(UUID.fromString(Macro.UUID_CLIENT_CONFIG));
        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(clientConfig);
        
        //		try {
        //			Thread.sleep(200);//200
        //		} catch (InterruptedException e) {
        //			// TODO Auto-generated catch block
        //			e.printStackTrace();
        //		}
        
        return true;
    }
    
    class MyHandler extends Handler
    {
        public MyHandler()
        {
        }
        
        public MyHandler(Looper L)
        {
            super(L);
        }
        
        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            // 此处可以更新UI
            
            if (msg.what == Macro.HANDLER_SCAN_STOPPED)
            {
                tv_hello.setText(tv_hello.getText() + "\n" + "扫描结束");
            }
            else if (msg.what == Macro.HANDLER_CONNECT_SUCCESS)
            {
                //Toast.makeText(getApplicationContext(), "连接已经建立", Toast.LENGTH_SHORT).show();
                tv_connect_info.setText("连接成功，开始寻找服务");
            }
            else if (msg.what == Macro.HANDLER_CONNECT_FAILED)
            {
                //Toast.makeText(getApplicationContext(), "连接建立失败", Toast.LENGTH_SHORT).show();
                tv_connect_info.setText("连接失败，请返回重连");
            }
            else if (msg.what == Macro.HANDLER_SERVICE_DISCOVERED)
            {
                //Toast.makeText(getApplicationContext(), "服务发现完毕", Toast.LENGTH_SHORT).show();
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
            
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.w("TAG", "success" + Macro.INTENT_BLEACTIVITY_TESTSHOW);
        
        if (requestCode == Macro.INTENT_BLEACTIVITY_TESTSHOW)
        {
            closeBle();
            if (Macro.SETTING_EXIT_DIRECTLY == true) //上一个activity要求直接退出。
                finish();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.w("tmp", "tmp = " + tmp);
        return tmp;
    }
}
