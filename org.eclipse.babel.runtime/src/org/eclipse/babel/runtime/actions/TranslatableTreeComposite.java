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

import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.runtime.Activator;
import org.eclipse.babel.runtime.Messages;
import org.eclipse.babel.runtime.TranslatableMenuItem;
import org.eclipse.babel.runtime.external.ILocalizationText;
import org.eclipse.babel.runtime.external.LocalizableText;
import org.eclipse.babel.runtime.external.LocalizableTextSet;
import org.eclipse.babel.runtime.external.UpdatableResourceBundle;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TranslatableTreeComposite extends Composite {

	private Button revertButton;

	private TreeViewerFocusCellManager focusCellManager;

	public TranslatableTreeComposite(Composite parent, ITreeContentProvider contentProvider, Object input, LocalizableTextSet languageSet, Set<UpdatableResourceBundle> updatedBundles) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(1, false));
		
		final TreeViewer viewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(contentProvider);

		createTreeColumns(viewer, languageSet, updatedBundles);

		viewer.setInput(input);

		// Turn on the header and the lines
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);

		ColumnViewerToolTipSupport.enableFor(viewer);

		createButtonsSection(this, viewer, languageSet, updatedBundles).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	/**
	 * @param tv
	 * @return
	 */
	private void createTreeColumns(final TreeViewer tv, final LocalizableTextSet languageSet, final Set<UpdatableResourceBundle> updatedBundles) {
		focusCellManager = new TreeViewerFocusCellManager(tv, new FocusCellOwnerDrawHighlighter(tv));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tv) {
			@Override
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
				|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
				|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
				|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TreeViewerEditor.create(tv, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL 
				| ColumnViewerEditor.KEYBOARD_ACTIVATION);


		// Add the columns

		final TreeViewerColumn columnKey = new TreeViewerColumn(tv, SWT.LEFT);
		columnKey.getColumn().setWidth(80);
		languageSet.associate(columnKey.getColumn(), Activator.getLocalizableText("LocalizeDialog.keyColumnHeader")); 
		columnKey.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof TranslatableMenuItem) {
					element = ((TranslatableMenuItem)element).getLocalizableText();
				}

				if (element instanceof LocalizableText) {
					return Activator.getImage("icons/localizable.gif"); //$NON-NLS-1$
				} else {
					return Activator.getImage("icons/nonLocalizable.gif"); //$NON-NLS-1$
				}
			}

			@Override
			public String getToolTipText(Object element) {
				ILocalizationText text = null;
				if (element instanceof ILocalizationText) {
					text = (ILocalizationText)element;
				} else if (element instanceof IAdaptable) {
					text = (ILocalizationText)((IAdaptable)element).getAdapter(ILocalizationText.class);
				}
				if (text instanceof LocalizableText) {
					LocalizableText localizableText = (LocalizableText)text;
					ILocalizationText tooltipLocalizableText = localizableText.getTooltip();
					languageSet.associate2(columnKey, tooltipLocalizableText);
					return tooltipLocalizableText.getLocalizedText();
				} else {
					return null;
				}
			}

		});

		Locale rootLocale = new Locale("", "", ""); 
		createLocaleColumn(tv, updatedBundles, rootLocale, null);
		String languageCode = Locale.getDefault().getLanguage();
		if (languageCode.length() != 0) {
			Locale languageLocale = new Locale(languageCode, "", ""); 
			createLocaleColumn(tv, updatedBundles, languageLocale, rootLocale);

			String countryCode = Locale.getDefault().getCountry();
			if (countryCode.length() != 0) {
				Locale countryLocale = new Locale(languageCode, countryCode, ""); 
				createLocaleColumn(tv, updatedBundles, countryLocale, languageLocale);

				String variantCode = Locale.getDefault().getVariant();
				if (variantCode.length() != 0) {
					Locale variantLocale = new Locale(languageCode, countryCode, variantCode); 
					createLocaleColumn(tv, updatedBundles, variantLocale, countryLocale);
				}
			}
		}
	}

	private void createLocaleColumn(final TreeViewer tv,
			final Set<UpdatableResourceBundle> updatedBundles,
			final Locale locale, final Locale previousLocale) {
		TreeViewerColumn column = new TreeViewerColumn(tv, SWT.LEFT);
		column.getColumn().setWidth(150);
		column.getColumn().setText(locale.getDisplayName());
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof TranslatableMenuItem) {
					element = ((TranslatableMenuItem)element).getLocalizableText();
				}

				ILocalizationText text = (ILocalizationText)element;
				String message = text.getLocalizedText(locale);
				if (previousLocale == null) {
					return message;
				} else {
					String fallbackMessage = text.getLocalizedText(previousLocale);
					return (message.equals(fallbackMessage)) ? "" : message; //$NON-NLS-1$
				}
			}
		});

		column.setEditingSupport(new EditingSupport(tv) {
			@Override
			protected boolean canEdit(Object element) {
				if (element instanceof TranslatableMenuItem) {
					element = ((TranslatableMenuItem)element).getLocalizableText();
				}

				return element instanceof LocalizableText;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(tv.getTree());
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof TranslatableMenuItem) {
					element = ((TranslatableMenuItem)element).getLocalizableText();
				}

				// The text cell editor requires that null is never returned
				// by this method.
				ILocalizationText text = (ILocalizationText)element;
				String message = text.getLocalizedText(locale);
				if (previousLocale == null) {
					return message;
				} else {
					String fallbackMessage = text.getLocalizedText(previousLocale);
					return (message.equals(fallbackMessage)) ? "" : message; //$NON-NLS-1$
				}
			}

			@Override
			protected void setValue(Object element, Object value) {
				ILocalizationText localizableText;

				if (element instanceof TranslatableMenuItem) {
					localizableText = ((TranslatableMenuItem)element).getLocalizableText();
				} else {
					localizableText = (ILocalizationText)element;
				}

				String text = (String)value;

				/*
				 * If the text is all white space then we assume that the user
				 * is clearing out the locale override.  The text would then be
				 * obtained by looking to the parent locale.  For example, if the
				 * user was using Canadian English and saw "colour", but he then blanked
				 * that out, then "color" would be used for Canadian locales.
				 * 
				 * Note this means that an entry must be placed in the delta properties
				 * file for Canadian English.  "Colour" would be in the original (immutable)
				 * properties file, and so we need an entry in the delta file to say that
				 * we should ignore that and use whatever might be used for US-English.
				 * We should not simply put the current US-English in the file because then 
				 * we would not pick up future changes to the US-English.    
				 * 
				 * We never allow the user to set text to be blank.  If the original
				 * developer displayed a message, then a message must be displayed,
				 * regardless of the language.  It is conceivable that this could be
				 * a problem in very specific circumstances.  Suppose a developer uses
				 * a message to be the suffix that is appended to a word to make it plural.
				 * In English the text for most words would be "s".  In another language
				 * the word may be the same in the singular and plural so would be the empty
				 * string.  This is, however, not a good example, because that would be bad
				 * localization.  So this is probably not a problem.
				 * 
				 * This is a restriction caused by the UI design of this dialog.  The resource
				 * bundle implementation would allow an empty string to be passed
				 * and that would result in the user seeing a blank string.  Null, on the
				 * other hand, results in the value from the parent locale being used.
				 */

				text = text.trim();
				if (text.length() == 0) {
					// Setting null means use value from parent locale.
					text = null;
				}

				// If it's editable, it's localizable
				((LocalizableText)localizableText).setLocalizedText(locale, text, updatedBundles);
				tv.update(element, null);
			}
		});
		
		column.getColumn().setData(locale);
	}

	
	private Control createButtonsSection(Composite parent, final TreeViewer viewer, LocalizableTextSet languageSet, final Set<UpdatableResourceBundle> updatedBundles) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		revertButton = new Button(container, SWT.PUSH);
		languageSet.associate(revertButton, Messages.LocalizeDialog_CommandLabel_Revert);
		languageSet.associateToolTip(revertButton, Messages.LocalizeDialog_CommandTooltip_Revert);
		GridData gd = new GridData();
		gd.horizontalIndent = 20;
		revertButton.setLayoutData(gd);

		revertButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int columnIndex = focusCellManager.getFocusCell().getColumnIndex();
				Object element = focusCellManager.getFocusCell().getElement();
				ILocalizationText localizableText;
				if (element instanceof TranslatableMenuItem) {
					localizableText = ((TranslatableMenuItem)element).getLocalizableText();
				} else if (element instanceof ILocalizationText) {
					localizableText = (ILocalizationText)element;
				} else {
					System.out.println("something wrong"); //$NON-NLS-1$
					return;
				}
				
				Locale locale = (Locale)viewer.getTree().getColumn(columnIndex).getData();
				
				// If this button is enabled, the text should be revertable.
				((LocalizableText)localizableText).revertLocalizedText(locale, updatedBundles);
				viewer.update(element, null);
				
			}
		});
		return container;
	}
}
