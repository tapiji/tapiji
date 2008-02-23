/*******************************************************************************
 * Copyright (c) 2008 Nigel Westbury and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nigel Westbury - initial API and implementation
 *******************************************************************************/

package org.eclipse.babel.runtime.external;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

public class FormattedLocalizationText implements ILocalizationText {

	ILocalizationText [] dependentText;
	
	public FormattedLocalizationText(ILocalizationText... dependentText) {
		this.dependentText = dependentText;
	}
	
	public String getLocalizedText(Locale locale) {
		String format = dependentText[0].getLocalizedText(locale);
		String args [] = new String[dependentText.length-1];
		for (int i = 0; i < args.length; i++) {
			args[i] = dependentText[i+1].getLocalizedText(locale);
		}
		return NLS.bind(format, args);
	}

	public String getLocalizedText() {
		return getLocalizedText(Locale.getDefault());
	}

	public ILocalizationText[] getDependentTexts() {
		return dependentText;
	}

	public void validateLocale(Locale locale) {
		for (ILocalizationText localizedText: dependentText) {
			localizedText.validateLocale(locale);
		}
	}
}
