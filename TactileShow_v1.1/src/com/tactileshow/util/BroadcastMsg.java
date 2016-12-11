package com.tactileshow.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.Time;
import android.util.Log;

/*
 * �Խ��յ��Ĺ㲥���ݣ���ʽΪ��#Sensor#Time#data#�����н�����
 */
public class BroadcastMsg {
	private String sensor;
	private Date time;
	private String data;
	public BroadcastMsg(String msg){
		time = new Date();
		parseString(msg);
	}
	
	public String getSensor(){
		return sensor;
	}
	
	public Date getTime(){
		return time;
	}
	
	public String getData(){
		return data;
	}
	
	private void parseString(String msg){
		if(msg.startsWith("#")){
			String[] pars = msg.substring(1).split("#");
			if(pars.length >= 3){
				sensor = pars[0];
				//time = new Date(Long.parseLong(pars[1]));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ms");
				try {
					time = sdf.parse(pars[0]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				data = pars[2];
			}
		}
	}
}
