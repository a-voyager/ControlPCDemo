package com.voyager.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	public static final int PORT = 3000;
	private EditText et_command;
	private Button btn_send;
	private WifiUtils wifiUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_send = (Button) findViewById(R.id.btn_send);
		et_command = (EditText) findViewById(R.id.et_command);
		btn_send.setOnClickListener(this);

		wifiUtils = new WifiUtils(MainActivity.this);
	}

	@Override
	public void onClick(View v) {
		new ScanThread().start();
	}

	private class ScanThread extends Thread {
		@Override
		public void run() {
			String serverIp = wifiUtils.getServerIp();
			int index = serverIp.lastIndexOf(".") + 1; // 要包含'.', 所以加1
			serverIp = serverIp.substring(0, index);
			boolean flag = false;
			for (int i = 1; i < 12; i++) {
				Socket socket = new Socket();
				InetSocketAddress inetSocketAddress = new InetSocketAddress(
						serverIp + i, PORT);
				try {
					socket.connect(inetSocketAddress, 1000);
					socket.close();
					flag = true;
					break;
				} catch (Exception e) {
					Log.e("ScanThread", "Scan Error:"+serverIp+i);
				}
				if (flag) {
					System.out.println("ip=" + serverIp + i);
				}
			}
			super.run();
		}
	}

}
