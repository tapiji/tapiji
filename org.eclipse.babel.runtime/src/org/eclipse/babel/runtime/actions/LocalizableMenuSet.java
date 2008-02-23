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

package org.eclipse.babel.runtime.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.babel.runtime.external.ILocalizableTextSet;
import org.eclipse.babel.runtime.external.ILocalizationText;
import org.eclipse.babel.runtime.external.LocalizedTextInput;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.menus.CommandContributionItem;

public class LocalizableMenuSet implements ILocalizableTextSet {

	/**
	 * All messages added to this object must come from bundles that have the same
	 * locale.  This is the locale of messages in this collection.
	 */
	private Locale locale;
	
	protected Map<Object, LocalizedTextInput> localizableTextCollection = new HashMap<Object, LocalizedTextInput>();	
	protected ArrayList<Object> controlOrder = new ArrayList<Object>();	

	public LocalizableMenuSet() {
		this.locale = Locale.getDefault();
	}
	
	public LocalizedTextInput[] getLocalizedTexts() {
		/*
		 * We need to get the values from the map, but return them in the order
		 * in which the controls were originally added. This ensures a more
		 * sensible order.
		 */
		LocalizedTextInput[] result = new LocalizedTextInput[controlOrder.size()];
		int i = 0;
		for (Object controlKey: controlOrder) {
			result[i++] = localizableTextCollection.get(controlKey); 
		}
		return result;
	}

	public void associate(Object controlKey, LocalizedTextInput textInput) {
		textInput.getLocalizedTextObject().validateLocale(locale);
		
		if (!controlOrder.contains(controlKey)) {
			controlOrder.add(controlKey);
		}
		localizableTextCollection.put(controlKey, textInput);
	}

	public void associate(final MenuItem label, ILocalizationText localizableText) {
		associate(label, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				label.setText(text);
			}
		}); 
	}

	public Locale getLocale() {
		return locale;
	}

	public void associate(final ActionContributionItem pluginAction,
			ILocalizationText localizableText) {
		associate(pluginAction, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				pluginAction.getAction().setText(text);
				pluginAction.update();
			}
		}); 
	}

	public void associate(final CommandContributionItem pluginAction,
			ILocalizationText localizableText) {
		associate(pluginAction, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				// TODO: Don't know how to do this.  The setText method
				// is private.
//				pluginAction.setText(text);
				pluginAction.update();
			}
		}); 
	}

	public void layout() {
		// No layouts are needed on menus.
	}
}
