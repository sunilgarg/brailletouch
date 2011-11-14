package com.typingtest;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TypingTestGameActivity extends Activity implements TextWatcher {
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        
        final String words[] = getResources().getStringArray(R.array.db);
        int random = 0 + (int)(Math.random()*words.length);
        String targetWord = words[random];
        
        final TextView targetWordView = (TextView) findViewById(R.id.targetWord);
        targetWordView.setText(targetWord);
        
        final EditText enteredWord = (EditText) findViewById(R.id.enteredWord);
        enteredWord.requestFocus();
        enteredWord.addTextChangedListener(this);
    	this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * Called when a character is entered.
     */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Toast.makeText(this, ""+s.charAt(count-1), Toast.LENGTH_SHORT).show();
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
