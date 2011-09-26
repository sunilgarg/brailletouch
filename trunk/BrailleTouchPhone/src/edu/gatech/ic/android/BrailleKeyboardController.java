package edu.gatech.ic.android;

import android.inputmethodservice.InputMethodService;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import edu.gatech.ic.android.keyDetector.AbstractKeyDetector;
import edu.gatech.ic.android.keyDetector.AbstractKeyDetector.KeyPressListener;
import edu.gatech.ic.android.keyDetector.SimpleBrailleKeyDetector;
import edu.gatech.ic.android.settings.AppSettings;

public class BrailleKeyboardController extends InputMethodService {
	private static final String TAG = "BrailleKeyboardController";
	
	private static final int SOUND_CLICK = R.raw.sound_key;
	private static final int SOUND_BACKSPACE = R.raw.sound_backspace;
	private static final int SOUND_RETURN = R.raw.sound_return;
	private static final int SOUND_UNDETECTED = R.raw.sound_undetected;
	
	private MediaPlayer clickPlayer;
	private MediaPlayer backspacePlayer;
	private MediaPlayer returnPlayer;
	private MediaPlayer undetectedPlayer;
	
	private AppSettings appSettings;
	
	private AbstractKeyDetector keyDetector;
	private BrailleKeyboardView brailleKeyboardView;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		appSettings = new AppSettings(this);
		
		clickPlayer = MediaPlayer.create(this, SOUND_CLICK);
		backspacePlayer = MediaPlayer.create(this, SOUND_BACKSPACE);
		returnPlayer = MediaPlayer.create(this, SOUND_RETURN);
		undetectedPlayer = MediaPlayer.create(this, SOUND_UNDETECTED);
	}

	@Override
	public void onDestroy() {
		
		try {
			
			clickPlayer.release();
			backspacePlayer.release();
			returnPlayer.release();
			undetectedPlayer.release();
			
		} catch (Exception ex) {
			
		}
		
		super.onDestroy();
	}

	@Override 
	public View onCreateInputView() {
    
		keyDetector = new SimpleBrailleKeyDetector(this, keyListener);
		brailleKeyboardView = new BrailleKeyboardView(this, keyDetector);
		
        return brailleKeyboardView;
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
	
	private void playSound(MediaPlayer player) {
		
		if (!appSettings.isSoundEnabled()) {
			return;
		}
		
		try {
			
			player.start();
			
		} catch (Exception ex) {
			Log.e(TAG, "Unable to play sound. " + ex);
		}
	}
	
	private KeyPressListener keyListener = new KeyPressListener() {
		
		@Override
		public void onKeyPress(String keys) {
			
			brailleKeyboardView.setText(keys);
			playSound(clickPlayer);
			
			InputConnection connection = getCurrentInputConnection();
			connection.commitText(keys, keys.length());
		}

		@Override
		public void onSpace() {
			
			playSound(clickPlayer);
			
			keyDownUp(KeyEvent.KEYCODE_SPACE);
		}

		@Override
		public void onBackspace() {
			
//			playSound(backspacePlayer);
			
			keyDownUp(KeyEvent.KEYCODE_DEL);
		}

		@Override
		public void onReturn() {
			
			playSound(returnPlayer);
			
			keyDownUp(KeyEvent.KEYCODE_ENTER);
			handleClose();
		}

		@Override
		public void onUndetected() {
			playSound(undetectedPlayer);
		}

	}; 
}
