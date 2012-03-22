package org.eclipselabs.tapiji.tools.core.model.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipselabs.tapiji.tools.core.Activator;

public class TapiJIPreferenceInitializer extends AbstractPreferenceInitializer {

	public TapiJIPreferenceInitializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		//ResourceBundle-Settings
		List<CheckItem> patterns = new LinkedList<CheckItem>();
		patterns.add(new CheckItem("^(.)*/build\\.properties", true));
		patterns.add(new CheckItem("^(.)*/config\\.properties", true));
		patterns.add(new CheckItem("^(.)*/targetplatform/(.)*", true));
		prefs.setDefault(TapiJIPreferences.NON_RB_PATTERN, TapiJIPreferences.convertListToString(patterns));
		
		// Builder
		prefs.setDefault(TapiJIPreferences.AUDIT_RESOURCE, true);
		prefs.setDefault(TapiJIPreferences.AUDIT_RB, true);
		prefs.setDefault(TapiJIPreferences.AUDIT_UNSPEZIFIED_KEY, true);
		prefs.setDefault(TapiJIPreferences.AUDIT_SAME_VALUE, false);
		prefs.setDefault(TapiJIPreferences.AUDIT_MISSING_LANGUAGE, true);
	}
	
}
