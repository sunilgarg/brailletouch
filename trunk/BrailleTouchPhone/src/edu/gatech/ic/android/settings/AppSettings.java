package edu.gatech.ic.android.settings;

import edu.gatech.ic.android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {

	private static final int PREF_SOUNDS = R.string.sounds_enabled_key;
	
	private static String prefSounds;
	
	private SharedPreferences prefs;
	
	public AppSettings(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		prefSounds = context.getString(PREF_SOUNDS);
	}
	
	public Boolean isSoundEnabled() {
		return prefs.getBoolean(prefSounds, true);
	}
	
}
