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
package org.eclipselabs.tapiji.translator.suggestionprovider.glossary.test;

import static org.junit.Assert.*;

import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.tapiji.translator.suggestionprovider.glossary.GlossarySuggestionProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GlossarySuggestionProviderTest {

	private GlossarySuggestionProvider gsp;
	private String originalText = "Instances of this class are selectable";
	private String translatedText = "Instanzen der Klasse (47% match)";
	private String targetLanguage = "de";
	private static final String ICON_PATH = "/icons/sample.gif";

	/** Test glossary */
	private String glossaryFile = "glossary.xml";
	private Image icon = new Image(Display.getCurrent(),
			GlossarySuggestionProvider.class.getResourceAsStream(ICON_PATH));

	@Before
	public void setUp() throws Exception {
		gsp = new GlossarySuggestionProvider();
		gsp.setGlossaryFile(glossaryFile);
	}

	@After
	public void tearDown() throws Exception {
		gsp = null;
	}

	@Test
	public void testGetSuggestion() {
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(originalText, targetLanguage);
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(icon, translatedText, gsp);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());
	}

	@Test
	public void testGetSuggestionWithTargetLanguageInUpperCase() {
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(originalText, "DE");
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(icon, translatedText, gsp);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());
	}

	@Test
	public void testGetSuggestionWithWrongLanguage() {
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(originalText, "foo");
		} catch (Exception e) {
			fail();
		}

		assertEquals(SuggestionErrors.NO_SUGESTION_ERR, actual.getText());

	}

	@Test
	public void testGetSuggestionCaseInsensitive() {
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(originalText.toLowerCase(),
					targetLanguage);
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(icon, translatedText, gsp);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());

	}

	@Test
	public void testGetSuggestionWithNullParameter() {
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(null, null);
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
			actual = gsp.getSuggestion("", "");
		} catch (Exception e) {
			fail();
		}

		assertNotNull(actual);
		assertEquals(SuggestionErrors.NO_SUGESTION_ERR, actual.getText());
	}
	// TODO test invalid glossary file
}
