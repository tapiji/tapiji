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

import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Provides suggestions via Microsoft Translator
 * @author Samir Soyer
 *
 */
public class MicrosoftTranslatorProvider implements ISuggestionProvider {

	private static final String CLIENT_ID = "tapiji_translator";
	private static final String CLIENT_SECRET = "+aQX87s1KwVOziGL3DgAdXIQu63K0nYDS7bNkh3XuyE=";
	private Image icon;
	private static final String ICON_PATH = "/icons/mt16.png";
	private static final String QUOTA_EXCEEDED = "Quota Exceeded";

	/**
	 * Sets the {@code clientId} and {@code clientSecret} to the obtained values from
	 * Windows Azure Marketplace and creates the image from globally defined icon path
	 */
	public MicrosoftTranslatorProvider(){
		Translate.setClientId(CLIENT_ID);
		Translate.setClientSecret(CLIENT_SECRET);
		icon = new Image(Display.getCurrent(),MicrosoftTranslatorProvider.class.getResourceAsStream(ICON_PATH));
	}


	/**
	 * Connects to Microsoft Translator, translates given String form
	 * English to {@code targetLanguage}, then returns translation as Suggestion object
	 * 
	 * Supported languages and their abbreviations are:
	 * <p><blockquote><pre>
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
	 *  </pre></blockquote><p>
	 * 
	 * @param original is the original text that  is going be translated.
	 * @param targetLanguage should be in ISO 639-1 Code, e.g "de" for GERMAN.
	 * If not specified "zh-CHS" will be used for any variants of Chinese
	 * @return suggestion object
	 * 
	 * */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) throws IllegalArgumentException{
		
		if(original == null || targetLanguage == null ||
				original.equals("") || targetLanguage.equals("")){
			return new Suggestion(icon,SuggestionErrors.NO_SUGESTION_ERR);
		}
		
		targetLanguage=targetLanguage.toLowerCase();
		
		String translatedText = "";
		
		if(targetLanguage.contains("zh")){
			targetLanguage = "zh-CHS";
		}
		
		try {
			if(!Language.getLanguageCodesForTranslation().contains(targetLanguage)){
				return new Suggestion(icon,SuggestionErrors.LANG_NOT_SUPPORT_ERR);
			}
		} catch (Exception e1) {
			return new Suggestion(icon,SuggestionErrors.CONNECTION_ERR);
		}

		try {
			translatedText = Translate.execute(original, Language.AUTO_DETECT, Language.fromString(targetLanguage));
		} catch (Exception e) {
			//TODO logging 
			return new Suggestion(icon,SuggestionErrors.CONNECTION_ERR);
		}
		
		if(translatedText.toLowerCase().contains("exception")){
			return new Suggestion(icon,SuggestionErrors.NO_SUGESTION_ERR);
		}
		if(translatedText.toLowerCase().contains(QUOTA_EXCEEDED)){
			return new Suggestion(icon,SuggestionErrors.QUOTA_EXCEEDED);
		}

		return new Suggestion(icon,translatedText);
	}

}
