package com.typingtest;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TypingTestGameActivity extends Activity implements TextWatcher {
	
	private TextView timerLabel, enteredWordLabel, errorPercentage, targetWordView;
	private EditText enteredWord;
	private Handler timerHandler = new Handler();
	String targetWord;
	String correctWord;
	String words[];
	int targetWordIndex;
	float totalLetters, correctLetters, wrongLetters;
	private long startTime;
	
	//Timer
    private Runnable updateTimer = new Runnable() {
	   public void run() {
		   
	       final long start = startTime;
	       long millis = System.currentTimeMillis() - start;
	       int seconds = (int) (millis / 1000);
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
        
        //Get timer label
        timerLabel = (TextView) findViewById(R.id.timerLabel);
        enteredWordLabel = (TextView) findViewById(R.id.enteredWordLabel);
        errorPercentage = (TextView) findViewById(R.id.errorPercentage);
        targetWordView = (TextView) findViewById(R.id.targetWord);
        enteredWord = (EditText) findViewById(R.id.enteredWord);
        words = getResources().getStringArray(R.array.db);
        
        errorPercentage.setText("Error Percentage: ");
        
        //Reset game labels
        setupGame();
        
        //Reset statistics
        totalLetters = 0;
        correctLetters = 0;
        wrongLetters = 0;
        
        enteredWord.addTextChangedListener(this);
    	startTimer();
    }
    
    private void setupGame() {
    	int random = 0 + (int)(Math.random()*words.length);
        targetWord = words[random];
        targetWordView.setText(targetWord);
        enteredWordLabel.setText("");
        enteredWord.setText("");
        targetWordIndex = 0;
        correctWord = "";
    }
    
    private void startTimer() {
		startTime = System.currentTimeMillis();
		timerHandler.removeCallbacks(updateTimer);
		timerHandler.postDelayed(updateTimer, 100);
    }
    
    private void updateError() {
    	errorPercentage.setText("Percent Wrong: " + (new DecimalFormat("##").format((wrongLetters/totalLetters)*100)) + "%");
    }
    
    /**
     * Called when a character is entered.
     */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	@Override
	public void afterTextChanged(Editable s) {
		if(s.length() > 0) {
			String typed = ""+s.charAt(s.length()-1);
			Toast.makeText(this, typed, Toast.LENGTH_SHORT).show();
			String typedText = "";
			
			//Show letters typed correctly in green, incorrect letter in red
			if(typed.equalsIgnoreCase(String.valueOf(targetWord.charAt(targetWordIndex)))) {
				correctWord += typed;
				typedText = "<font color=#00ff00>" + correctWord + "</font>";
				targetWordIndex++;
				correctLetters += 1;
			} else {
				wrongLetters += 1;
				typedText = "<font color=#00ff00>" + correctWord + "</font><font color=#ff0000>" + typed + "</font>";
			}
			enteredWordLabel.setText(Html.fromHtml(typedText));
			
			//Computer error percentage
			totalLetters++;
			updateError();
			
			//Check for word completed
			if(correctWord.equalsIgnoreCase(targetWord)) {
				setupGame();
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		//DO NOTHING
	}
}
