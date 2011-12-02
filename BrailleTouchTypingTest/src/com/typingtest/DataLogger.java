package com.typingtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DataLogger {
	
	static final String DATADIR = "typingTestStorage";
	private Activity owner;
	private FileOutputStream persistentLog;
	private FileOutputStream sessionLog;
	private Calendar calendar;
	private List<String> sessionLogCharList;
	private String sessionLogChars;
	private String sessionLogCharsSave;
	private Date time;
	private String timeString;
	private String storagePath;
	private File SDsessionLog;
	FileWriter SDsessionLogWriter;
    BufferedWriter SDsessionLogOut; 
	
	public DataLogger(Context c) {
		
		owner = (Activity) c;
		sessionLogCharList = new ArrayList<String>();
		sessionLogChars = "";
		calendar = Calendar.getInstance();
		time = calendar.getTime();
		timeString = time.toGMTString();
		timeString = timeString.replace(':', '_');
    	timeString = timeString.replace(' ', '_');
		
		/*
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
    	*/
    	storagePath = c.getFilesDir().getPath();
    	
    	String sessionLogName = timeString.concat(".txt");
    	try {
    	    File root = Environment.getExternalStorageDirectory();
    	    File SDsessionDir = new File(root, DATADIR);
    	    SDsessionDir.mkdirs();
    	        SDsessionLog = new File(SDsessionDir, sessionLogName);
    	   
    	        if(!SDsessionLog.exists()) {
    	        	SDsessionLog.createNewFile();
    	        }
    	        SDsessionLogWriter = new FileWriter(SDsessionLog);
    	        SDsessionLogOut = new BufferedWriter(SDsessionLogWriter);
    	   
    	} catch (IOException e) {
    	   
    	}
    	
	}
	
	public void addKeyStrokeToSessionLog(String in) {
		addKeyStrokeToSessionLog(in,"",0,0);
	}
	
	public void addKeyStrokeToSessionLog(String in, String targetPhrase, float wpm, double totalError) {
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
		
		if (String.valueOf((int)key).length() < 3) {
		line = String.valueOf(nanos + "   " + String.valueOf((int)key) + "  " + String.valueOf(key)) + "\n";
		}
		else {
			line = String.valueOf(nanos + "   " + String.valueOf((int)key) + " " + String.valueOf(key)) + "\n";
		}
		sessionLogCharList.add(line);
		sessionLogChars += String.valueOf(key);
		
		if(in.equals("\r")) {
			sessionLogCharsSave = sessionLogChars;
			sessionLogChars = "";
		}
		
	}
	
	//we should make this function the Activities responsibility to call - can pass target phrase, WPM, etc.
	public void addWordToSessionLog(String targetPhrase, float wpm, double totalError) {
		DecimalFormat format = new DecimalFormat("##.##");
		String firstLine = "'" + targetPhrase + "' WPM: " + format.format(wpm) + " Total Error: " + format.format(totalError*100) + "% \n";
		try {
			SDsessionLogOut.write(firstLine);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//TODO: APPEND FIRST LINE (see below)
		
		for (int i = 0; i < sessionLogCharList.size(); i+=1) {
			try {
				//sessionLog.write(sessionLogCharList.get(i).getBytes());
				SDsessionLogOut.write(sessionLogCharList.get(i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sessionLogCharList.clear();
		
	}
	
	public String getSessionCharListAsString() {
		return sessionLogCharsSave.substring(0, sessionLogCharsSave.length()-1);
	}

	public void closeDataLogger() {
		
		try {
			//persistentLog.close();
			//sessionLog.close();
			SDsessionLogOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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