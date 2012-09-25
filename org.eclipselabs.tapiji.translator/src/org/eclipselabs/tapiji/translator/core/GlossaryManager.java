/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipselabs.tapiji.translator.model.Glossary;

public class GlossaryManager {

    private Glossary glossary;
    private File file;
    private static List<ILoadGlossaryListener> loadGlossaryListeners = new ArrayList<ILoadGlossaryListener>();

    public GlossaryManager(File file, boolean overwrite) throws Exception {
	this.file = file;

	if (file.exists() && !overwrite) {
	    // load the existing glossary
	    glossary = new Glossary();
	    JAXBContext context = JAXBContext.newInstance(glossary.getClass());
	    Unmarshaller unmarshaller = context.createUnmarshaller();
	    glossary = (Glossary) unmarshaller.unmarshal(file);
	} else {
	    // Create a new glossary
	    glossary = new Glossary();
	    saveGlossary();
	}
    }

    public void saveGlossary() throws Exception {
	JAXBContext context = JAXBContext.newInstance(glossary.getClass());
	Marshaller marshaller = context.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");

	OutputStream fout = new FileOutputStream(file.getAbsolutePath());
	OutputStream bout = new BufferedOutputStream(fout);
	marshaller.marshal(glossary, new OutputStreamWriter(bout, "UTF-16"));
    }

    public void setGlossary(Glossary glossary) {
	this.glossary = glossary;
    }

    public Glossary getGlossary() {
	return glossary;
    }

    public static void loadGlossary(File file) {
	/* Inform the listeners */
	LoadGlossaryEvent event = new LoadGlossaryEvent(file);
	for (ILoadGlossaryListener listener : loadGlossaryListeners) {
	    listener.glossaryLoaded(event);
	}
    }

    public static void registerLoadGlossaryListener(
	    ILoadGlossaryListener listener) {
	loadGlossaryListeners.add(listener);
    }

    public static void unregisterLoadGlossaryListener(
	    ILoadGlossaryListener listener) {
	loadGlossaryListeners.remove(listener);
    }

    public static void newGlossary(File file) {
	/* Inform the listeners */
	LoadGlossaryEvent event = new LoadGlossaryEvent(file);
	event.setNewGlossary(true);
	for (ILoadGlossaryListener listener : loadGlossaryListeners) {
	    listener.glossaryLoaded(event);
	}
    }
}
