package edu.gatech.ic.android.keyDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import edu.gatech.ic.android.Point;

public class SimpleBrailleKeyDetector extends AbstractKeyDetector {
	private static final String TAG = "SimpleBrailleKeyDetector";
	
	private static final int NAVIGATION_BAR_HEIGHT = 10;
	
	private ArrayList<Point> currentLocations;
	private int screenWidth;
	private int screenHeight;
	
	public SimpleBrailleKeyDetector(Context context, KeyPressListener keyListener) {
		super(context, keyListener);
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight() - NAVIGATION_BAR_HEIGHT;
	}

	@Override
	public void onTouchUp(ArrayList<Point> points) {
		
		currentLocations = points;
		
		Boolean gesturesFound = detectGestures();
		
		if (!gesturesFound) {
			
			Boolean keyDetected = detectKeyPress();
			
			if (!keyDetected) {
				keyListener.onUndetected();
			}
		}
	}
	
	private Boolean detectGestures() {
		
		if (currentLocations.size() == 2) {
			Point point1 = currentLocations.get(0);
			Point point2 = currentLocations.get(1);
			
			if (point1.isSwipeRight() && point2.isSwipeRight()) {
				keyListener.onReturn();
				return true;
			}
		} else if (currentLocations.size() == 1) {
			Point point = currentLocations.get(0);
			
			if (point.isSwipeLeft()) {
				keyListener.onBackspace();
				return true;
			} else if (point.isSwipeRight()) {
				keyListener.onSpace();
				return true;
			}
		}
		
		return false;
	}
	
	private Boolean detectKeyPress() {
		
		Boolean[] keyPresses = new Boolean[6];
		int middleIndex = splitPointArray();
		Log.d(TAG, "mid index: " + middleIndex);
		
		for (int i = 0; i < keyPresses.length; i++) {
			keyPresses[i] = false;
		}
		
		
		// Left side
		if (middleIndex == 3) {
			// All fingers on the left side are down
			for (int i = 0; i < 3; i++) {
				keyPresses[i] = true;
			}
			
		} else if (middleIndex == 2) {
			// 2 left fingers down
			Point point1 = currentLocations.get(0);
			Point point2 = currentLocations.get(1);
			
			if (Math.abs(point1.getEndY() - point2.getEndY()) >= (screenHeight / 2)) {
				// Button 1 + 3 pressed
				keyPresses[0] = true;
				keyPresses[2] = true;
			} else {
				// Top 2 or bottom 2 buttons
				float screenMidpoint = screenHeight / 2;
				float touchMidpoint = (point1.getEndY() + point2.getEndY()) / 2;
				
				Log.d(TAG, "Screen midpoint: " + screenMidpoint + " touch midpoint: " + touchMidpoint + " touch1 y: " + point1.getEndY() + " touch2 y: " + point2.getEndY());
				
				if (touchMidpoint > screenMidpoint) {
					// Bottom 2 buttons (2 + 3)
					keyPresses[1] = true;
					keyPresses[2] = true;
				} else {
					// Top 2 buttons (1 + 2)
					keyPresses[0] = true;
					keyPresses[1] = true;
				}
				
			}
			
		} else if (middleIndex == 1) {
			// 1 left finger down
			
			Point point = currentLocations.get(0);
			
			int lowerThird = screenHeight / 3;
			int middleThird = lowerThird + lowerThird;
			
			if (point.getEndY() <= lowerThird) {
				// Button 1 pressed
				keyPresses[0] = true;
			} else if (point.getEndY() <= middleThird) {
				// Button 2 pressed
				keyPresses[1] = true;
			} else {
				// Button 3 pressed
				keyPresses[2] = true;
			}
			
		}
		
		// Right side
		if (middleIndex < currentLocations.size()) {
		
			int rightPoints = (currentLocations.size() - middleIndex);
			
			if (rightPoints >= 3) {
				// All fingers on the right side are down
				for (int i = 3; i < 6; i++) {
					keyPresses[i] = true;
				}
				
			} else if (rightPoints == 2) {
				// 2 right fingers down
				Point point1 = currentLocations.get(middleIndex);
				Point point2 = currentLocations.get(middleIndex + 1);
				
				if (Math.abs(point1.getEndY() - point2.getEndY()) >= (screenHeight / 2)) {
					// Button 4 + 6 pressed
					keyPresses[3] = true;
					keyPresses[5] = true;
				} else {
					// Top 2 or bottom 2 buttons
					float screenMidpoint = screenHeight / 2;
					float touchMidpoint = (point1.getEndY() + point2.getEndY()) / 2;
					
					if (touchMidpoint > screenMidpoint) {
						// Bottom 2 buttons (5 + 6)
						keyPresses[4] = true;
						keyPresses[5] = true;
					} else {
						// Top 2 buttons (4 + 5)
						keyPresses[3] = true;
						keyPresses[4] = true;
					}
					
				}
				
			} else if (rightPoints == 1) {
				// 1 left finger down
				
				Point point = currentLocations.get(middleIndex);
				
				int lowerThird = screenHeight / 3;
				int middleThird = lowerThird + lowerThird;
				
				if (point.getEndY() <= lowerThird) {
					// Button 4 pressed
					keyPresses[3] = true;
				} else if (point.getEndY() <= middleThird) {
					// Button 5 pressed
					keyPresses[4] = true;
				} else {
					// Button 6 pressed
					keyPresses[5] = true;
				}
				
			}
		}
		
		String printString = "";
		for (int i = 0; i < keyPresses.length; i++) {
			printString += (keyPresses[i]? "1 " : "0 ");
		}
		Log.d(TAG, "Recognized: " + printString);
		
		Character recognizedChar = BrailleCharacter.getChar(keyPresses);
		Log.d(TAG, "Recognized: " + recognizedChar);
		
		if (recognizedChar != null) {
			keyListener.onKeyPress(recognizedChar.toString());
			return true;
		}
		
		return false;
	}
	
	// Returns the first index of a touch on the right side of the screen
	private int splitPointArray() {
		
		Collections.sort(currentLocations, new Comparator<Point>() {

			@Override
			public int compare(Point point1, Point point2) {
				
				if (point1.getEndX() < point2.getEndX())
					return 1;
				else if (point1.getEndX() > point2.getEndX())
					return -1;
				else
					return 0;
				
			}
		});
		
		for (int i = 0; i < currentLocations.size(); i++) {
			Log.d(TAG, "current locations[" + i + "] = " + currentLocations.get(i).getEndX());
		}
		
		
		float screenMiddle = screenWidth / 2;
		for (int i = 0; i < currentLocations.size(); i++) {
			
			if (!(currentLocations.get(i).getEndX() > screenMiddle)) {
				return i;
			}
			
		}
		
		return currentLocations.size();
	}

}
