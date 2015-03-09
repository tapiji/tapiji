/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Christian Behon
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.core.internal;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.model.ILoadGlossaryListener;
import org.eclipselabs.e4.tapiji.translator.model.LoadGlossaryEvent;


public class GlossaryManager implements IGlossaryService {

  private static final String TAG = GlossaryManager.class.getSimpleName();
  private final Object LOCK_OBJECT = new Object();
  private Glossary glossary;
  private final List<ILoadGlossaryListener> glossaryListeners = new ArrayList<ILoadGlossaryListener>();

  /* public GlossaryManager(final File file, final boolean overwrite) throws Exception {
    glossary = new Glossary();
    if (file.exists() && !overwrite) {
      loadGlossary(file);
    } else {
      saveGlossary(file);
    }
  }*/

  @Override
  public void setGlossary(final Glossary glossary) {
    this.glossary = glossary;
  }

  @Override
  public Glossary getGlossary() {
    Log.d(TAG, "getGlossary");
    return glossary;
  }

  @Override
  public void loadGlossaryEvent(final File file) {
    notifyListeners(new LoadGlossaryEvent(file));
  }

  @Override
  public void newGlossaryEvent(final File file) {
    notifyListeners(new LoadGlossaryEvent(file, true));
  }

  private void notifyListeners(final LoadGlossaryEvent event) {
    synchronized (LOCK_OBJECT) {
      for (final ILoadGlossaryListener listener : glossaryListeners) {
        listener.glossaryLoaded(event);
      }
    }
  }

  @Override
  public void loadGlossary(final File file) throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(glossary.getClass());
    final Unmarshaller unmarshaller = context.createUnmarshaller();
    glossary = (Glossary) unmarshaller.unmarshal(file);
  }

  @Override
  public void saveGlossary(final File file) throws Exception {
    final JAXBContext context = JAXBContext.newInstance(glossary.getClass());
    final Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");

    try (OutputStream fout = new FileOutputStream(file.getAbsolutePath());
         OutputStream bout = new BufferedOutputStream(fout);
         OutputStreamWriter osw = new OutputStreamWriter(bout, "UTF-16")) {
      marshaller.marshal(glossary, osw);
    }
  }

  @Override
  public void unregisterGlossaryListener(final ILoadGlossaryListener listener) {
    synchronized (LOCK_OBJECT) {
      glossaryListeners.remove(listener);
    }
  }

  @Override
  public void registerGlossaryListener(final ILoadGlossaryListener listener) {
    synchronized (LOCK_OBJECT) {
      glossaryListeners.add(listener);
    }
  }
}
