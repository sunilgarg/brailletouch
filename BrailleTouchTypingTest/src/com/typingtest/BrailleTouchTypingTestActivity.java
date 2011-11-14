package com.typingtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BrailleTouchTypingTestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent game = new Intent(BrailleTouchTypingTestActivity.this, TypingTestGameActivity.class);
            	startActivity(game);
            }
        });
    }
}