package com.tactileshow.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.text.TextUtils;
import android.text.format.Time;
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

import com.tactileshow.bluetooth.BluetoothHelper;
import com.tactileshow.util.DataFile;
import com.tactileshow.util.Point3D;
import com.tactileshow.util.StaticValue;
import com.tactileshow.util.macro;
import com.yline.application.SDKManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * 主界面的Activity。
 * 整个连接执行过程为：onMenuItemSelected里的macro.MENU_ITEMID_FRESH情况（当点击刷新时，进行设备扫描），scanLeDevice（真正开始扫描）
 * lv_device.setOnItemClickListener（当点击设备时，进行连接）
 */
public class MainActivity extends Activity
{
	private BluetoothHelper mBluetoothHelper;
	
	private boolean mIsScanning;
	
	private Handler mHandler = new Handler();
	
	private MyHandler myHandler;
	
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
	
	private static final int STATE_CONNECTING = 1;  //设备正在连接状态
	
	private static final int STATE_CONNECTED = 2;   //设备连接完毕
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_ble);
		
		Log.e("wshg", "Starting...");
		ActionBar actionBar = getActionBar();
		actionBar.show();
		
		mBluetoothHelper = new BluetoothHelper(this);
		mBluetoothHelper.enableBluetoothForResult(this);
		
		initView();
		
		// Problem
		SDKManager.toast("蓝牙已开启");
		
		myHandler = new MyHandler(); //用于扫描时间设定
		
		StaticValue.data_file = new DataFile();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(macro.BROADCAST_ADDRESS);
		registerReceiver(mGattUpdateReceiver, filter);
		
		tv_hello.setText("请按刷新开始搜索");
	}
	
	private void initView()
	{
		tv_hello = (TextView) findViewById(R.id.layout_ble_hello);
		lv_device = (ListView) findViewById(R.id.lv_ble_device);
		
		lvaa_device = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, mLeDevices_lvdata);
		updateDeviceList();
		
		lv_device.setAdapter(lvaa_device);
		lv_device.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				String deviceName = mBluetoothHelper.connectGatt(MainActivity.this, mGattCallback, i);
				if (!TextUtils.isEmpty(deviceName))
				{
					SDKManager.toast("连接" + deviceName);
					
					scanLeDevice(false);
					showConnectDialog();
				}
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothHelper.closeBluetooth();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		me_globle = menu;
		mi_debug = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_DEBUG, 0, "测试");
		mi_fresh = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_FRESH, 1, "搜索");
		mi_exit = menu.add(macro.MENU_GROUPID_BLE, macro.MENU_ITEMID_EXIT, 2, "退出");
		mi_fresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi_exit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
		//return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		if (item.getItemId() == macro.MENU_ITEMID_FRESH)
		{
			mBluetoothHelper.closeBluetooth();
			mBluetoothHelper.clearDevices();
			
			updateDeviceList();
			tv_hello.setText("开始搜索");
			scanLeDevice(true);
		}
		else if (item.getItemId() == macro.MENU_ITEMID_EXIT)
		{
			Log.w(TAG, "滚");
			finish();
		}
		else if (item.getItemId() == macro.MENU_ITEMID_DEBUG)
		{
			Log.w(TAG, "测试模式");
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MainTabActivity.class);
			intent.putExtra("str", "come from first activity TEST");
			startActivityForResult(intent, macro.INTENT_BLEACTIVITY_TESTSHOW);
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void showConnectDialog()
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.activity_ble_connect, null);
		
		builder_dl_connect = new AlertDialog.Builder(this);
		builder_dl_connect.setTitle("连接状态");
		builder_dl_connect.setView(view);
		builder_dl_connect.setNegativeButton("取消连接",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						mBluetoothHelper.closeBluetooth();
						dialog.dismiss();
					}
				});
		tv_connect_info = (TextView) view.findViewById(R.id.tv_ble_connect_info);
		if (tv_connect_info == null)
			Log.w(TAG, "NULL");
		tv_connect_info.setText("正在连接中");
		
		dl_connect = builder_dl_connect.show();
	}
	
	private void updateDeviceList()
	{
		Log.w(TAG, "更新数据");
		mLeDevices_lvdata.clear();
		if (mBluetoothHelper.getDeviceSize() == 0)
		{
			mLeDevices_lvdata.add("暂时没有搜索到BLE设备");
			lv_device.setEnabled(false);
		}
		else
		{
			lv_device.setEnabled(true);
			Iterator<BluetoothDevice> it = mBluetoothHelper.getDeviceIterator();
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
					Log.i(TAG, "run: mIsScanning = " + mIsScanning);
					if (mIsScanning == false)
					{
						return; //已经终止扫描
					}
					mIsScanning = false;
					MenuItemCompat.setActionView(mi_fresh, null);
					
					mBluetoothHelper.stopAdapterScan(mLeScanCallback);
					Log.i(TAG, "扫描结束");
					sendHandleMsg("Scan", macro.HANDLER_SCAN_STOPPED);
				}
			}, macro.BLE_SCAN_PERIOD);
			
			mIsScanning = true;
			Log.i(TAG, "setActionView: mIsScanning = " + mIsScanning);
			MenuItemCompat.setActionView(mi_fresh, R.layout.activity_ble_progressbar);
			
			mBluetoothHelper.startAdapterScan(mLeScanCallback);
		}
		else
		{
			mIsScanning = false;
			MenuItemCompat.setActionView(mi_fresh, null);
			
			Log.i(TAG, "终止扫描");
			mBluetoothHelper.stopAdapterScan(mLeScanCallback);
		}
	}
	
	private void sendHandleMsg(String tag, int content)
	{
		Message msg = new Message();
		msg.what = content;
		myHandler.sendMessage(msg);
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback()
			{
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							boolean isContain = mBluetoothHelper.addDevices(device);
							if (isContain) // 去重效果，否则界面卡死
							{
								updateDeviceList();
								
								SDKManager.toast("扫描到新BLE设备 " + device.getName());
								String out_info = device.getAddress() + " " + device.getBondState() + " " + device.getName();
								tv_hello.setText(tv_hello.getText() + "\n" + out_info);
							}
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
				intentAction = macro.ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				
				sendHandleMsg("Device", macro.HANDLER_CONNECT_SUCCESS);
				Log.i(TAG, "Connected to GATT server.");
				mBluetoothHelper.discoverServices();
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED)
			{//当设备无法连接
				intentAction = macro.ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				sendHandleMsg("Device", macro.HANDLER_CONNECT_FAILED);
				Log.i(TAG, "Disconnected from GATT server.");
			}
		}
		
		@Override
		// 发现新服务端
		public void onServicesDiscovered(BluetoothGatt gatt, int status)
		{
			if (status == BluetoothGatt.GATT_SUCCESS)
			{
				sendHandleMsg("service", macro.HANDLER_SERVICE_DISCOVERED);
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
				//updateBroadcast(ACTION_DATA_AVAILABLE, characteristic);
			}
		}
		
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
		{
			super.onCharacteristicChanged(gatt, characteristic);
			Log.w(TAG, "改变");
			Time t = new Time();
			t.setToNow();
			String str_time = t.format2445();
			
			String uuid = characteristic.getUuid().toString();
			
			if (uuid.equals(macro.UUID_HUM_DAT))
			{
				Point3D p3d_hum = convertHum(characteristic.getValue());
				//Log.w(TAG, "改变是 " + p3d.x + " " + p3d.y + " " + p3d.z);
				updateBroadcast("#" + "PRESS" + "#" + str_time + "#" + p3d_hum.x);
				
				Point3D p3d_temp = convertTemp(characteristic.getValue());
				//Log.w(TAG, "改变是 " + p3d.x + " " + p3d.y + " " + p3d.z);
				
				updateBroadcast("#" + "TEMP" + "#" + str_time + "#" + p3d_temp.x);
			}
			/*
			if(uuid.equals(macro.UUID_MAG_DAT))
			{
				Point3D p3d = convertMag(characteristic.getValue());
				//Log.w(TAG, "改变是 " + p3d.x + " " + p3d.y + " " + p3d.z);
				
				updateBroadcast("#" + "TEMP" + "#" + str_time + "#" + p3d.x);
			}
			*/
		}
	};
	
	private void updateBroadcast(String str_intent)
	{
		Intent intent = new Intent(macro.BROADCAST_ADDRESS);
		intent.putExtra("msg", str_intent);
		sendBroadcast(intent);
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getStringExtra("msg");
			Log.i(TAG, "广播来了 " + action);
			tv_hello.setText(action);
		}
	};
	
	private class MyHandler extends Handler
	{
		public MyHandler() {}
		
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
			
			if (msg.what == macro.HANDLER_SCAN_STOPPED)
			{
				tv_hello.setText(tv_hello.getText() + "\n" + "扫描结束");
			}
			else if (msg.what == macro.HANDLER_CONNECT_SUCCESS)
			{
				//Toast.makeText(getApplicationContext(), "连接已经建立", Toast.LENGTH_SHORT).show();
				tv_connect_info.setText("连接成功，开始寻找服务");
			}
			else if (msg.what == macro.HANDLER_CONNECT_FAILED)
			{
				//Toast.makeText(getApplicationContext(), "连接建立失败", Toast.LENGTH_SHORT).show();
				tv_connect_info.setText("连接失败，请返回重连");
			}
			else if (msg.what == macro.HANDLER_SERVICE_DISCOVERED)
			{
				//Toast.makeText(getApplicationContext(), "服务发现完毕", Toast.LENGTH_SHORT).show();
				tv_connect_info.setText("成功发现服务，开始启动服务");
				boolean isHasValidData = false;
				
				mBluetoothHelper.logServiceInfo();
				
				logCharacterList(macro.UUID_MAG_SER);
				
				if (enableConfig(macro.UUID_MAG_CON) && EnableData(macro.UUID_MAG_DAT))
				{
					//Toast.makeText(getApplicationContext(), "开始传输磁场数据", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("温度数据激活成功");
					isHasValidData = true;
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "磁场特征写入失败", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("温度数据激活失败");
				}
				
				logCharacterList(macro.UUID_HUM_SER);
				
				if (enableConfig(macro.UUID_HUM_CON) && EnableData(macro.UUID_HUM_DAT))
				{
					//Toast.makeText(getApplicationContext(), "开始传输湿度数据", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("湿度数据激活成功");
					isHasValidData = true;
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "湿度特征写入失败", Toast.LENGTH_SHORT).show();
					tv_connect_info.setText("湿度数据激活失败");
				}
				
				if (isHasValidData == true)
				{
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MainTabActivity.class);
					intent.putExtra("str", "come from first activity");
					startActivityForResult(intent, macro.INTENT_BLEACTIVITY_TESTSHOW);
					dl_connect.dismiss();
				}
			}
		}
	}
	
	private void logCharacterList(String uuid)
	{
		String ser_str = mBluetoothHelper.logService(uuid);
		if (TextUtils.isEmpty(ser_str))
		{
			SDKManager.toast("服务获取失败");
		}
		else
		{
			SDKManager.toast("服务获取成功");
		}
	}
	
	
	/*
	 * 读取数据前的配置工作，温度和湿度传感器的读取都要执行这个方法
	 */
	boolean EnableData(String uuid)
	{
		mBluetoothHelper.enableData(uuid);
		
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	private boolean enableConfig(String uuid)
	{
		// 此处开始按照协议对内容进行获取
		byte[] val = new byte[1];
		val[0] = 1;
		
		mBluetoothHelper.enableConfig(uuid, val);
		
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.w("TAG", "sssssssssssss" + macro.INTENT_BLEACTIVITY_TESTSHOW);
		
		if (requestCode == macro.INTENT_BLEACTIVITY_TESTSHOW)
		{
			mBluetoothHelper.closeBluetooth();
			if (macro.SETTING_EXIT_DIRECTLY == true) // 上一个activity要求直接退出。
			{
				finish();
			}
		}
	}
	
	public Point3D convertHum(final byte[] value)
	{
		int a = shortUnsignedAtOffset(value, 2);   // 湿度
		
		// bits [1..0] are status bits and need to be cleared according
		// to the user guide, but the iOS code doesn't bother. It should
		// have minimal impact.
		a = a - (a % 4);
		
		return new Point3D((-6f) + 125f * (a / 65535f), 0, 0);    //湿度
	}
	
	public Point3D convertTemp(final byte[] value)
	{
		int a = shortUnsignedAtOffset(value, 0);     // 温度
		return new Point3D(-46.85f + 175.72f * (a / 65535f), 0, 0);    // 温度
	}
	
	public Point3D convertMag(final byte[] value)
	{    // 压力，暂时没用到该数据
		Point3D mcal = MagnetometerCalibrationCoefficients.INSTANCE.val;
		// Multiply x and y with -1 so that the values correspond with the image in the app
		float x = shortSignedAtOffset(value, 0) * (2000f / 65536f) * -1;
		float y = shortSignedAtOffset(value, 2) * (2000f / 65536f) * -1;
		float z = shortSignedAtOffset(value, 4) * (2000f / 65536f);
		
		return new Point3D(x - mcal.x, y - mcal.y, z - mcal.z);
	}
	
	public enum MagnetometerCalibrationCoefficients
	{
		INSTANCE;
		
		Point3D val = new Point3D(0.0, 0.0, 0.0);
	}
	
	private static Integer shortUnsignedAtOffset(byte[] c, int offset)
	{
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer upperByte = (int) c[offset + 1] & 0xFF; // // Interpret MSB as signed
		return (upperByte << 8) + lowerByte;
	}
	
	private static Integer shortSignedAtOffset(byte[] c, int offset)
	{
		Integer lowerByte = (int) c[offset] & 0xFF;
		Integer upperByte = (int) c[offset + 1]; // // Interpret MSB as signed
		return (upperByte << 8) + lowerByte;
	}
}
