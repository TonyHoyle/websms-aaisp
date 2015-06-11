package uk.me.hoyle.websms.aaisp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	static final String PREFS_ENABLED = "prefEnabled";
	static final String PREFS_USERNAME = "prefUsername";
	static final String PREFS_PASSWORD = "prefPassword";
	static final String PREFS_ORIGIN = "prefOrigin";
	static final String PREFS_DELIVERY_REPORT = "prefDelivery";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
