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
package org.eclipselabs.tapiji.translator.suggestionprovider.microsofttranslator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.babel.editor.widgets.suggestion.exception.InvalidConfigurationSetting;
import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProviderConfigurationSetting;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Provides suggestions via Microsoft Translator
 * 
 * @author Samir Soyer
 * 
 */
public class MicrosoftTranslatorProvider implements ISuggestionProvider {

	private static final String CLIENT_ID = "tapiji_translator";
	private static final String CLIENT_SECRET =
			"+aQX87s1KwVOziGL3DgAdXIQu63K0nYDS7bNkh3XuyE=";
	private static final String ICON_PATH = "/icons/mt16.png";
	private static final String QUOTA_EXCEEDED = "Quota Exceeded";
	private static Language SOURCE_LANG;
	private Map<String, ISuggestionProviderConfigurationSetting> configSettings;
	private Image icon;

	private final Level LOG_LEVEL = Level.INFO;
	private static final Logger LOGGER = Logger
			.getLogger(MicrosoftTranslatorProvider.class.getName());

	/**
	 * Sets the {@code clientId} and {@code clientSecret} to the obtained values
	 * from Windows Azure Marketplace and creates the image from globally
	 * defined icon path
	 */
	public MicrosoftTranslatorProvider() {
		Translate.setClientId(CLIENT_ID);
		Translate.setClientSecret(CLIENT_SECRET);
		icon = new Image(Display.getCurrent(),
				MicrosoftTranslatorProvider.class
				.getResourceAsStream(ICON_PATH));
		configSettings = new HashMap<String, 
				ISuggestionProviderConfigurationSetting>();

		setSourceLanguage(System
				.getProperty("tapiji.translator.default.language"));

	}

	private void setSourceLanguage(String lang) {
		if (lang != null) {
			lang = lang.substring(0, 2);
			if (lang.contains("zh")) {
				lang = "zh-CHS";
			}
		}

		try {
			if (!Language.getLanguageCodesForTranslation().contains(lang)) {
				LOGGER.log(LOG_LEVEL, "Source language " + lang
						+ " is not supported, "
						+ "English will be used instead");
				SOURCE_LANG = Language.ENGLISH;
			} else {
				SOURCE_LANG = Language.fromString(lang);
			}
		} catch (Exception e1) {
			LOGGER.log(LOG_LEVEL, "Error while checking supported"
					+ "languages, check your internet connection,"
					+ " therefore English will be used as default (source)"
					+ "language");
			SOURCE_LANG = Language.ENGLISH;
		}
	}

	/**
	 * @return Path to the icon of this provider
	 */
	public String getIconPath() {
		return ICON_PATH;
	}

	/**
	 * Connects to Microsoft Translator, translates given String form English to
	 * {@code targetLanguage}, then returns translation as Suggestion object
	 * 
	 * Supported languages and their abbreviations are:
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * ARABIC("ar"),
	 * BULGARIAN("bg"),
	 * CATALAN("ca"),
	 * CHINESE_SIMPLIFIED("zh-CHS"),
	 * CHINESE_TRADITIONAL("zh-CHT"),
	 * CZECH("cs"),
	 * DANISH("da"),
	 * DUTCH("nl"),
	 * ENGLISH("en"),
	 * ESTONIAN("et"),
	 * FINNISH("fi"),
	 * FRENCH("fr"),
	 * GERMAN("de"),
	 * GREEK("el"),
	 * HAITIAN_CREOLE("ht"),
	 * HEBREW("he"),
	 * HINDI("hi"),
	 * HMONG_DAW("mww"),
	 * HUNGARIAN("hu"),
	 * INDONESIAN("id"),
	 * ITALIAN("it"),
	 * JAPANESE("ja"),
	 * KOREAN("ko"),
	 * LATVIAN("lv"),
	 * LITHUANIAN("lt"),
	 * MALAY("ms"),
	 * NORWEGIAN("no"),
	 * PERSIAN("fa"),
	 * POLISH("pl"),
	 * PORTUGUESE("pt"),
	 * ROMANIAN("ro"),
	 * RUSSIAN("ru"),
	 * SLOVAK("sk"),
	 * SLOVENIAN("sl"),
	 * SPANISH("es"),
	 * SWEDISH("sv"),
	 * THAI("th"),
	 * TURKISH("tr"),
	 * UKRAINIAN("uk"),
	 * URDU("ur"),
	 * VIETNAMESE("vi");
	 * </pre>
	 * 
	 * </blockquote>
	 * <p>
	 * 
	 * @param original
	 *            is the original text that is going be translated.
	 * @param targetLanguage
	 *            should be in ISO 639-1 Code, e.g "de" for GERMAN. If not
	 *            specified "zh-CHS" will be used for any variants of Chinese
	 * @return suggestion object
	 * 
	 * */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage)
			throws IllegalArgumentException {
		LOGGER.log(LOG_LEVEL, "original text: " + original
				+ ", targetLanguage: " + targetLanguage);

		if (original == null || targetLanguage == null || original.equals("")
				|| targetLanguage.equals("")) {
			return new Suggestion(icon, SuggestionErrors.NO_SUGESTION_ERR, this);
		}

		targetLanguage = targetLanguage.toLowerCase();

		String translatedText = "";

		if (targetLanguage.contains("zh")) {
			targetLanguage = "zh-CHS";
		}

		try {
			if (!Language.getLanguageCodesForTranslation().contains(
					targetLanguage)) {
				return new Suggestion(icon,
						SuggestionErrors.LANG_NOT_SUPPORT_ERR, this);
			}
		} catch (Exception e1) {
			LOGGER.log(LOG_LEVEL, "Language exception: " + e1.getMessage());
			return new Suggestion(icon, SuggestionErrors.CONNECTION_ERR, this);
		}

		try {
			translatedText = Translate.execute(original, SOURCE_LANG,
					Language.fromString(targetLanguage));
		} catch (Exception e) {
			LOGGER.log(LOG_LEVEL, "Translation exception: " + e.getMessage());
			return new Suggestion(icon, SuggestionErrors.CONNECTION_ERR, this);
		}

		if (translatedText.toLowerCase().contains("exception")) {
			return new Suggestion(icon, SuggestionErrors.NO_SUGESTION_ERR, this);
		}
		if (translatedText.toLowerCase().contains(QUOTA_EXCEEDED)) {
			return new Suggestion(icon, SuggestionErrors.QUOTA_EXCEEDED, this);
		}
		if (translatedText.toLowerCase().equals("")) {
			return new Suggestion(icon, SuggestionErrors.NO_SUGESTION_ERR, this);
		}

		return new Suggestion(icon, translatedText, this);
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
		// TODO Auto-generated method stub
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof MicrosoftTranslatorProvider) {
			MicrosoftTranslatorProvider mtp = (MicrosoftTranslatorProvider) obj;
			if (this.getIconPath().equals(mtp.getIconPath())) {
				return true;
			}
		}
		return false;
	}
}
