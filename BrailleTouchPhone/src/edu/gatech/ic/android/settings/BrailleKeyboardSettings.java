package edu.gatech.ic.android.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import edu.gatech.ic.android.R;

public class BrailleKeyboardSettings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}

}
