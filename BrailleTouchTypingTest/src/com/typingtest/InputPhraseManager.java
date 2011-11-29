package com.typingtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class InputPhraseManager {

	private List<String> phrases;
	
	public InputPhraseManager(Context c) {
		
		phrases = new ArrayList<String>();
		readRawTextFile(c);
	}
	
	private void readRawTextFile(Context ctx) {
         InputStream inputStream = ctx.getResources().openRawResource(R.raw.phrases2);
         InputStreamReader inputreader = new InputStreamReader(inputStream);
         BufferedReader buffreader = new BufferedReader(inputreader);
         String line;
             try {
               while (( line = buffreader.readLine()) != null) {
            	   phrases.add(line);
                 }
           } catch (IOException e) {      
        }       
    }
	
	public String getPhrase() {
		int random = 0 + (int)(Math.random()*phrases.size());	
		return phrases.get(random);
	}
}

