package com.tactileshow.activity;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tactileshow.R;
import com.tactileshow.application.IApplication;
import com.tactileshow.base.BaseAppCompatActivity;
import com.tactileshow.log.LogFileUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/*
 * 主界面的Activity。实现蓝牙功能
 * 整个连接执行过程为：onMenuItemSelected里的macro.MENU_ITEMID_FRESH情况（当点击刷新时，进行设备扫描），scanLeDevice（真正开始扫描）
 * lv_device.setOnItemClickListener（当点击设备时，进行连接）
 */
public class MainActivity extends BaseAppCompatActivity
{
	// Menu
	private static final int MENU_GROUPID_BLE = 21000;

	private static final int MENU_ITEMID_FRESH = 21001;

	private static final int MENU_ITEMID_EXIT = 21002;

	private static final int MENU_ITEMID_DEBUG = 21003;

	// Handler
	private final static int HANDLER_SERVICE_DISCOVERED = 20000;

	private final static int HANDLER_SCAN_STOPPED = 20001;

	private final static int HANDLER_CONNECT_SUCCESS = 20003;

	private final static int HANDLER_CONNECT_FAILED = 20004;

	private final static String TAG = "HomePageTAG";

	// blueTooth Constant 低功耗蓝牙(BLE)
	private static long BLE_SCAN_PERIOD = 10 * 1000;

	private static int INTENT_REQUEST_ENABLE_BT = 10001;

	public static String BROADCAST_ADDRESS = "zju.ccnt.ble";

	private static String UUID_BLE_SER = "0000fff0-0000-1000-8000-00805f9b34fb"; //rx服务uuid

	private static String UUID_BLE_DAT = "0000fff6-0000-1000-8000-00805f9b34fb"; //notify

	private static String UUID_BLE_CON = "0000fff1-0000-1000-8000-00805f9b34fb"; //

	//private static String UUID_HUM_SER = "f000aa20-0451-4000-b000-000000000000";//压力

	//private static String UUID_HUM_DAT = "f000aa21-0451-4000-b000-000000000000";

	//private static String UUID_HUM_CON = "f000aa22-0451-4000-b000-000000000000";

	private static String UUID_CLIENT_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

	//private static String UUID_IRT_SER = "f000aa00-0451-4000-b000-000000000000";

	//private static String UUID_IRT_DAT = "f000aa01-0451-4000-b000-000000000000";

	//private static String UUID_IRT_CON = "f000aa02-0451-4000-b000-000000000000"; // 0: disable, 1: enable

	//private static String UUID_MAG_SER = "f000aa30-0451-4000-b000-000000000000";//温度

	//private static String UUID_MAG_DAT = "f000aa31-0451-4000-b000-000000000000";

	//private static String UUID_MAG_CON = "f000aa32-0451-4000-b000-000000000000"; // 0: disable, 1: enable

	//private static String UUID_MAG_PER = "f000aa33-0451-4000-b000-000000000000"; // Period in tens of milliseconds

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

	AlertDialog connectDialog;

