package edu.gatech.ic.android;

import com.google.gson.Gson;

import android.view.MotionEvent;

/**
 * This class is used to serialize a subset of the data contained 
 * in a MotionEvent for network transfer.
 * 
 * @author sunil
 *
 */
public class SerializableMotionEvent {
	private float x;
	private float y;
	
	/**
	 * Public constructor
	 * @param e the MotionEvent to be serialized
	 */
	public SerializableMotionEvent(MotionEvent e) {
		this.x = e.getX();
		this.y = e.getY();
	}
	
	/**
	 * Instantiates an object based on the serialized JSON 
	 * @param s JSON string created by this.getJson
	 * @return
	 */
	public static SerializableMotionEvent fromJson(String s) {
		Gson gson = new Gson();
		return gson.fromJson(s, SerializableMotionEvent.class);
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	/**
	 * Returns a JSON string representing this object
	 * @return
	 */
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
