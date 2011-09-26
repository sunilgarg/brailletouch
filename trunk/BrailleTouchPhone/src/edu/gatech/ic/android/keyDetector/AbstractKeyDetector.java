package edu.gatech.ic.android.keyDetector;

import java.util.ArrayList;

import android.content.Context;
import edu.gatech.ic.android.Point;

public abstract class AbstractKeyDetector {
	
	protected Context context;
	protected KeyPressListener keyListener;
	
	public interface KeyPressListener {
		public void onKeyPress(String keys);
		public void onSpace();
		public void onBackspace();
		public void onReturn();
		public void onUndetected();
	}
	
	public AbstractKeyDetector(Context context, KeyPressListener keyListener) {
		this.context = context;
		this.keyListener = keyListener;
	}
	
	public abstract void onTouchUp(ArrayList<Point> points);
}
