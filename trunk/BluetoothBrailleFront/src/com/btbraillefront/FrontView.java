package com.btbraillefront;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class FrontView extends FrameLayout {
	
	Button setupButton;
	InputMethodService controller;
	
	public FrontView(Context context) {
		super(context);
		
		controller = (InputMethodService) context;
		
		LayoutInflater.from(context).inflate(R.layout.front_view, this);
		
	}

	
}
