/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.tests;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipselabs.tapiji.translator.model.Glossary;
import org.eclipselabs.tapiji.translator.model.Info;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.model.Translation;
import org.junit.Assert;
import org.junit.Test;


public class JaxBTest {

	@Test
	public void testModel () {
		Glossary glossary = new Glossary ();
		Info info = new Info ();
		info.translations = new ArrayList<String>();
		info.translations.add("default");
		info.translations.add("de");
		info.translations.add("en");
		glossary.info = info;
		glossary.terms = new ArrayList<Term>();
		
		// Hello World
		Term term = new Term();
		
		Translation tranl1 = new Translation ();
		tranl1.id = "default";
		tranl1.value = "Hallo Welt";
		
		Translation tranl2 = new Translation ();
		tranl2.id = "de";
		tranl2.value = "Hallo Welt";
		
		Translation tranl3 = new Translation ();
		tranl3.id = "en";
		tranl3.value = "Hello World!";
		
		term.translations = new ArrayList <Translation>();
		term.translations.add(tranl1);
		term.translations.add(tranl2);
		term.translations.add(tranl3);
		term.parentTerm = null;
		
		glossary.terms.add(term);
		
		// Hello World 2
		Term term2 = new Term();
		
		Translation tranl12 = new Translation ();
		tranl12.id = "default";
		tranl12.value = "Hallo Welt2";
		
		Translation tranl22 = new Translation ();
		tranl22.id = "de";
		tranl22.value = "Hallo Welt2";
		
		Translation tranl32 = new Translation ();
		tranl32.id = "en";
		tranl32.value = "Hello World2!";
		
		term2.translations = new ArrayList <Translation>();
		term2.translations.add(tranl12);
		term2.translations.add(tranl22);
		term2.translations.add(tranl32);
		//term2.parentTerm = term;
		
		term.subTerms = new ArrayList<Term>();
		term.subTerms.add(term2);
		
		// Hello World 3
		Term term3 = new Term();
		
		Translation tranl13 = new Translation ();
		tranl13.id = "default";
		tranl13.value = "Hallo Welt3";
		
		Translation tranl23 = new Translation ();
		tranl23.id = "de";
		tranl23.value = "Hallo Welt3";
		
		Translation tranl33 = new Translation ();
		tranl33.id = "en";
		tranl33.value = "Hello World3!";
		
		term3.translations = new ArrayList <Translation>();
		term3.translations.add(tranl13);
		term3.translations.add(tranl23);
		term3.translations.add(tranl33);
		term3.parentTerm = null;
		
		glossary.terms.add(term3);
		
		// Serialize model 
		try {
			JAXBContext context = JAXBContext.newInstance(glossary.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(glossary, new FileWriter ("C:\\test.xml"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
	}
	
	@Test
	public void testReadModel () {
		Glossary glossary = new Glossary();
		
		try {
			JAXBContext context = JAXBContext.newInstance(glossary.getClass());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			glossary = (Glossary) unmarshaller.unmarshal(new File ("C:\\test.xml"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
	}
	
}
