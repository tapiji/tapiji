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
package org.eclipselabs.tapiji.translator.suggestionprovider.mymemory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Provides suggestions via MyMemory
 * @author Samir Soyer
 *
 */
public class MyMemoryProvider implements ISuggestionProvider {

	private final String URL = "http://api.mymemory.translated.net/get?q=";
	private static final String ICON_PATH = "/icons/mymemo16.png";
	private static final String QUOTA_EXCEEDED = "YOU USED ALL AVAILABLE FREE TRANSLATION FOR TODAY";
	private static final String INVALID_LANGUAGE = "IS AN INVALID TARGET LANGUAGE";
	private Image icon;
	private static String SOURCE_LANG;
	private Map<String, ISuggestionProviderConfigurationSetting> configSettings;

	private final Level LOG_LEVEL = Level.INFO;
	private static final Logger LOGGER = Logger.getLogger(MyMemoryProvider.class.getName());

	/**
	 * Creates the image from globally defined icon path
	 */
	public MyMemoryProvider() {
		this.icon = new Image(Display.getCurrent(),MyMemoryProvider.class.getResourceAsStream(ICON_PATH));
		//		this.icon = UIUtils.getImageDescriptor("mymemo16.png").createImage();
		configSettings = new HashMap<String, ISuggestionProviderConfigurationSetting>();
		
		setSourceLanguage(System.getProperty("tapiji.translator.default.language"));
	}

	/**
	 * @return Path to the icon of this provider
	 */
	public String getIconPath() {
		return ICON_PATH;
	}
	
private void setSourceLanguage(String lang){
		
		if(lang!=null){
			lang = lang.substring(0, 2);
			if(lang.contains("zh")){
				lang = "zh-CHS";
			}
			
			SOURCE_LANG = "langpair="+lang+"|";
		}else{
			LOGGER.log(LOG_LEVEL,"No source language"
					+" has benn set, " +
					"English will be used instead");
			SOURCE_LANG = "langpair=en|";
		}
	}

	/**
	 * Connects to MyMemory Translation Memory, translates given String from
	 * English to {@code targetLanguage}, then returns translation as Suggestion object
	 * @param original is the original text that is going be translated.
	 * @param targetLanguage should be in ISO 639-1 Code, e.g "de" for GERMAN.
	 * @return suggestion object
	 */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) {

		LOGGER.log(LOG_LEVEL,"original text: "+original+
				", targetLanguage: "+targetLanguage);

		if(original == null || targetLanguage == null ||
				original.equals("") || targetLanguage.equals("")){
			return new Suggestion(icon,SuggestionErrors.NO_SUGESTION_ERR, this);
		}


		//REST GET
		String langpair = SOURCE_LANG + targetLanguage.toLowerCase()
				.subSequence(0, 2);
		String restUrl = URL + original + "&"+ langpair;
		restUrl = restUrl.replaceAll("\\s", "%20");

		URL url;
		StringBuilder sb;

		try {
			url = new URL(restUrl);
		} catch (MalformedURLException e) {
			LOGGER.log(LOG_LEVEL,"url exception: "+e.getMessage());
			return new Suggestion(icon,SuggestionErrors.CONNECTION_ERR, this);
		}

		HttpURLConnection conn = null;

		try {
			conn = (HttpURLConnection) url.openConnection();


			if (conn.getResponseCode() != 200) {	
				conn.disconnect();
				LOGGER.log(LOG_LEVEL,"Http response: "+conn.getResponseCode());
				return new Suggestion(icon,SuggestionErrors.CONNECTION_ERR, this);
			}

			BufferedReader br;

			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream()), "UTF-8"));

			sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			LOGGER.log(Level.SEVERE,"rest response: "+sb.toString());
			
			br.close();
		} catch (IOException e) {
			LOGGER.log(LOG_LEVEL,"IO exception: "+e.getMessage());
			return new Suggestion(icon,SuggestionErrors.CONNECTION_ERR, this);
		}finally{
			if(conn != null)
				conn.disconnect();
		}

		JsonObject jobject;
		try {
			JsonElement jelement = new JsonParser().parse(sb.toString());
			jobject = jelement.getAsJsonObject();
			jobject = jobject.getAsJsonObject("responseData");
		} catch (Exception e) {
			LOGGER.log(LOG_LEVEL,"JSON parsing exception: "+e.getMessage());
			return new Suggestion(icon,SuggestionErrors.CONNECTION_ERR, this);
		}

		String translatedText = jobject.get("translatedText").toString().replaceAll("\"", "");
		
		LOGGER.log(LOG_LEVEL,"translatedText: "+translatedText);

		if(translatedText.contains(INVALID_LANGUAGE)){
			return new Suggestion(icon,SuggestionErrors.LANG_NOT_SUPPORT_ERR, this);
		}
		if(translatedText.contains(QUOTA_EXCEEDED)){
			return new Suggestion(icon,SuggestionErrors.QUOTA_EXCEEDED, this);
		}
		if(translatedText.equals("")){
			return new Suggestion(icon,SuggestionErrors.NO_SUGESTION_ERR, this);
		}

		return new Suggestion(icon,translatedText, this);
	}


	@Override
	public Map<String, ISuggestionProviderConfigurationSetting> getAllConfigurationSettings() {
		return configSettings;
	}


	@Override
	public void updateConfigurationSetting(String configurationId,
			ISuggestionProviderConfigurationSetting setting)
					throws InvalidConfigurationSetting {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(Object obj){

		if (this == obj) {
			return true;
		} else if (obj instanceof MyMemoryProvider) {
			MyMemoryProvider mmp = 
					(MyMemoryProvider) obj;
			if(this.getIconPath().equals(mmp.getIconPath())){
				return true;
			}
		}
		return false;
	}

}
