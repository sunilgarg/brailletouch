package com.typingtest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;

public class DataLogger {
	
	static final String DATAFILE = "typingTestStorage";
	private Activity owner;
	private FileOutputStream persistentLog;
	private FileOutputStream sessionLog;
	private Calendar calendar;
	private List<String> sessionLogCharList;
	private Date time;
	private String timeString;
	
	public DataLogger(Context c) {
		
		owner = (Activity) c;
		sessionLogCharList = new ArrayList<String>();
		calendar = Calendar.getInstance();
		time = calendar.getTime();
		timeString = time.toGMTString();
			
    	try {
    	    persistentLog = c.openFileOutput(DATAFILE, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
    	try {
    		sessionLog = c.openFileOutput(timeString, Context.MODE_WORLD_READABLE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
	public void addKeyStrokeToSessionLog(String in) {
		long nanos = System.nanoTime();
		char key;
		String line;
		
		//get ASCII char
		if (in.equals("\b")) {
			key = (char)8;
		}
		else if(in.equals("\r")) {
			key = (char)13;
		}
		else {
			key = in.charAt(0);
		}
		
		line = String.valueOf(nanos + "   " + String.valueOf((int)key) + " " + String.valueOf(key)) + "\n";
		sessionLogCharList.add(line);
		
		if(in.equals("\r")) {
			addWordToSessionLog();
			sessionLogCharList.clear();
		}
		
	}
	
	//we should make this function the Activities responsibility to call - can pass target phrase, WPM, etc.
	private void addWordToSessionLog() {
		
		//TODO: APPEND FIRST LINE (see below)
		
		for (int i = 0; i < sessionLogCharList.size(); i+=1) {
			try {
				sessionLog.write(sessionLogCharList.get(i).getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public String getSessionCharListAsString() {
		String retString = "";
		for(int i = 0; i < sessionLogCharList.size(); i++) {
			retString += sessionLogCharList.get(i);
		}
		
		return retString;
	}

}

/*
'wear a crown with many jewels'	29.866666666666667 WPM	100.0%
1314549531890  	119	w
1314549532406	101	e
1314549532531	97	a
1314549532968	114	r
1314549534109	32	 
1314549534500	97	a
1314549535015	32	 
1314549535421	99	c
1314549535953	114	r
1314549536453	111	o
1314549536953	119	w
1314549537437	110	n
1314549538015	32	 
1314549538187	119	w
1314549538625	105	i
1314549539031	116	t
1314549539531	104	h
1314549539718	32	 
1314549540000	109	m
1314549540156	97	a
1314549540656	110	n
1314549540765	121	y
1314549541187	32	 
1314549541593	106	j
1314549541812	101	e
1314549542234	119	w
1314549542593	101	e
1314549542812	108	l
1314549543140	115	s
1314549543921	13	
*/