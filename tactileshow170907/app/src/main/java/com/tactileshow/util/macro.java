package com.tactileshow.util;

public class macro
{
	public static int INTENT_REQUEST_ENABLE_BT = 10001;
	
	public static long BLE_SCAN_PERIOD = 10000;
	
	public static int HANDLER_SERVICE_DISCOVERED = 20000;
	
	public static int HANDLER_SCAN_STOPPED = 20001;
	
	public static int HANDLER_UPDATE_LISTVIEW = 20002;
	
	public static int HANDLER_CONNECT_SUCCESS = 20003;
	
	public static int HANDLER_CONNECT_FAILED = 20004;
	
	public static int HANDLER_NEWDATA = 20005;
	
	public static String BROADCAST_ADDRESS = "zju.ccnt.ble";
	
	public static int MENU_GROUPID_BLE = 21000;
	
	public static int MENU_ITEMID_FRESH = 21001;
	
	public static int MENU_ITEMID_EXIT = 21002;
	
	public static int MENU_ITEMID_DEBUG = 21003;
	
	public static String UUID_HUM_SER = "f000aa20-0451-4000-b000-000000000000";//压力
	
	public static String UUID_HUM_DAT = "f000aa21-0451-4000-b000-000000000000";
	
	public static String UUID_HUM_CON = "f000aa22-0451-4000-b000-000000000000";
	
	public static String UUID_CLIENT_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	
	public static String UUID_IRT_SER = "f000aa00-0451-4000-b000-000000000000";
	
	public static String UUID_IRT_DAT = "f000aa01-0451-4000-b000-000000000000";
	
	public static String UUID_IRT_CON = "f000aa02-0451-4000-b000-000000000000"; // 0: disable, 1: enable
	
	public static String UUID_MAG_SER = "f000aa30-0451-4000-b000-000000000000";//温度
	
	public static String UUID_MAG_DAT = "f000aa31-0451-4000-b000-000000000000";
	
	public static String UUID_MAG_CON = "f000aa32-0451-4000-b000-000000000000"; // 0: disable, 1: enable
	
	public static String UUID_MAG_PER = "f000aa33-0451-4000-b000-000000000000"; // Period in tens of milliseconds
	
	
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	
	public static int INTENT_BLEACTIVITY_TESTSHOW = 22000;
	
	public static long[] VIBRATION_MODE = {100, 400, 100, 400};   // 用于手机振动的参数
	
	public static boolean SETTINGS_SOUND = false;
	
	public static boolean SETTINGS_VIBRA = false;
	
	public static boolean SETTINGS_BCAST = false;
	
	public static double SETTING_TEMP_RANGE[] = {10, 20, 40};
	
	public static double SETTING_PRESS_RANGE[] = {30, 50, 80};
	
	public static double SETTING_GERM_RANGE[] = {38, 42};
	
	public static boolean SETTING_EXIT_DIRECTLY = false;
	
	
}
