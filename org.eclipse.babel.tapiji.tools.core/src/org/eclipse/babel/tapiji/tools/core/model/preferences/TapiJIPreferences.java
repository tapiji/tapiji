/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.model.preferences;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.babel.core.configuration.IConfiguration;
import org.eclipse.babel.tapiji.tools.core.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

public class TapiJIPreferences implements IConfiguration {

	public static final String AUDIT_SAME_VALUE = "auditSameValue";
	public static final String AUDIT_UNSPEZIFIED_KEY = "auditMissingValue";
	public static final String AUDIT_MISSING_LANGUAGE = "auditMissingLanguage";
	public static final String AUDIT_RB = "auditResourceBundle";
	public static final String AUDIT_RESOURCE = "auditResource";
	
	public static final String NON_RB_PATTERN = "NoRBPattern";
	
	private static final IPreferenceStore PREF = Activator.getDefault().getPreferenceStore();
	
	private static final String DELIMITER = ";";
	private static final String ATTRIBUTE_DELIMITER = ":";
	
	public boolean getAuditSameValue() {
		return PREF.getBoolean(AUDIT_SAME_VALUE);
	}


	public boolean getAuditMissingValue() {
		return PREF.getBoolean(AUDIT_UNSPEZIFIED_KEY);
	}


	public boolean getAuditMissingLanguage() {
		return PREF.getBoolean(AUDIT_MISSING_LANGUAGE);
	}


	public boolean getAuditRb() {
		return PREF.getBoolean(AUDIT_RB);
	}


	public boolean getAuditResource() {
		return PREF.getBoolean(AUDIT_RESOURCE);
	}

	public String getNonRbPattern() {
		return PREF.getString(NON_RB_PATTERN);
	}
	
	public static List<CheckItem> getNonRbPatternAsList() {
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
