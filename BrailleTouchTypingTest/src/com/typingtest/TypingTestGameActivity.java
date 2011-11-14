package com.typingtest;

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
	
	private TextView timerLabel, enteredWordLabel;
	private EditText enteredWord;
	private Handler timerHandler = new Handler();
	String targetWord;
	String correctWord = "";
	int targetWordIndex = 0;
	private long startTime = 0;
	
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
        
        final TextView errorPercentage = (TextView) findViewById(R.id.errorPercentage);
        errorPercentage.setText("Error Percentage: ");
        
        final String words[] = getResources().getStringArray(R.array.db);
        int random = 0 + (int)(Math.random()*words.length);
        targetWord = words[random];
        
        final TextView targetWordView = (TextView) findViewById(R.id.targetWord);
        targetWordView.setText(targetWord);
        
        enteredWord = (EditText) findViewById(R.id.enteredWord);
        enteredWord.requestFocus();
        enteredWord.addTextChangedListener(this);
    	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	startTimer();
    }
    
    private void startTimer() {
		startTime = System.currentTimeMillis();
		timerHandler.removeCallbacks(updateTimer);
		timerHandler.postDelayed(updateTimer, 100);
    }
    
    /**
     * Called when a character is entered.
     */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String typed = ""+s.charAt(count-1);
		Toast.makeText(this, typed, Toast.LENGTH_SHORT).show();
		String typedText = "";
		
		if(typed.equalsIgnoreCase(String.valueOf(targetWord.charAt(targetWordIndex)))) {
			correctWord += typed;
			typedText = "<font color=#00ff00>" + correctWord + "</font>";
			targetWordIndex++;
		} else {
			typedText = "<font color=#00ff00>" + correctWord + "</font><font color=#ff0000>" + typed + "</font>";
		}
		enteredWordLabel.setText(Html.fromHtml(typedText));
		//TODO compare with target word and do some stuff
	}
	@Override
	public void afterTextChanged(Editable s) {
		//DO NOTHING
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		//DO NOTHING
	}
}
