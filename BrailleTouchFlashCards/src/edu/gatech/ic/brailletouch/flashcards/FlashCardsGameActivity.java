package edu.gatech.ic.brailletouch.flashcards;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class FlashCardsGameActivity extends Activity implements
		ViewSwitcher.ViewFactory, OnTouchListener, OnClickListener {
	private Button hintButton;
	private TextSwitcher letterSwitcher;
	private TextView numCorrect;
	private TextView numSeconds;

	private Random r;
	private HintTimer hintTimer;
	
	private boolean isGameActive;
	private int correctCount;
	private String currentLetter;

	private static final int GAME_LENGTH_MS = 60000;
	private static final int HINT_DELAY_MS = 3000;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		r = new Random();

		setContentView(R.layout.game);

		hintButton = (Button) findViewById(R.id.hint_button);
		letterSwitcher = (TextSwitcher) findViewById(R.id.letter_switcher);
		numCorrect = (TextView) findViewById(R.id.num_correct);
		numSeconds = (TextView) findViewById(R.id.seconds_left);

		hintButton.setOnClickListener(this);
		
		letterSwitcher.setFactory(this);
		letterSwitcher.setInAnimation(inFromRightAnimation);
		letterSwitcher.setOutAnimation(outToLeftAnimation);
		letterSwitcher.setOnTouchListener(this);
		
		hintTimer = new HintTimer(HINT_DELAY_MS, HINT_DELAY_MS);

		startGame();
	}

	private void startGame() {
		isGameActive = true;
		correctCount = 0;
		changeLetter();
				
		new CountDownTimer(GAME_LENGTH_MS, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				numSeconds.setText("" + millisUntilFinished / 1000);
			}

			@Override
			public void onFinish() {
				numSeconds.setText("0");
				isGameActive = false;
				// TODO: do something here because the game is over
			}
		}.start();
	}
	
	private void changeLetter() {
		String newLetter;
		do {
			newLetter = getRandomLetter();
		} while (newLetter.equalsIgnoreCase(currentLetter));
		
		hintTimer.cancel();
		hintButton.setEnabled(false);
		hintTimer.start();
		
		currentLetter = newLetter;
		letterSwitcher.setText(newLetter);
	}
	
	private void handleInput(String input) {
		// TODO: provide some more visual feedback
		if (isGameActive) {
			if (input.equalsIgnoreCase(currentLetter)) {
				numCorrect.setText(String.valueOf(++correctCount));
				changeLetter();
			} else {
				
			}
		}
	}
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private String getRandomLetter() {
		int index = r.nextInt(LETTERS.length());
		return LETTERS.substring(index, index + 1);
	}

	private static final Animation inFromRightAnimation;
	
	static {
		AnimationSet animation = new AnimationSet(true);

		Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(500);

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());

		animation.addAnimation(fadeIn);
		animation.addAnimation(inFromRight);

		inFromRightAnimation = animation;
	}

	private final static Animation outToLeftAnimation;
	
	static {
		AnimationSet animation = new AnimationSet(true);

		Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(500);

		Animation outToLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outToLeft.setDuration(500);
		outToLeft.setInterpolator(new AccelerateInterpolator());

		animation.addAnimation(fadeOut);
		animation.addAnimation(outToLeft);

		outToLeftAnimation = animation;
	}
	
	@Override
	public View makeView() {
		TextView t = new TextView(this);
		t.setGravity(Gravity.CENTER);
		t.setShadowLayer(4.0f, 0, 0, Color.parseColor("#444444"));
		t.setTextColor(Color.BLUE);
		t.setTextSize(200);
		return t;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO: get rid of this hack
		
		String fakeInput = "";
		
		if (arg1.getX() < arg0.getWidth() / 2) {
			fakeInput = currentLetter;
		}
		
		handleInput(fakeInput);

		return false;
	}
	
	private class HintTimer extends CountDownTimer {
		public HintTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// noop
		}
		
		@Override
		public void onFinish() {
			hintButton.setEnabled(true);
		}
	}

	@Override
	public void onClick(View view) {
		if (view == hintButton) {
			// TODO: show a real hint instead
			Toast.makeText(getBaseContext(), "Hint!", Toast.LENGTH_SHORT).show();
		}
	}
}
