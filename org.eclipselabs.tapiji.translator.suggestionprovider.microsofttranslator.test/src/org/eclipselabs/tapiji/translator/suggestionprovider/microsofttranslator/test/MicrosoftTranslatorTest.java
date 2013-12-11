package org.eclipselabs.tapiji.translator.suggestionprovider.microsofttranslator.test;

import static org.junit.Assert.*;

import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.tapiji.translator.suggestionprovider.microsofttranslator.MicrosoftTranslatorProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MicrosoftTranslatorTest {
	
	private MicrosoftTranslatorProvider mtp;
	
	private String originalText = "Instances of this class are selectable user " +
			"interface objects that allow the user to enter and modify text." +
			" Text controls can be either single or multi-line."; 
			
	private String targetLanguage = "de";
	
	private String translatedText = "Instanzen dieser Klasse sind wählbar " +
			"Benutzer-Interface-Objekte, mit denen den Benutzer eingeben " +
			"und Ändern von Text. Text-Steuerelemente können entweder " +
			"ein- oder mehrzeiligen sein.";
	
	private static final String ICON_PATH = "/icons/mt16.png";
	
	private Image icon = new Image(Display.getCurrent(),
			MicrosoftTranslatorProvider.class.getResourceAsStream(ICON_PATH));

	@Before
	public void setUp() throws Exception {
		mtp = new MicrosoftTranslatorProvider();
	}

	@After
	public void tearDown() throws Exception {
		mtp = null;
	}

	/**According to Microsoft Translator API, empty source language string should cause to detect source
	 * language automatically. If the resulting suggestion doesn't match with already translated suggestion 
	 * (by Microsoft Translator) test fails.
	 * */
	@Test
	public void testGetSuggestion() {
		
		Suggestion actual = null;
		
		try {
			actual = mtp.getSuggestion(originalText, targetLanguage);
		} catch (Exception e) {
			fail();
		}
				
		
		Suggestion expected = new Suggestion(icon,translatedText, mtp);
		
		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());		
	}
	
	@Test
	public void testGetSuggestionWithTargetLanguageInUpperCase() {
		Suggestion actual = null;
		
		try {
			actual =  mtp.getSuggestion(originalText, "DE");
		} catch (Exception e) {
			fail();
		}
		
		Suggestion expected = new Suggestion(icon,translatedText, mtp);
		
		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());		
	}
	
	@Test
	public void testGetSuggestionWithWrongLanguage() {
		
		Suggestion actual = null;
		try {
			actual = mtp.getSuggestion(originalText, "foo");
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
			actual = mtp.getSuggestion(null, null);
		}catch (Exception e) {
			fail();
		}
		
		assertNotNull(actual);
		assertEquals(SuggestionErrors.NO_SUGESTION_ERR, actual.getText());
	}
	
	@Test
	public void testGetSuggestionInvalidParameter() {
		Suggestion actual = null;

		try {
			actual = mtp.getSuggestion("", "");
		}catch (Exception e) {
			fail();
		}
		
		assertNotNull(actual);
		assertEquals(SuggestionErrors.NO_SUGESTION_ERR, actual.getText());
		
	}

}