	/**
	 * 用于扫描时间设定
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == HANDLER_SCAN_STOPPED)
			{
				tvHello.setText(tvHello.getText() + "\n" + "扫描结束");
			}
			else if (msg.what == HANDLER_CONNECT_SUCCESS)
			{
				// IApplication.toast("连接已经建立");
				tv_connect_info.setText("连接成功，开始寻找服务");
			}
			else if (msg.what == HANDLER_CONNECT_FAILED)
			{
				// IApplication.toast("连接建立失败");
				tv_connect_info.setText("连接失败，请返回重连");
			}
			else if (msg.what == HANDLER_SERVICE_DISCOVERED)
			{
				// IApplication.toast("服务发现完毕");
				tv_connect_info.setText("成功发现服务，开始启动服务");
				boolean isHasValidData = false;
				listServices();

				listCharacters(UUID_BLE_SER);

				if (EnableConfig(UUID_BLE_CON) && EnableData(UUID_BLE_DAT))
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
					MainTabActivity.actionStart(MainActivity.this);
					connectDialog.dismiss();
				}
			}
		}

		;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		// getActionBar().show();
		setContentView(R.layout.activity_main);


		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, INTENT_REQUEST_ENABLE_BT);
		}
		else
		{
			IApplication.toast("蓝牙已开启");
		}

		initView();

		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_ADDRESS);
		registerReceiver(mGattUpdateReceiver, filter);
	}

	/**
	 * 初始化控件，并更新控件数据
	 */
	private void initView()
	{
		tvHello = (TextView) findViewById(R.id.layout_ble_hello);
		tvHello.setText("请按刷新开始搜索");

		lvDevice = (ListView) findViewById(R.id.lv_ble_device);
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
		MenuItem bleMenuItem = menu.add(MENU_GROUPID_BLE, MENU_ITEMID_DEBUG, 0, "测试");
		bleMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				LogFileUtil.v(TAG, "测试模式");

				MainTabActivity.actionStart(MainActivity.this);
				return true;
			}
		});

		freshMenuItem = menu.add(MENU_GROUPID_BLE, MENU_ITEMID_FRESH, 1, "搜索");
		freshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		freshMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				closeBle();
				mLeDevices.clear();
				updateDeviceList();
				tvHello.setText("开始搜索");
				scanLeDevice(true);
				return true;
			}
		});

		MenuItem exitMenuItem = menu.add(MENU_GROUPID_BLE, MENU_ITEMID_EXIT, 2, "退出");
		exitMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		exitMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				LogFileUtil.v(TAG, "退出");
				IApplication.finishActivity();
				return true;
			}
		});

		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		unregisterReceiver(mGattUpdateReceiver);
		closeBle();
	}

	@SuppressLint("InflateParams")
	private void showConnectDialog()
	{
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_main_ble_connect, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("连接状态");
		builder.setView(view);
		builder.setNegativeButton("取消连接", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				closeBle();
				dialog.dismiss();
			}
		});
		tv_connect_info = (TextView) view.findViewById(R.id.tv_ble_connect_info);
		if (tv_connect_info == null)
		{
			LogFileUtil.v(TAG, "NULL");
		}
		tv_connect_info.setText("正在连接中");

		connectDialog = builder.show();
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

	@SuppressWarnings("deprecation")
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
					MenuItemCompat.setActionView(freshMenuItem, null);
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					LogFileUtil.i(TAG, "扫描结束");
					mHandler.obtainMessage(HANDLER_SCAN_STOPPED).sendToTarget();
				}
			}, BLE_SCAN_PERIOD);

			mIsScanning = true;
			MenuItemCompat.setActionView(freshMenuItem, R.layout.menu_main_ble_progressbar);
			mBluetoothAdapter.startLeScan(mLeScanCallback);//蓝牙适配器开始进行扫描设备，回调mLeScanCallBack变量
		}
		else
		{
			mIsScanning = false;
			MenuItemCompat.setActionView(freshMenuItem, null);

			LogFileUtil.i(TAG, "终止扫描");
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

					IApplication.toast("扫描到新BLE设备 " + device.getName());

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
			LogFileUtil.v(TAG, "onConnectionStateChange newState = " + newState);
			if (newState == BluetoothProfile.STATE_CONNECTED)
			{
				mHandler.obtainMessage(HANDLER_CONNECT_SUCCESS).sendToTarget();
				mBluetoothGatt.discoverServices(); //先去发现服务
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED)
			{//当设备无法连接
				mHandler.obtainMessage(HANDLER_CONNECT_FAILED).sendToTarget();
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
				mHandler.obtainMessage(HANDLER_SERVICE_DISCOVERED).sendToTarget();
			}
			else
			{
				LogFileUtil.v(TAG, "onServicesDiscovered received: " + status);
			}
		}

		/**
		 * 读写特性
		 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
		{
			LogFileUtil.v(TAG, "onCharacteristicRead READ");
			if (status == BluetoothGatt.GATT_SUCCESS)
			{
				//broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
		{
			super.onCharacteristicChanged(gatt, characteristic);

			String uuid = characteristic.getUuid().toString();

			if (uuid.equals(UUID_BLE_DAT))
			{
				String data = blueRead(characteristic.getValue());
				String[] datas = data.split("\n");

				LogFileUtil.v(TAG, "str data = " + data);

				for (int i = 0; i < datas.length; ++i)
				{
					String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ms", Locale.getDefault()).format(new Date());
					String content = "#" + "BLE" + "#" + str + "#" + datas[i];

					LogFileUtil.v(TAG, "str content = " + content);
					Intent intent = new Intent(BROADCAST_ADDRESS);
					intent.putExtra("msg", content);
					sendBroadcast(intent);
				}
			}
		}
	};

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getStringExtra("msg");
			LogFileUtil.v(TAG, "广播来了 " + action);
			tvHello.setText(action);
		}
	};

	private String listServices()
	{
		String ser_str = "";
		if (mBluetoothGatt != null)
		{
			List<BluetoothGattService> mLeServices = mBluetoothGatt.getServices();
			for (int i = 0; i < mLeServices.size(); i++)
			{
				LogFileUtil.v(TAG, "找到的服务为: " + i + mLeServices.get(i).getUuid());
				ser_str += "\n" + mLeServices.get(i).getUuid();
			}
			return ser_str;
		}
		else
		{
			return "Error Gatt";
		}
	}

	private String listCharacters(String uuid)
	{
		String ser_str = "";
		mGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
		List<BluetoothGattCharacteristic> tmp_listcha;
		if (mGattService == null)
		{
			IApplication.toast("服务获取失败");
			return "Error Service";
		}
		else
		{
			IApplication.toast("服务获取成功");

			tmp_listcha = mGattService.getCharacteristics();
			ser_str = "";
			for (int i = 0; i < tmp_listcha.size(); i++)
			{
				LogFileUtil.v(TAG, "找到的特征为: " + i + tmp_listcha.get(i).getUuid() + " " + tmp_listcha.get(i).getValue());
				ser_str += "\n" + tmp_listcha.get(i).getUuid() + " " + tmp_listcha.get(i).getValue();
			}

			return ser_str;
		}
	}

	private boolean EnableConfig(String uuid)
	{
		//此处开始按照协议对内容进行获取
		byte[] val = new byte[1];
		val[0] = 1;

		if (mGattService == null)
		{
			return false;
		}

		BluetoothGattCharacteristic charac = mGattService.getCharacteristic(UUID.fromString(uuid));

		if (charac == null)
		{
			return false;
		}

		charac.setValue(val); //conf
		mBluetoothGatt.writeCharacteristic(charac);

		return true;
	}

	/*
	 * 读取数据前的配置工作，温度和压力传感器的读取都要执行这个方法
	 */
	private boolean EnableData(String uuid)
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
		LogFileUtil.v(TAG, "noti " + noti);
		BluetoothGattDescriptor clientConfig = charac.getDescriptor(UUID.fromString(UUID_CLIENT_CONFIG));
		clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(clientConfig);

		return true;
	}

	private String blueRead(byte[] value)
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
		LogFileUtil.v(TAG, "tmp = " + tmp);
		return tmp;
	}
}
