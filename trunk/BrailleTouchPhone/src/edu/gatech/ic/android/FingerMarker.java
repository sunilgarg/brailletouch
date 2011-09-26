package edu.gatech.ic.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class FingerMarker extends View {

	private static final int DEFAULT_RADIUS = 40;
	private static final int DEFAULT_TEXT_SIZE = 40; 
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	
	private final float x;
	private final float y;
	private final int color;
	private final String fingerIdentifier;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public FingerMarker(Context context, float x, float y, int color, String fingerIdentifier) {
		super(context);
		
		this.x = x;
		this.y = y;
		this.color = color;
		this.fingerIdentifier = fingerIdentifier;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    
	    paint.setColor(color);
	    canvas.drawCircle(x, y, DEFAULT_RADIUS, paint);
	    
	    paint.setColor(DEFAULT_TEXT_COLOR);
	    paint.setTextAlign(Paint.Align.CENTER);
	    paint.setTextSize(DEFAULT_TEXT_SIZE);
	    canvas.drawText(fingerIdentifier, x, y + (DEFAULT_TEXT_SIZE / 4) + 1, paint);
	}

}
