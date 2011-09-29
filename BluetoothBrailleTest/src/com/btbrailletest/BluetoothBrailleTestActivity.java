/* Derived from DeviceListActivity.java in android-7/samples/BluetoothChat
 *
 * Modifications
 * Copyright (C) 2011 Peter Brinkmann (peter.brinkmann@gmail.com)
 *
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btbrailletest;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * A simple activity that offers the user a list of paired Bluetooth devices to choose from.
 */
public class BluetoothBrailleTestActivity extends Activity {

	public static final String DEVICE_ADDRESS = "device_address";

	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> pairedDevicesAdapter;
	private boolean empty;
	ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	ListView pairedListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		setResult(Activity.RESULT_CANCELED);
		pairedDevicesAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(pairedDevicesAdapter);
		pairedListView.setOnItemClickListener(clickListener);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		empty = pairedDevices.isEmpty();
		if (!empty) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
				devices.add(device);
			}
		} else {
			String noDevices = getResources().getText(R.string.none_paired).toString();
			pairedDevicesAdapter.add(noDevices);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private OnItemClickListener clickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int selPos, long arg3) {
			if (!empty) {
				BluetoothDevice device = devices.get(selPos);
				String info = ((TextView) v).getText().toString();
				String address = info.substring(info.length() - 17);
				Toast.makeText(BluetoothBrailleTestActivity.this, info, Toast.LENGTH_LONG).show();
				// Create an Intent to launch an Activity for the tab (to be reused)
				Bundle bundle = new Bundle();
				bundle.putParcelable("device", device);
			    Intent intent = new Intent().setClass(BluetoothBrailleTestActivity.this, TouchPadActivity.class);
			    intent.putExtras(bundle);
			    startActivity(intent);
			}
		}
	};
}
