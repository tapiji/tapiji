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
package org.eclipselabs.e4.tapiji.translator.core;


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
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.LoadGlossaryEvent;


public class GlossaryManager {

  private final static Object lockObject = new Object();
  private Glossary glossary;
  private final File file;
  private static List<ILoadGlossaryListener> loadGlossaryListeners = new ArrayList<ILoadGlossaryListener>();

  public GlossaryManager(final File file, final boolean overwrite) throws Exception {
    this.file = file;

    if (file.exists() && !overwrite) {
      // load the existing glossary
      glossary = new Glossary();
      final JAXBContext context = JAXBContext.newInstance(glossary.getClass());
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      glossary = (Glossary) unmarshaller.unmarshal(file);
    } else {
      // Create a new glossary
      glossary = new Glossary();
      saveGlossary();
    }
  }

  public void saveGlossary() throws Exception {
    final JAXBContext context = JAXBContext.newInstance(glossary.getClass());
    final Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");

    final OutputStream fout = new FileOutputStream(file.getAbsolutePath());
    final OutputStream bout = new BufferedOutputStream(fout);
    marshaller.marshal(glossary, new OutputStreamWriter(bout, "UTF-16"));
  }

  public void setGlossary(final Glossary glossary) {
    this.glossary = glossary;
  }

  public Glossary getGlossary() {
    return glossary;
  }

  public static void loadGlossary(final File file) {
    notifyListeners(new LoadGlossaryEvent(file));
  }

  public static void newGlossary(final File file) {
    notifyListeners(new LoadGlossaryEvent(file, true));
  }

  private static void notifyListeners(final LoadGlossaryEvent event) {
    synchronized (lockObject) {
      for (final ILoadGlossaryListener listener : loadGlossaryListeners) {
        listener.glossaryLoaded(event);
      }
    }
  }

  public static void registerLoadGlossaryListener(final ILoadGlossaryListener listener) {
    synchronized (lockObject) {
      loadGlossaryListeners.add(listener);
    }
  }

  public static void unregisterLoadGlossaryListener(final ILoadGlossaryListener listener) {
    synchronized (lockObject) {
      loadGlossaryListeners.remove(listener);
    }
  }
}
