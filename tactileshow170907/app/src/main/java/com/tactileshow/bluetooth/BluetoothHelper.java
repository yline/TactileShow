package com.tactileshow.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tactileshow.util.macro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BluetoothHelper
{
	private static final String TAG = "xxx-BluetoothHelper";
	
	private BluetoothManager mBluetoothManager;
	
	private BluetoothAdapter mBluetoothAdapter;
	
	private BluetoothGatt mBluetoothGatt;
	
	private BluetoothGattService mGattService;
	
	private List<BluetoothDevice> mLeDevices;
	
	private List<BluetoothGattService> mLeServices;
	
	public BluetoothHelper(Context context)
	{
		mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		mLeDevices = new ArrayList<>();
	}
	
	public void enableBluetoothForResult(Activity activity)
	{
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, macro.INTENT_REQUEST_ENABLE_BT);
		}
	}
	
	public void stopAdapterScan(BluetoothAdapter.LeScanCallback callback)
	{
		mBluetoothAdapter.stopLeScan(callback);
	}
	
	/**
	 * 蓝牙适配器开始进行扫描设备，回调mLeScanCallBack变量
	 *
	 * @param callback 回调
	 */
	public void startAdapterScan(BluetoothAdapter.LeScanCallback callback)
	{
		mBluetoothAdapter.startLeScan(callback);
	}
	
	public boolean addDevices(BluetoothDevice device)
	{
		if (!mLeDevices.contains(device))
		{
			mLeDevices.add(device);
			return true;
		}
		return false;
	}
	
	public int getDeviceSize()
	{
		return mLeDevices.size();
	}
	
	public Iterator<BluetoothDevice> getDeviceIterator()
	{
		return mLeDevices.iterator();
	}
	
	public String connectGatt(Context context, BluetoothGattCallback callback, int position)
	{
		BluetoothDevice tempBleDevice = mLeDevices.get(position);
		if (null != tempBleDevice)
		{
			mBluetoothGatt = tempBleDevice.connectGatt(context, false, callback);
			return tempBleDevice.getName();
		}
		return null;
	}
	
	/* 发现服务 */
	public void discoverServices()
	{
		if (null != mBluetoothGatt)
		{
			mBluetoothGatt.discoverServices();
		}
	}
	
	public void enableConfig(String uuid, byte[] value)
	{
		if (null != mGattService)
		{
			BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(uuid));
			if (null != characteristic)
			{
				characteristic.setValue(value);
				if (null != mBluetoothGatt)
				{
					mBluetoothGatt.writeCharacteristic(characteristic);
				}
			}
		}
	}
	
	public boolean enableData(String uuid)
	{
		if (null != mGattService)
		{
			BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(uuid));
			if (null != characteristic && null != mBluetoothGatt)
			{
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
	
	public void clearDevices()
	{
		mLeDevices.clear();
	}
	
	public void closeBluetooth()
	{
		if (null == mBluetoothGatt)
		{
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}
	
	public String logServiceInfo()
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (null != mBluetoothGatt)
		{
			mLeServices = mBluetoothGatt.getServices();
			UUID serviceUUID;
			for (int i = 0; i < mLeServices.size(); i++)
			{
				serviceUUID = mLeServices.get(i).getUuid();
				Log.i(TAG, "找到的服务为: " + i + serviceUUID);
				stringBuilder.append('\n');
				stringBuilder.append(serviceUUID);
			}
		}
		else
		{
			Log.e(TAG, "getServiceInfo: mBluetoothGatt is null");
		}
		return stringBuilder.toString();
	}
	
	public String logService(String uuid)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (null != mBluetoothGatt)
		{
			mGattService = mBluetoothGatt.getService(UUID.fromString(uuid));
			List<BluetoothGattCharacteristic> tempCharList;
			if (null != mGattService)
			{
				tempCharList = mGattService.getCharacteristics();
				UUID charUuid;
				byte[] valueByte;
				for (int i = 0; i < tempCharList.size(); i++)
				{
					charUuid = tempCharList.get(i).getUuid();
					valueByte = tempCharList.get(i).getValue();
					
					Log.i(TAG, "找到的特征为: " + i + charUuid + " " + valueByte);
					stringBuilder.append('\n');
					stringBuilder.append(charUuid);
					stringBuilder.append(" ");
					stringBuilder.append(valueByte);
				}
			}
			else
			{
				Log.e(TAG, "getServiceInfo: mGattService is null");
			}
		}
		else
		{
			Log.e(TAG, "getServiceInfo: mBluetoothGatt is null");
		}
		return stringBuilder.toString();
	}
}
