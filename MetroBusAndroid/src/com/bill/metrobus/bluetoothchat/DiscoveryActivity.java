package com.bill.metrobus.bluetoothchat;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DiscoveryActivity extends ListActivity implements OnTouchListener
{
	private BluetoothDevice d = null;
	private Handler handler = new Handler();
	// 获得蓝牙适配器对象
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	// 用来存储搜索到的蓝牙设备
	private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();

	// 是否搜索完成
	private volatile boolean discoveryFinished;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);  
		//  如果蓝牙适配器没有打开，则关闭Activity
		if (!bluetoothAdapter.isEnabled())
		{
			//bluetoothAdapter.enable();
			finish();
			return;
		}
		
		//  注册接收器                                                                   
		IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(discoveryReceiver, discoveryFilter);                                      
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);                        
		registerReceiver(foundReceiver, foundFilter);                  
		//  显示一个对话框,正在搜索蓝牙设备 
		MyProgressDialog.indeterminate(DiscoveryActivity.this, handler,
				"正在扫描...", discoveryWorker, new OnDismissListener()
		{                                                        
			public void onDismiss(DialogInterface dialog)
			{
				
				for (; bluetoothAdapter.isDiscovering();)
				{
					
					bluetoothAdapter.cancelDiscovery();
				}
				
				discoveryFinished = true;
			}
		}, true, new OnCancelListener() 
		{
			@Override
			public void onCancel(DialogInterface dialog) 
			{
				// TODO Auto-generated method stub
				bluetoothAdapter.cancelDiscovery();
			}
		});
	}
	
	private Runnable discoveryWorker = new Runnable()
	{
		public void run()
		{
			// 开始搜索
			bluetoothAdapter.startDiscovery();        
			while(true)
			{
				if (discoveryFinished)
				{
					break;
				}
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	};
	
	// 搜索到一个远程蓝牙设备时调用
	private BroadcastReceiver foundReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			//  获得搜索结果数据
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    //  将结果添加到设备列表中
			bluetoothDevices.add(device);
			//  显示列表
			showDevices();
		}
	};
	
	//搜索完成时调用
	private BroadcastReceiver discoveryReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//  卸载注册的接收器 
			unregisterReceiver(foundReceiver);
			unregisterReceiver(this);
			discoveryFinished = true;
		}
	};


	//显示搜索设备列表
	protected void showDevices()
	{
		List<String> list = new  ArrayList<String>();
		for (int i = 0, size = bluetoothDevices.size(); i < size; ++i)
		{
			StringBuilder b = new StringBuilder();
		    d = bluetoothDevices.get(i);
			b.append(d.getAddress());
			b.append('\n');
			b.append(d.getName());
			b.append('\n');
			if(d.getBondState()==BluetoothDevice.BOND_BONDED)
				b.append("点击与此设备连接");
			String s = b.toString();
			list.add(s);
		}

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		handler.post(new Runnable()
		{
			public void run()
			{
				setListAdapter(adapter);
			}
		});
	}

	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		d = bluetoothDevices.get(position);
		if(d.getBondState()==BluetoothDevice.BOND_BONDED)
		{
			Intent result = new Intent();
			String address = d.getAddress();    
			Bundle bundle = new Bundle();
			bundle.putString("device_address" , address);
			result.putExtras(bundle);
			setResult(RESULT_OK, result);
			finish();
		}
		else 
		{
			Toast.makeText(this, "该设备没有配对！", Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onDestroy()
	{                                             
		Intent result = new Intent();
		String error = "ERROR";
		Bundle bundle = new Bundle();
		bundle.putString("error", error);
		result.putExtras(bundle);
		setResult(RESULT_CANCELED , result);
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		// TODO Auto-generated method stub
		Log.i("Touch","Touch");
		return false;
	}
}
