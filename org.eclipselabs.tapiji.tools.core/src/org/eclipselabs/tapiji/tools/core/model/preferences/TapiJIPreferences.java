package org.eclipselabs.tapiji.tools.core.model.preferences;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipselabs.tapiji.tools.core.Activator;

public class TapiJIPreferences {

	public static final String AUDIT_SAME_VALUE = "auditSameValue";
	public static final String AUDIT_UNSPEZIFIED_KEY = "auditMissingValue";
	public static final String AUDIT_MISSING_LANGUAGE = "auditMissingLanguage";
	public static final String AUDIT_RB = "auditResourceBundle";
	public static final String AUDIT_RESOURCE = "auditResource";
	
	public static final String NON_RB_PATTERN = "NoRBPattern";
	
	private static final IPreferenceStore PREF = Activator.getDefault().getPreferenceStore();
	
	private static final String DELIMITER = ";";
	private static final String ATTRIBUTE_DELIMITER = ":";
	
	
	public static boolean getAuditSameValue() {
		return PREF.getBoolean(AUDIT_SAME_VALUE);
	}


	public static boolean getAuditMissingValue() {
		return PREF.getBoolean(AUDIT_UNSPEZIFIED_KEY);
	}


	public static boolean getAuditMissingLanguage() {
		return PREF.getBoolean(AUDIT_MISSING_LANGUAGE);
	}


	public static boolean getAuditRb() {
		return PREF.getBoolean(AUDIT_RB);
	}


	public static boolean getAuditResource() {
		return PREF.getBoolean(AUDIT_RESOURCE);
	}


	public static List<CheckItem> getNonRbPattern() {
		return convertStringToList(PREF.getString(NON_RB_PATTERN));
	}

	public static List<CheckItem> convertStringToList(String string) {
		StringTokenizer tokenizer = new StringTokenizer(string, DELIMITER);
		int tokenCount = tokenizer.countTokens();
		List<CheckItem> elements = new LinkedList<CheckItem>();

		for (int i = 0; i < tokenCount; i++) {
			StringTokenizer attribute = new StringTokenizer(tokenizer.nextToken(), ATTRIBUTE_DELIMITER);
			String name = attribute.nextToken();
			boolean checked;
			if (attribute.nextToken().equals("true")) checked = true;
			else checked = false;
			
			elements.add(new CheckItem(name, checked));
		}
		return elements;
	}


	public static String convertListToString(List<CheckItem> patterns) {
		StringBuilder sb = new StringBuilder();
		int tokenCount = 0;
		
		for (CheckItem s : patterns){
			sb.append(s.getName());
			sb.append(ATTRIBUTE_DELIMITER);
			if(s.checked) sb.append("true");
			else sb.append("false");
			
			if (++tokenCount!=patterns.size())
				sb.append(DELIMITER);
		}
		return sb.toString();
	}
	
	
	public static void addPropertyChangeListener(IPropertyChangeListener listener){
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(listener);
	}


	public static void removePropertyChangeListener(IPropertyChangeListener listener) {
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(listener);
	}
}