package com.typingtest;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TypingTestGameActivity extends Activity implements TextWatcher {
	
	private PowerManager.WakeLock mWakeLock;
	private TextView timerLabel, enteredWordLabel, errorPercentage, targetWordView;
	private EditText enteredWordBox;
	private Handler timerHandler = new Handler();
	private ArrayList<String> enteredWordHTML;
	String targetWord;
	String correctWord;
	String enteredWord;
	String words[];
	int targetWordIndex;
	int totalSeconds = 0;
	int completedWords = 0;
	ScaleAnimation completedAnimation;
	AnimationSet completedAnimationSet; 
	
	float totalLetters, correctLetters, wrongLetters;
	private long startTime;
	
	//Timer
    private Runnable updateTimer = new Runnable() {
	   public void run() {
		   
	       final long start = startTime;
	       long millis = System.currentTimeMillis() - start;
	       int seconds = (int) (millis / 1000);
	       totalSeconds = seconds;
	       int minutes = seconds / 60;
	       seconds = seconds % 60;

	       if (seconds < 10) {
	    	   timerLabel.setText("Time: " + minutes + ":0" + seconds);
	       } else {
	    	   timerLabel.setText("Time: " + minutes + ":" + seconds);            
	       }
	     
	       timerHandler.postDelayed(updateTimer, 100);
	   }
	};
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		mWakeLock.acquire();
        
        //Get timer label
        timerLabel = (TextView) findViewById(R.id.timerLabel);
        enteredWordLabel = (TextView) findViewById(R.id.enteredWordLabel);
        errorPercentage = (TextView) findViewById(R.id.errorPercentage);
        targetWordView = (TextView) findViewById(R.id.targetWord);
        enteredWordBox = (EditText) findViewById(R.id.enteredWord);
        words = getResources().getStringArray(R.array.db);
        enteredWordHTML = new ArrayList<String>(); 
        
        errorPercentage.setText("Error Percentage: " + "\nWPM: ");
        
        //Reset game labels
        setupGame();
        
        //Reset statistics
        totalLetters = 0;
        correctLetters = 0;
        wrongLetters = 0;
        
        enteredWordBox.addTextChangedListener(this);
    	startTimer();
    	
    	//animation for when a word is completed
    	completedAnimationSet = new AnimationSet(true);
    	completedAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 2.0f);
    	completedAnimation.setDuration(300);
    	completedAnimation.setStartOffset(0);
    	completedAnimation.setFillAfter(false);
    	completedAnimationSet.addAnimation(completedAnimation);
    	
    	completedAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.5f);
    	completedAnimation.setDuration(150);
    	completedAnimation.setStartOffset(300);
    	completedAnimation.setFillAfter(false);
    	completedAnimationSet.addAnimation(completedAnimation);
    	
    	completedAnimationSet.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation anim)
            {
            };
            public void onAnimationRepeat(Animation anim)
            {
            };
            public void onAnimationEnd(Animation anim)
            {
            	setupGame();
				completedWords++;
            };
        });                 
    }
    
    private void setupGame() {
    	int random = 0 + (int)(Math.random()*words.length);
        targetWord = words[random];
        targetWordView.setText(targetWord);
        enteredWordLabel.setText("");
        enteredWordBox.setText("");
        targetWordIndex = 0;
        enteredWord = "";
        enteredWordHTML.clear();
 
    }
    
    private void startTimer() {
		startTime = System.currentTimeMillis();
		timerHandler.removeCallbacks(updateTimer);
		timerHandler.postDelayed(updateTimer, 100);
    }
    
    private void updateError() {
    	DecimalFormat format = new DecimalFormat("##");
    	errorPercentage.setText("Percent Wrong: " + format.format((wrongLetters/totalLetters)*100) + "%" + "\n" +
    							"WPM: " + format.format(((float)(completedWords*60)/totalSeconds)));
    }
    
    /**
     * Called when a character is entered.
     */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	@Override
	public void afterTextChanged(Editable s) {
	//this eventually needs to be adapted to receiving only individual chars, "\b" and "\r",
	//which is how the bt comm works
		
	//takes care of useless calls - second condition corresponds to call that happens when keyboard is forced down
	if ((s.length() == enteredWord.length()) || (enteredWord.length() - s.length() > 1) ) {
		return;
	}
	else if (s.length() < enteredWord.length()){
		//this is backspace - not sure if we want the game to have backspace enabled
		if (targetWordIndex > 0) {
		enteredWordHTML.remove(enteredWordHTML.size()-1);
		enteredWord = enteredWord.substring(0,enteredWord.length()-1);
		Toast.makeText(this, "backspace", Toast.LENGTH_SHORT).show();
		targetWordIndex-=1;
		}
	}
	else {
		//new char added
			String typed = String.valueOf(s.charAt(s.length()-1));
			enteredWord += typed;
			Toast.makeText(this, typed, Toast.LENGTH_SHORT).show();
		
				//Show letters typed correctly in green, incorrect letter in red
				if(typed.equalsIgnoreCase(String.valueOf(targetWord.charAt(targetWordIndex)))) {
					enteredWordHTML.add("<font color=#0000ff>" + typed + "</font>");
					correctLetters += 1;
				} else {
					enteredWordHTML.add("<font color=#ff0000>" + typed + "</font>");
					wrongLetters += 1;
				}
		targetWordIndex+=1;	
				
	}	
	
	enteredWord = String.valueOf(s);
	//display word
	String typedText = "";
	for(int i = 0; i < enteredWordHTML.size(); i+=1) {
		typedText += enteredWordHTML.get(i);
	}
	enteredWordLabel.setText(Html.fromHtml(typedText));
			
			//Compute error percentage
			totalLetters = correctLetters + wrongLetters;
			updateError();
			
			//Check for word completed
			if(targetWordIndex >= targetWord.length()) {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(enteredWordLabel.getWindowToken(), 0);
				enteredWordLabel.startAnimation(completedAnimationSet);
				//defined in onCreate
				/*
				public void onAnimationEnd(Animation anim)
	            {
	            	setupGame();
					completedWords++;
	            };
	            */
			}
	}
	

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		//DO NOTHING
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
		
	}
	
}
