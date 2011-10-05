package com.btbrailletest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class TouchPadActivity extends Activity implements TextWatcher{
	private EditText et;
	BluetoothDevice device;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.touchpad);
    	
    	Bundle bundle = this.getIntent().getExtras();
    	device = (BluetoothDevice) bundle.get("device");
    	
    	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	
    	et = (EditText) findViewById(R.id.touchpad_letter_layout);
    	et.requestFocus();
    	et.addTextChangedListener(this);
    }

    /**
     * Called when a character is entered.
     */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Toast.makeText(this, ""+s.charAt(start), Toast.LENGTH_SHORT).show();
		char charToSend = s.charAt(start);
		sendChar(charToSend);
	}
	
	/**
	 * Function to send char across bluetooth
	 * @param charToSend
	 */
	public void sendChar(char charToSend) {
		/**TODO 
		 * Some one figure out bluetooth and implement it here. 
		 * The variable "device" is the device chosen from
		 * the list at the beginning of the app.
		 */
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		//DO NOTHING
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		//DO NOTHING
	}

}
