/*******************************************************************************
 * Copyright (c) 2013 Samir Soyer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Samir Soyer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.suggestionprovider.glossary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.babel.editor.widgets.suggestion.exception.InvalidConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProviderConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.provider.StringConfigurationSetting;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads the specified xml File, parses it and looks for matches between
 * substrings of default text and glossary entries.
 * 
 * @author Samir Soyer
 * 
 */
public class GlossarySuggestionProvider extends DefaultHandler implements
ISuggestionProvider {

	private Image icon;
	// private static final String GLOSSARY = "/glossary.xml";
	private static final String ICON_PATH = "/icons/sample.gif";
	private String glossaryPath;
	private Map<String, ISuggestionProviderConfigurationSetting> configSettings;

	private final Level LOG_LEVEL = Level.INFO;
	private static final Logger LOGGER = Logger
			.getLogger(GlossarySuggestionProvider.class.getName());

	/**
	 * Creates the image of this provider from globally defined icon path
	 */
	public GlossarySuggestionProvider() {
		this.icon = new Image(Display.getCurrent(),
				GlossarySuggestionProvider.class.getResourceAsStream(ICON_PATH));
		configSettings = new HashMap<String, ISuggestionProviderConfigurationSetting>();
		configSettings.put("glossaryFile", new StringConfigurationSetting(""));
	}

	/**
	 * Sets xml file path of glossary
	 * 
	 * @param glossaryPath
	 *            is the path of xml file to be parsed.
	 */
	public void setGlossaryFile(String glossaryPath) {
		LOGGER.log(LOG_LEVEL, "glossaryPath: " + glossaryPath);
		this.glossaryPath = glossaryPath;
	}

	/**
	 * @return Path to the icon of this provider
	 */
	public String getIconPath() {
		return ICON_PATH;
	}

	/**
	 * @see org.eclipse.babel.editor.widgets.suggestion.provider.
	 *      ISuggestionProvider#getSuggestion(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) {
		LOGGER.log(LOG_LEVEL, "original text: " + original
				+ ", targetLanguage: " + targetLanguage);

		if (original == null || targetLanguage == null || original.equals("")
				|| targetLanguage.equals("")) {
			return new Suggestion(icon, SuggestionErrors.NO_SUGESTION_ERR, this);
		}

		if (glossaryPath == null) {
			return new Suggestion(icon, SuggestionErrors.NO_GLOSSARY_FILE, this);
		}

		XMLReader xr = null;

		try {
			xr = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			LOGGER.log(LOG_LEVEL, "SAX exception: " + e.getMessage());
			return new Suggestion(icon, SuggestionErrors.INVALID_GLOSSARY, this);
		}

		XMLContentHandler handler = new XMLContentHandler(original,
				targetLanguage);
		xr.setContentHandler(handler);
		InputSource is = null;

		// is = new
		// InputSource(GlossarySuggestionProvider.class.
		// getResourceAsStream(glossaryPath));
		try {
			is = new InputSource(new FileInputStream(new File(glossaryPath)));
		} catch (FileNotFoundException e1) {
			LOGGER.log(LOG_LEVEL, "File exception: " + e1.getMessage());
			return new Suggestion(icon, SuggestionErrors.INVALID_GLOSSARY, this);
		}

		try {
			xr.parse(is);
		} catch (IOException e) {
			LOGGER.log(LOG_LEVEL, "IO exception: " + e.getMessage());
			return new Suggestion(icon, SuggestionErrors.INVALID_GLOSSARY, this);
		} catch (SAXException e) {
			LOGGER.log(LOG_LEVEL, "SAX exception: " + e.getMessage());
			return new Suggestion(icon, SuggestionErrors.INVALID_GLOSSARY, this);
		}

		String bestSuggestion = handler.getBestSuggestion();

		if (bestSuggestion.equals("")) {
			return new Suggestion(icon, SuggestionErrors.NO_SUGESTION_ERR, this);
		}

		return new Suggestion(icon, bestSuggestion, this);
	}

	@Override
	public Map<String, ISuggestionProviderConfigurationSetting> 
	getAllConfigurationSettings() {
		return configSettings;
	}

	@Override
	public void updateConfigurationSetting(String configurationId,
			ISuggestionProviderConfigurationSetting setting)
					throws InvalidConfigurationSetting {
		if (getAllConfigurationSettings().containsKey(configurationId)) {

			String config = (String) configSettings.get(configurationId)
					.getConfigurationSetting();

			if (!config.equals((String) setting.getConfigurationSetting())) {

				configSettings.put(configurationId,
						new StringConfigurationSetting(config));

				if (configurationId.equals("glossaryFile")) {
					glossaryPath = (String) setting.getConfigurationSetting();
				}

			}

		} else {
			throw new InvalidConfigurationSetting(configurationId
					+ "is not a valid configuration id");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof GlossarySuggestionProvider) {
			GlossarySuggestionProvider gsp = (GlossarySuggestionProvider) obj;
			if (this.getIconPath().equals(gsp.getIconPath())) {
				return true;
			}
		}
		return false;
	}
}

/**
 * @author Samir Soyer
 * 
 */
class XMLContentHandler extends DefaultHandler {

	private int percentage = 0;
	private boolean inID = false;
	private boolean inValue = false;
	private String id = "";
	private String value = "";
	private String defaultGlossaryEntry = "";
	private String original;
	private String targetLanguage;
	private String longestMatching = "";
	private String bestSuggestion = "";
	private String suggestion = "";

	/**
	 * @param original
	 *            is the untranslated string
	 * @param targetLanguage
	 *            is the language, to which the original text will be translated
	 */
	public XMLContentHandler(String orginal, String targetLanguage) {
		this.original = orginal;
		this.targetLanguage = targetLanguage;

	}

	/**
	 * @return best matching suggestion, which is decided by percentage of the
	 *         match between substring of default text and glossary entry.
	 */
	public String getBestSuggestion() {
		if (!bestSuggestion.equals("")) {
			return bestSuggestion + " (" + percentage + "% match)";
		}
		return bestSuggestion;
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("id")) {
			inID = true;
		}
		if (localName.equals("value")) {
			inValue = true;
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (localName.equals("translations")) {
			// original.toLowerCase().contains(defaultGlossaryEntry.toLowerCase())
			if (contains(original.toLowerCase(),
					defaultGlossaryEntry.toLowerCase())) {
				if (defaultGlossaryEntry.length() > longestMatching.length()) {

					longestMatching = defaultGlossaryEntry;

					bestSuggestion = suggestion;

					percentage = (int) ((((double) longestMatching.length()) / original
							.length()) * 100);

				}
			}
		}

		if (localName.equals("translation")) {
			id = "";
			value = "";
		}

		if (localName.equals("id")) {
			inID = false;

		}
		if (localName.equals("value")) {
			inValue = false;
		}
	}

	private boolean contains(String source, String substring) {
		String[] originalWords = source.split(" ");
		StringBuilder sb = new StringBuilder();

		for (String word : originalWords) {
			if (substring.contains(word)) {
				sb.append(word + " ");
			}
		}

		if (substring.equals(sb.toString().trim())) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (inID) {

			id = new String(ch, start, length);

			if (!value.equals("") && id.equals("Default")) {
				defaultGlossaryEntry = value;
			}

			if (id.contains(targetLanguage.substring(0, 2).toLowerCase())) {
				if (!value.equals("")) {
					suggestion = value;
				}
			}

		}
		if (inValue) {
			value = new String(ch, start, length);

			if (!id.equals("")) {
				if (id.equals("Default")) {
					defaultGlossaryEntry = value;
				}

				if (id.contains(targetLanguage.substring(0, 2).toLowerCase())) {
					suggestion = value;
				}
			}
		}
	}
}
