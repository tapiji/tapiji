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
package org.eclipselabs.tapiji.translator.suggestionprovider.mymemory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.tapiji.translator.suggestionprovider.mymemory.MyMemoryProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyMemoryProviderTest {

	private MyMemoryProvider mp;
	private String originalText = "Instances of this class are"
			+ " selectable user interface objects";

	private String translatedText = "Instanzen dieser Klasse sind"
			+ " w\u00e4hlbar Objekte der Benutzeroberfl\u00e4che";

	private String targetLanguage = "de";

	private static final String ICON_PATH = "/icons/mymemo16.png";
	private Image icon = new Image(Display.getCurrent(),
			MyMemoryProvider.class.getResourceAsStream(ICON_PATH));

	@Before
	public void setUp() throws Exception {
		mp = new MyMemoryProvider();
	}

	@After
	public void tearDown() throws Exception {
		mp = null;
	}

	@Test
	public void testgetSuggestion() {
		Suggestion actual = null;

		try {
			actual = mp.getSuggestion(originalText, targetLanguage);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		Suggestion expected = new Suggestion(icon, translatedText, mp);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());
	}

	@Test
	public void testGetSuggestionWithTargetLanguageInUpperCase() {
		Suggestion actual = null;

		try {
			actual = mp.getSuggestion(originalText, "DE");
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(icon, translatedText, mp);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());
	}

	@Test
	public void testGetSuggestionWithWrongLanguage() {
		Suggestion actual = null;
		try {
			actual = mp.getSuggestion(originalText, "--");
		} catch (Exception e) {
			fail();
		}

		assertNotNull(actual);
		assertEquals(SuggestionErrors.LANG_NOT_SUPPORT_ERR, actual.getText());
	}

	@Test
	public void testGetSuggestionWithNullParameter() {
		Suggestion actual = null;

		try {
			actual = mp.getSuggestion(null, null);
		} catch (Exception e) {
			fail();
		}

		assertNotNull(actual);
		assertEquals(SuggestionErrors.NO_SUGESTION_ERR, actual.getText());
	}

	@Test
	public void testGetSuggestionInvalidParameter() {
		Suggestion actual = null;

		try {
			actual = mp.getSuggestion("", "");
		} catch (Exception e) {
			fail();
		}

		assertNotNull(actual);
		assertEquals(SuggestionErrors.NO_SUGESTION_ERR, actual.getText());
	}
}
