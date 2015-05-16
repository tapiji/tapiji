/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.editor.ui.widget;


import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.utils.UiUtils;


/**
 * Composite for dynamically selecting a locale from a list of available
 * locales.
 *
 * @author Pascal Essiembre (pascal@essiembre.com)
 * @author Christian Behon
 */
public final class LocaleSelector extends Composite implements FocusListener, SelectionListener {

    private static final String DEFAULT_LOCALE = "[ DEFAULT ]"; // TODO
    private static final String TAG = LocaleSelector.class.getSimpleName();

    public enum Input {
        LANGUAGE,
        COUNTRY,
        VARIANT
    }

    private final Locale[] availableLocales;
    private final Combo localesCombo;
    private final Text langText;
    private final Text countryText;
    private final Text variantText;

    /**
     * Constructor.
     *
     * @param parent parent composite
     */
    public LocaleSelector(final Composite parent) {
        super(parent, SWT.NONE);

        availableLocales = Locale.getAvailableLocales();
        Arrays.sort(availableLocales, localeComperator);

        GridLayout layout = new GridLayout();
        setLayout(layout);
        layout.numColumns = 1;
        layout.verticalSpacing = 20;

        final Group selectionGroup = new Group(this, SWT.NULL);
        layout = new GridLayout(3, false);
        selectionGroup.setLayout(layout);
        selectionGroup.setText("selector.title");
        selectionGroup.setText("");

        localesCombo = createLocaleComboBox(selectionGroup);

        langText = createText(Input.LANGUAGE, selectionGroup, 3);
        countryText = createText(Input.COUNTRY, selectionGroup, 2);
        variantText = createText(Input.VARIANT, selectionGroup, 50);

        createLabel(selectionGroup, "selector.language");
        createLabel(selectionGroup, "selector.country");
        createLabel(selectionGroup, "selector.variant");
    }

    private Combo createLocaleComboBox(final Group selectionGroup) {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        final Combo comboBox = new Combo(selectionGroup, SWT.READ_ONLY);
        comboBox.setLayoutData(gd);
        comboBox.add(DEFAULT_LOCALE);
        for (final Locale availableLocale : availableLocales) {
            comboBox.add(availableLocale.getDisplayName());
        }
        comboBox.addSelectionListener(this);
        return comboBox;
    }

    private Text createText(final Input inputId, final Group selectionGroup, final int textLimit) {
        final GridData gridData = new GridData();
        final Text text = new Text(selectionGroup, SWT.BORDER);
        text.setData(inputId.toString());
        gridData.widthHint = UiUtils.getWidthInChars(text, 4);
        text.setTextLimit(textLimit);
        text.setLayoutData(gridData);
        text.addFocusListener(this);
        return text;
    }

    private void createLabel(final Group selectionGroup, final String message) {
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        final Label lblVariant = new Label(selectionGroup, SWT.NULL);
        //lblVariant.setText(message); // TODO
        lblVariant.setLayoutData(gridData);
    }

    @Override
    public void focusGained(final FocusEvent e) {

    }

    @Override
    public void focusLost(final FocusEvent focusEvent) {
        Log.d(TAG, "LANGUAGE" + ((Text) focusEvent.getSource()).getData());
        if (focusEvent.getSource() instanceof Text) {
            final String inputId = (String) ((Text) focusEvent.getSource()).getData();
            switch (Input.valueOf(inputId)) {
                case LANGUAGE:
                    langText.setText(langText.getText().toLowerCase());
                    break;
                case COUNTRY:
                    countryText.setText(countryText.getText().toUpperCase());
                    break;
                default:
                    break;
            }
            setLocaleOnlocalesCombo();
        }
    }

    /**
     * Gets the selected locale. Default locale is represented by a <code>null</code> value.
     *
     * @return selected locale
     */
    public Locale getSelectedLocale() {
        final String lang = langText.getText().trim();
        final String country = countryText.getText().trim();
        final String variant = variantText.getText().trim();

        if (lang.length() > 0 && country.length() > 0 && variant.length() > 0) {
            return new Locale(lang, country, variant);
        } else if (lang.length() > 0 && country.length() > 0) {
            return new Locale(lang, country);
        } else if (lang.length() > 0) {
            return new Locale(lang);
        } else {
            return null;
        }
    }

    /**
     * Sets an available locale on the available locales combo box.
     */
    private void setLocaleOnlocalesCombo() {
        final Locale locale = new Locale(langText.getText(), countryText.getText(), variantText.getText());
        int index = -1;
        for (int i = 0; i < availableLocales.length; i++) {
            final Locale availLocale = availableLocales[i];
            if (availLocale.equals(locale)) {
                index = i + 1;
            }
        }
        if (index >= 1) {
            localesCombo.select(index);
        } else {
            localesCombo.clearSelection();
        }
    }

    /**
     * Adds a modify listener.
     *
     * @param listener modify listener
     */
    public void addModifyListener(final ModifyListener listener) {
        langText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                listener.modifyText(e);
            }
        });
        countryText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                listener.modifyText(e);
            }
        });
        variantText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                listener.modifyText(e);
            }
        });
    }

    @Override
    public void widgetSelected(final SelectionEvent selectionEvent) {
        final int index = localesCombo.getSelectionIndex();
        if (index == 0) { // default
            langText.setText(""); //$NON-NLS-1$
            countryText.setText(""); //$NON-NLS-1$
        } else {
            final Locale locale = availableLocales[index - 1];
            langText.setText(locale.getLanguage());
            countryText.setText(locale.getCountry());
        }
        variantText.setText(""); //$NON-NLS-1$
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
    }

    /**
     *
     */
    private static Comparator<Locale> localeComperator = new Comparator<Locale>() {

        @Override
        public int compare(final Locale locale1, final Locale locale2) {
            return Collator.getInstance().compare(locale1.getDisplayName(), locale2.getDisplayName());
        }
    };
}
