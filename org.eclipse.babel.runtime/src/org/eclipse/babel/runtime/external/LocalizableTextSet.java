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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class LocalizableTextSet implements ILocalizableTextSet {

	/**
	 * All messages added to this object must come from bundles that have the same
	 * locale.  This is the locale of messages in this collection.
	 */
	private Locale locale;
	
	protected Map<Object, LocalizedTextInput> localizableTextCollection = new HashMap<Object, LocalizedTextInput>();	
	protected ArrayList<Object> controlOrder = new ArrayList<Object>();	

	public LocalizableTextSet() {
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

	public void layout() {
		// This default implementation does nothing.
		// Override this method to re-calculate layouts that may
		// be affected by changes to the text values in this set.
	}
	
	public void associate(Object controlKey, LocalizedTextInput textInput) {
		textInput.getLocalizedTextObject().validateLocale(locale);
		
		if (!controlOrder.contains(controlKey)) {
			controlOrder.add(controlKey);
		}
		localizableTextCollection.put(controlKey, textInput);
	}

	public void associate(final Label label, ILocalizationText localizableText) {
		associate(label, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				label.setText(text);
			}
		}); 
	}

	public void associate(final Button button, ILocalizationText localizableText) {
		associate(button, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				button.setText(text);
			}
		}); 
	}

	public void associateToolTip(final Button button, ILocalizationText localizableText) {
		associate(button, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				button.setToolTipText(text);
			}
		}); 
	}

	public void associate(final Item item, ILocalizationText localizableText) {
		associate(item, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				item.setText(text);
			}
		}); 
	}

	public void associate(final Shell shell, ILocalizationText localizableText) {
		associate(shell, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				shell.setText(text);
			}
		}); 
	}

	public void associate(final TabItem tabItem, ILocalizationText localizableText) {
		associate(tabItem, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				tabItem.setText(text);
			}
		}); 
	}

	public void associate(final ScrolledForm form, ILocalizationText localizableText) {
		associate(form, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				form.setText(text);
			}
		}); 
	}

	public void associate(final Section section, ILocalizationText localizableText) {
		associate(section, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				section.setText(text);
			}
		}); 
	}

	public void associateDescription(final Section section, ILocalizationText localizableText) {
		associate(section, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				section.setDescription(text);
			}
		}); 
	}

//	public void associate(
//			FormPage formPage, ILocalizationText localizableText) {
//		associate(formPage, new LocalizedTextInput(localizableText) {
//			@Override
//			public void updateControl(String text) {
//				formPage.setPartName(text);
//			}
//		}); 
//	}

	public Locale getLocale() {
		return locale;
	}

	/**
	 * This method should be used to register translatable text that does
	 * not appear directly in the part/dialog.  For example, text that appears
	 * in a message dialog.  Such text does not have to be updated in the controls
	 * so long as it is re-read every time it is about to be displayed.
	 *  
	 * @param localizableText
	 */
	public void associate(ILocalizationText localizableText) {
		associate(new Object(), new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				// do nothing
			}
		}); 
	}

	/**
	 * This method should be used to register translatable text that does
	 * not appear directly in the part/dialog.  For example, text that appears
	 * in a tooltip.  This method should be used only if the tooltip is built each
	 * time, such as a tooltip in a table.
	 * 
	 * Although every cell in a column has a different tooltip, they all have tooltips
	 * built in the same way from the same templates.  Therefore the tooltips for a column
	 * should appear just once.  However, we update the sample data so that the user
	 * always sees the last toolip shown.
	 *  
	 * @param localizableText
	 */
	public void associate2(Object control, ILocalizationText localizableText) {
		associate(control, new LocalizedTextInput(localizableText) {
			@Override
			public void updateControl(String text) {
				// do nothing
			}
		}); 
	}
}
