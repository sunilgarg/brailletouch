package edu.gatech.ic.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import edu.gatech.ic.android.keyDetector.AbstractKeyDetector;

public class BrailleKeyboardView extends FrameLayout {
	private static final String TAG = "BrailleKeyboardView";
	
	private static final long MAX_TOUCH_TIME_DIFF_MS = 250;
	
	private AbstractKeyDetector keyDetector;
	
	private ViewGroup rootLayout;
	private ViewGroup touchLayout;
	private TextView letterText;
	
	private ArrayList<Point> currentLocations;
	private HashMap<Integer, Integer> mapLocation;
	
	public BrailleKeyboardView(Context context, AbstractKeyDetector keyDetector) {
		super(context);
		
		this.keyDetector = keyDetector;
		
		LayoutInflater.from(context).inflate(R.layout.braille_keyboard, this);
		rootLayout = (ViewGroup) findViewById(R.id.braille_keyboard_root_layout);
		touchLayout = (ViewGroup) findViewById(R.id.braille_keyboard_layout);
		letterText = (TextView) findViewById(R.id.braille_keyboard_letter_text);
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		rootLayout.setLayoutParams(new FrameLayout.LayoutParams(display.getWidth(), display.getHeight()));
		
		currentLocations = new ArrayList<Point>();
		mapLocation = new HashMap<Integer, Integer>();
		
		setOnTouchListener(touchListener);
	}
	
	public void setText(String text) {
		letterText.setText(text);
	}
	
	private void updateLocations(MotionEvent event) {
		
		for (int i = 0; i < event.getPointerCount(); i++) {
			
			int currentIndex = event.getPointerId(i);
			if (currentIndex == -1)
				continue;
			
			Integer pointerIndex = mapLocation.get(currentIndex);
			if (pointerIndex == null)
				continue;
			
			float x = event.getX(currentIndex);
			float y = event.getY(currentIndex);
			
			Point currentPoint = currentLocations.get(pointerIndex);
			
			Boolean didUpdate = currentPoint.update(x, y);
			
			if (didUpdate) {
				drawCircle(x, y, Color.GREEN, "");
			}
			
		}
		
	}
	
	private Long updateUp(MotionEvent event) {

		Long lastUpMS = 0L;
		
		for (int i = 0; i < event.getPointerCount(); i++) {
			
			int currentIndex = event.getPointerId(i);
			if (currentIndex == -1) {
				continue;
			}
			
			Integer pointerIndex = mapLocation.get(currentIndex);
			if (pointerIndex == null) {
				Log.d(TAG, "CHRIS: pointer index is null");
				continue;
			}
			
			float x = event.getX(currentIndex);
			float y = event.getY(currentIndex);
			
			Point currentPoint = currentLocations.get(pointerIndex);
			currentPoint.update(x, y);
			currentPoint.setEnd();
			
			if (currentPoint.getEndTimeMS() > lastUpMS.longValue()) {
				lastUpMS = currentPoint.getEndTimeMS();
			}
			
			drawPoint(currentPoint, Color.RED);
		}
		
		return lastUpMS;
	}
	
	private void removeBadPoints(long lastUpTime) {
		
		ArrayList<Point> pointsToRemove = new ArrayList<Point>();
		
		for (Point point : currentLocations) {
			
			Log.d(TAG, "CHRIS: lastuptime: " + lastUpTime + " point uptime: " + point.getEndTimeMS() + " diff: " + (lastUpTime - point.getEndTimeMS()));
			if ((lastUpTime - point.getEndTimeMS()) > MAX_TOUCH_TIME_DIFF_MS) {
				pointsToRemove.add(point);
			}
		}
		
		for (Point point : pointsToRemove) {
			currentLocations.remove(point);
		}
		
	}
	
	private void drawPoint(Point point, int color) {
		drawCircle(point.getEndX(), point.getEndY(), color, "");
	}
	
	private void drawCircle(float x, float y, int color, String name) {
		
		FingerMarker marker = new FingerMarker(getContext(), x, y, color, name);
		touchLayout.addView(marker);
	}
	
	private View.OnTouchListener touchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			Log.d(TAG, TAG + event.toString());
			
			int action = event.getAction();
			
			switch(action & MotionEvent.ACTION_MASK) {
			
				case MotionEvent.ACTION_DOWN:
					touchLayout.removeAllViews();
					// fall through
				case MotionEvent.ACTION_POINTER_DOWN:
					
					int pointerID = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
					int pointerIndex = event.findPointerIndex(pointerID);
					if (pointerIndex == -1)
						break;
					
					float x = event.getX(pointerIndex);
					float y = event.getY(pointerIndex);
					
					Point point = new Point(x, y, pointerIndex);
					drawPoint(point, Color.BLUE);
					mapLocation.put(pointerIndex, currentLocations.size());
					currentLocations.add(point);

					break;
					
				case MotionEvent.ACTION_MOVE:
					
					updateLocations(event);
					
					break;
					
				case MotionEvent.ACTION_POINTER_UP:
					
					updateUp(event);
					
					break;
					
				case MotionEvent.ACTION_UP:
					
					Long lastUp = updateUp(event);
					removeBadPoints(lastUp);
					
					letterText.setText("");
					keyDetector.onTouchUp(currentLocations);
										
					currentLocations.clear();
					mapLocation.clear();
					
					break;
			}
			
			return true;
		}
	};

}
