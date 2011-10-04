package com.btbrailletest;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.gatech.ic.android.SerializableMotionEvent;

public class TouchPadActivity extends Activity {
	private ViewGroup frameLayout;
	float x = 0;
	float y = 0;
	private TextView tv;
	BluetoothDevice device;
	
	private View.OnTouchListener touchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch(action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					// fall through
				case MotionEvent.ACTION_POINTER_DOWN:
					sendTouchEvent(event);
					int pointerID = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
					int pointerIndex = event.findPointerIndex(pointerID);
					if (pointerIndex == -1)
						break;
					x = event.getX(pointerIndex);
					y = event.getY(pointerIndex);
					tv.setText("X: " + x + " Y: " + y);
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_POINTER_UP:
					break;
				case MotionEvent.ACTION_UP:
					System.out.println("X: " + x + " Y: " + y);
					tv.setText("X: " + x + " Y: " + y);
					break;
			}
			return true;
		}
	};
	
	public void sendTouchEvent(MotionEvent event) {
		SerializableMotionEvent sme = new SerializableMotionEvent(event);
		String json = sme.toJson();
		
		// the below lines are for testing purposes!
		SerializableMotionEvent sme2 = SerializableMotionEvent.fromJson(json);
		System.out.println("sme2 x " + sme2.getX() + " y " + sme2.getY());
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.touchpad);
    	
    	Bundle bundle = this.getIntent().getExtras();
    	device = (BluetoothDevice) bundle.get("device");
    	
    	frameLayout = (ViewGroup) findViewById(R.id.touchpad_layout);
    	frameLayout.setOnTouchListener(touchListener);
    	
    	tv = (TextView) findViewById(R.id.touchpad_letter_layout);
    	tv.setText(device.toString());
    }

}
