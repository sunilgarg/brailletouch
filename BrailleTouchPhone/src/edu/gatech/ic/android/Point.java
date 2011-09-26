package edu.gatech.ic.android;

public class Point {
	
	private static final int SWIPE_MAX_TIME_MS = 500;
	private static final int SWIPE_LENGTH = 100;
	
	private int pointerIndex;
	
	private float startX;
	private float startY;
	
	private float endX;
	private float endY;
	
	private float xDistTraveled;
	private float yDistTraveled;
	
	private Long startTimeMS;
	private Long endTimeMS;
	
	public Point(float x, float y, int pointerIndex) {
		this.startX = this.endX = x;
		this.startY = this.endY = y;
		this.pointerIndex = pointerIndex;
		
		xDistTraveled = 0;
		yDistTraveled = 0;
		
		startTimeMS = System.currentTimeMillis();
	}
	
	public Boolean update(float x, float y) {
		
		Boolean result = false;
		
		if (x != this.endX || y != this.endY) {
			result = true;
		}
		
		xDistTraveled += (this.endX - x);
		yDistTraveled += (this.endY - y);
		
		this.endX = x;
		this.endY = y;
		
		return result;
	}

	public void setEnd() {
		endTimeMS = System.currentTimeMillis();
	}
	
	public Boolean isSwipeRight() {
		
		if (endTimeMS == null)
			return false;
		
		Boolean validTime = ((endTimeMS - startTimeMS) <= SWIPE_MAX_TIME_MS);
		Boolean validSwipeLength = (Math.abs(xDistTraveled) - SWIPE_LENGTH >= 0);
		Boolean validDirection = (xDistTraveled > 0);
		
		return (validTime && validSwipeLength && validDirection);
	}
	
	public Boolean isSwipeLeft() {
		
		if (endTimeMS == null)
			return false;
		
		Boolean validTime = ((endTimeMS - startTimeMS) <= SWIPE_MAX_TIME_MS);
		Boolean validSwipeLength = (Math.abs(xDistTraveled) - SWIPE_LENGTH >= 0);
		Boolean validDirection = (xDistTraveled < 0);
		
		return (validTime && validSwipeLength && validDirection);
	}
	
	public int getPointerIndex() {
		return pointerIndex;
	}

	public void setPointerIndex(int pointerIndex) {
		this.pointerIndex = pointerIndex;
	}

	public float getStartX() {
		return startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getStartY() {
		return startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}

	public float getEndX() {
		return endX;
	}

	public void setEndX(float endX) {
		this.endX = endX;
	}

	public float getEndY() {
		return endY;
	}

	public void setEndY(float endY) {
		this.endY = endY;
	}

	public float getxDistTraveled() {
		return xDistTraveled;
	}

	public void setxDistTraveled(float xDistTraveled) {
		this.xDistTraveled = xDistTraveled;
	}

	public float getyDistTraveled() {
		return yDistTraveled;
	}

	public void setyDistTraveled(float yDistTraveled) {
		this.yDistTraveled = yDistTraveled;
	}

	public long getStartTimeMS() {
		return startTimeMS;
	}

	public void setStartTimeMS(long startTimeMS) {
		this.startTimeMS = startTimeMS;
	}

	public long getEndTimeMS() {
		return endTimeMS;
	}

	public void setEndTimeMS(long endTimeMS) {
		this.endTimeMS = endTimeMS;
	}
	
	public long getTouchTimeMS() {
		return endTimeMS - startTimeMS;
	}
	
}
