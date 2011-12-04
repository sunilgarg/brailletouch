package edu.gatech.ic.brailletouch.flashcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FlashCardsHomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        final Button start = (Button) findViewById(R.id.start_button);
        start.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent game = new Intent(FlashCardsHomeActivity.this, FlashCardsGameActivity.class);
                startActivity(game);
            }
        });
    }
}