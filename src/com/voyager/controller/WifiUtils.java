package com.voyager.controller;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiUtils {
	private WifiManager wifiManager;
	private WifiInfo connInfo;
	private DhcpInfo dhcpInfo;

	public WifiUtils(Context context) {
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		connInfo = wifiManager.getConnectionInfo();
		dhcpInfo = wifiManager.getDhcpInfo();
	}

	/**
	 * 获取本机IP地址
	 * @return
	 */
	public String getLocalIp() {
		return intToString(dhcpInfo.ipAddress);
	}
	
	/**
	 * 获取服务器IP地址
	 * @return
	 */
	public String getServerIp() {
		return intToString(dhcpInfo.serverAddress);
	}

	/**
	 * 将整型IP地址转换为String
	 * @param data
	 * @return
	 */
	private String intToString(int value) {
		String strValue="";
		byte[] ary = intToByteArray(value);
		for(int i=ary.length-1;i>=0;i--){
			strValue+=(ary[i]&0xFF);
			if(i>0){
				strValue+=".";
			}
		}
		return strValue;
	}
	
	public byte[] intToByteArray(int value){
		byte[] b=new byte[4];
		for(int i=0;i<4;i++){
			int offset = (b.length-1-i)*8;
			b[i]=(byte) ((value>>>offset)&0xFF);
		}
		return b;
	}
}
