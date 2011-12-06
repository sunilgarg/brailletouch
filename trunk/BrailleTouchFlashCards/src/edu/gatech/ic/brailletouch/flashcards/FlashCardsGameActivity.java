package edu.gatech.ic.brailletouch.flashcards;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Display;
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
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class FlashCardsGameActivity extends Activity implements
		ViewSwitcher.ViewFactory, OnTouchListener, OnClickListener {
	private Button hintButton;
	private Button startButton;
	private ViewSwitcher screenSwitcher;
	private TextSwitcher letterSwitcher;
	private TextView inputFeedbackText;
	private TextView numCorrect;
	private TextView numSeconds;
	private TextView gameStartText;
	private TextView gameStartSubtext;
	private EditText et;
	private FrameLayout hintFrame;
	private HintDot[] hintDots = new HintDot[6];
	private Animation hintAnim;
	private PowerManager.WakeLock mWakeLock;

	private Random r;
	private HintTimer hintTimer;
	
	private boolean isGameActive;
	private int correctCount;
	private String currentLetter;

	private static final boolean INPUT_HACK_ON = false;
	private static final int GAME_LENGTH_MS = 60000;
	private static final int HINT_DELAY_MS = 3000;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		mWakeLock.acquire();
		
		r = new Random();

		setContentView(R.layout.game);

		hintButton = (Button) findViewById(R.id.hint_button);
		startButton = (Button) findViewById(R.id.start_button);
		screenSwitcher = (ViewSwitcher) findViewById(R.id.screen_switcher);
		letterSwitcher = (TextSwitcher) findViewById(R.id.letter_switcher);
		inputFeedbackText = (TextView) findViewById(R.id.input_feedback_text);
		numCorrect = (TextView) findViewById(R.id.num_correct);
		numSeconds = (TextView) findViewById(R.id.seconds_left);
		gameStartText = (TextView) findViewById(R.id.game_start_text);
		gameStartSubtext = (TextView) findViewById(R.id.game_start_subtext);
		hintFrame = (FrameLayout) findViewById(R.id.hintFrame);
		et = (EditText) findViewById(R.id.et);
		//et.setVisibility(View.INVISIBLE);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		et.setFocusableInTouchMode(true);
		et.requestFocus();
		et.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void onTextChanged(CharSequence s, int start, int before, int count) { }
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		    @Override
			public void afterTextChanged(Editable s) {
		    	int len = s.length();
				if(len > 0) {
					handleInput(String.valueOf(s.charAt(len-1)));
				}
			}
		});

		hintButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		
		screenSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		screenSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		
		letterSwitcher.setFactory(this);
		letterSwitcher.setInAnimation(inFromRightAnimation);
		letterSwitcher.setOutAnimation(outToLeftAnimation);
		letterSwitcher.setOnTouchListener(this);
		
		hintTimer = new HintTimer(HINT_DELAY_MS, HINT_DELAY_MS);
		
		flashInputFeedback(); // this is a hack; for some reason it doesn't show up the first time.
		
		Display disp = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int frameWidth = disp.getWidth();
		int frameHeight = disp.getHeight();
		hintFrame.setVisibility(View.INVISIBLE);
		
		hintDots[0] = new HintDot(this,frameWidth/8,frameHeight/6,frameHeight/8);
		hintDots[1] = new HintDot(this,frameWidth/8,frameHeight/2,frameHeight/8);
		hintDots[2] = new HintDot(this,frameWidth/8,5*frameHeight/6,frameHeight/8);
		hintDots[3] = new HintDot(this,frameWidth-frameWidth/8,frameHeight/6,frameHeight/8);
		hintDots[4] = new HintDot(this,frameWidth-frameWidth/8,frameHeight/2,frameHeight/8);
		hintDots[5] = new HintDot(this,frameWidth-frameWidth/8,5*frameHeight/6,frameHeight/8);
		
		for (int i=0; i<6; i++) {
			hintDots[i].setVisibility(View.INVISIBLE);
			hintFrame.addView(hintDots[i]);
		}

		hintAnim = new AlphaAnimation(0.00f,1.00f);
        hintAnim.setDuration(2000);
        hintAnim.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            	hintFrame.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
            }
        });
        

	}

	private void startGame() {
		isGameActive = true;
		numCorrect.setText("0");
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
				gameStartText.setText(R.string.game_over);
				gameStartSubtext.setText(String.format("You got %d letter%s correct!", correctCount, correctCount == 1 ? "" : "s"));
				startButton.setText(R.string.restart);
				screenSwitcher.showPrevious();
			}
		}.start();
	}
	
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String[] HINTLAYOUTS = 
		{"0","01","03","034","04",
		"013","0134","014","13","134",
		"02","012","023","0234","024",
		"0123","01234","0124","123","1234",
		"025","0125","1345","0235","02345","0245"};

	private void changeLetter() {
		String newLetter;
		int index;
		
		hintFrame.setVisibility(View.INVISIBLE);
		for (int i=0; i<6; i++) {
			hintDots[i].setVisibility(View.INVISIBLE);
		}
		do {
			index = r.nextInt(LETTERS.length());
			newLetter = LETTERS.substring(index, index+1);
		} while (newLetter.equalsIgnoreCase(currentLetter));
		
		String hintLayout = HINTLAYOUTS[index];
		
		int dotIndex;
		for (int i=0; i<hintLayout.length(); i++) {
			dotIndex = Integer.parseInt(hintLayout.substring(i, i+1));
			hintDots[dotIndex].setVisibility(View.VISIBLE);
		}
		
		hintTimer.cancel();
		hintButton.setEnabled(false);
		hintTimer.start();
		
		currentLetter = newLetter;
		letterSwitcher.setText(newLetter);
	}
	
	private void handleInput(String input) {
		if (isGameActive) {
			if (input.equalsIgnoreCase(currentLetter)) {
				numCorrect.setText(String.valueOf(++correctCount));
				inputFeedbackText.setText("Correct!");
				inputFeedbackText.setTextColor(Color.parseColor("#008000"));
				flashInputFeedback();
				changeLetter();
			} else {
				inputFeedbackText.setText("Try Again.");
				inputFeedbackText.setTextColor(Color.parseColor("#ff0000"));
				flashInputFeedback();
			}
		}
	}

	private void flashInputFeedback() {
        inputFeedbackText.setVisibility(View.VISIBLE);
		Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setStartOffset(400);
		fadeOut.setDuration(750);
        inputFeedbackText.startAnimation(fadeOut);
        inputFeedbackText.setVisibility(View.INVISIBLE);
	}
	
	private static final Animation inFromRightAnimation;
	
	static {
		AnimationSet animation = new AnimationSet(true);

		Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(250);

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(250);
		inFromRight.setInterpolator(new AccelerateInterpolator());

		animation.addAnimation(fadeIn);
		animation.addAnimation(inFromRight);

		inFromRightAnimation = animation;
	}

	private final static Animation outToLeftAnimation;
	
	static {
		AnimationSet animation = new AnimationSet(true);

		Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(250);

		Animation outToLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outToLeft.setDuration(250);
		outToLeft.setInterpolator(new AccelerateInterpolator());

		animation.addAnimation(fadeOut);
		animation.addAnimation(outToLeft);

		outToLeftAnimation = animation;
	}
	
	@Override
	public View makeView() {
		TextView t = new TextView(this);
		t.setGravity(Gravity.CENTER);
		t.setShadowLayer(4.0f, 0, 0, Color.rgb(43, 80, 232));
		t.setTextColor(Color.BLUE);
		t.setTextSize(200);
		return t;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (INPUT_HACK_ON) {
			String fakeInput = "";
			
			if (arg1.getX() < arg0.getWidth() / 2) {
				fakeInput = currentLetter;
			}
			
			handleInput(fakeInput);
		}
		return false;
	}
	
	@Override
	public void onClick(View view) {		
		if (view == hintButton) {
			hintFrame.startAnimation(hintAnim);
			hintButton.setEnabled(false);
		} else if (view == startButton) {
			screenSwitcher.showNext();
			startGame();
		}
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
	
	private class HintDot extends View {
	    private final float x;
	    private final float y;
	    private final int r;
	    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    
	    public HintDot(Context context, float x, float y, int r) {
	        super(context);
	        mPaint.setColor(Color.argb(200,92, 0, 183));
	        this.x = x;
	        this.y = y;
	        this.r = r;
	    }
	    
	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        canvas.drawCircle(x, y, r, mPaint);
	    }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWakeLock.release();

	}
}
