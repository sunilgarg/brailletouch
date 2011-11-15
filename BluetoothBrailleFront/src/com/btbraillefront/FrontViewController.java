package com.btbraillefront;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.inputmethodservice.InputMethodService;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

public class FrontViewController extends InputMethodService {
	private static final String TAG = "BrailleKeyboardController";
	private static final String MAC_ADDRESS = "F0:08:F1:5C:97:BB";

	private MediaPlayer clickPlayer;
	private MediaPlayer backspacePlayer;
	private MediaPlayer returnPlayer;
	private MediaPlayer undetectedPlayer;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothBrailleSendService mSendService = null; 
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast"; 
    private StringBuffer mOutStringBuffer;
    private String mConnectedDeviceName = null;
	
	
	//private AppSettings appSettings;
	
	//private AbstractKeyDetector keyDetector;
	//private BrailleKeyboardView brailleKeyboardView;
	private FrontView frontView;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		android.os.Debug.waitForDebugger();
		Log.i(TAG, "onCreate");
		if (mSendService == null) setupService();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Log.i(TAG, "setupService Return");
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
		
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
        // Attempt to connect to the device
        mSendService.connect(device, true); //always secure
		
		//appSettings = new AppSettings(this);
		
		//clickPlayer = MediaPlayer.create(this, SOUND_CLICK);
		//backspacePlayer = MediaPlayer.create(this, SOUND_BACKSPACE);
		//returnPlayer = MediaPlayer.create(this, SOUND_RETURN);
		//undetectedPlayer = MediaPlayer.create(this, SOUND_UNDETECTED);
	}

	private void setupService() {
    	
		Log.i(TAG, "setupService");
    	mSendService = new BluetoothBrailleSendService(this, mHandler);
    	mOutStringBuffer = new StringBuffer("");
    	//mSendService.connect(device, true);
    	
    }
	
	private final void setStatus(int resId) {
        //final ActionBar actionBar = getActionBar();
        //actionBar.setSubtitle(resId);
		Log.i(TAG, "setStatus: " + resId);
    }

    private final void setStatus(CharSequence subTitle) {
        //final ActionBar actionBar = getActionBar();
        //actionBar.setSubtitle(subTitle);
    	Log.i(TAG, "setStatus " + subTitle.toString());
    }
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
   
                switch (msg.arg1) {
                case BluetoothBrailleSendService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothBrailleSendService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothBrailleSendService.STATE_LISTEN:
                case BluetoothBrailleSendService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                
                if(readMessage.equals("\r")) {
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                }
                else if(readMessage.equals("\b")) {
                	//InputConnection connection = getCurrentInputConnection();
        			//connection.commitText(readMessage, 1);
        			keyDownUp(KeyEvent.KEYCODE_DEL);
                }
                else {
                InputConnection connection = getCurrentInputConnection();
    			connection.commitText(readMessage, readMessage.length());
                }
                
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
	
	@Override
	public void onDestroy() {
		
		try {
			
			clickPlayer.release();
			backspacePlayer.release();
			returnPlayer.release();
			undetectedPlayer.release();
			if (mSendService != null) mSendService.stop();
			
		} catch (Exception ex) {
			
		}
		
		super.onDestroy();
	}

	@Override 
	public View onCreateInputView() {
    
		//keyDetector = new SimpleBrailleKeyDetector(this, keyListener);
		Log.i(TAG, "onCreateInputView");
		frontView = new FrontView(this);
		
        return frontView;
    }
		
	@Override
	public void setCandidatesViewShown(boolean shown) {
		super.setCandidatesViewShown(false);
	}

	@Override
	public View onCreateExtractTextView () {
		return null;
	}
	
	@Override
	public boolean onEvaluateFullscreenMode () {
		return false;
	}
	
	private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }
	
	private void handleClose() {
        requestHideSelf(0);
    }
	
}