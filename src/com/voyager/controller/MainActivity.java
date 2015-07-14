package com.voyager.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author wuhaojie
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	/**
	 * 服务端端口
	 */
	public static final int PORT = 3000;
	/**
	 * handler查找失败
	 */
	private static final int FAILED = -1000;
	/**
	 * handler查找成功
	 */
	private static final int SUCCEED = 1000;
	/**
	 * 主界面命令编辑框
	 */
	private EditText et_command;
	/**
	 * 主界面发送按钮
	 */
	private Button btn_send;
	/**
	 * WiFi工具类
	 */
	private WifiUtils wifiUtils;
	/**
	 * 主界面进度条
	 */
	private ProgressBar pd_main;
	/**
	 * 连接ip超时
	 */
	private static final int TIMEOUT = 50;
	/**
	 * 最大搜索ip
	 */
	private static final int MAX_IP = 256;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_send = (Button) findViewById(R.id.btn_send);
		et_command = (EditText) findViewById(R.id.et_command);
		pd_main = (ProgressBar) findViewById(R.id.pd_main);
		btn_send.setOnClickListener(this);
		pd_main.setMax(MAX_IP);

		wifiUtils = new WifiUtils(MainActivity.this);
		new ScanThread().start();
	}

	@Override
	public void onClick(View v) {
	}

	/**
	 * handler处理消息
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				Toast.makeText(MainActivity.this, "找到服务端", 0);
				break;

			case -1000:
				Toast.makeText(MainActivity.this, "未找到服务端", 0);
				break;
			default:
				pd_main.setProgress(msg.what);
				break;
			}
		};
	};

	private class ScanThread extends Thread {

		@Override
		public void run() {
			String serverIp = wifiUtils.getServerIp();
			int index = serverIp.lastIndexOf(".") + 1; // 要包含'.', 所以加1
			serverIp = serverIp.substring(0, index);
			boolean flag = false;
			for (int i = 1; i < MAX_IP; i++) {
				Socket socket = new Socket();
				InetSocketAddress inetSocketAddress = new InetSocketAddress(
						serverIp + i, PORT);
				try {
					socket.connect(inetSocketAddress, TIMEOUT);
					socket.close();
					Message message = new Message();
					message.what = SUCCEED;
					message.obj = serverIp + i;
					handler.sendMessage(message);
					flag = true;
					break;
				} catch (Exception e) {
					Log.e("ScanThread", "Scan Error:" + serverIp + i);
					handler.sendEmptyMessage(i);
				}
				if (!flag) {
					handler.sendEmptyMessage(FAILED);
				}
			}
			super.run();
		}
	}

}
