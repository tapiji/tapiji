/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.core.internal;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.messages.ErrorMessage;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Info;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.utils.FileUtils;


public final class GlossaryManager implements IGlossaryService {

    private static final String TAG = GlossaryManager.class.getSimpleName();

    @Inject
    private Glossary glossary;

    @Inject
    private IEventBroker eventBroker;

    private File file;

    private JAXBContext glossaryContext;

    @Override
    public Glossary getGlossary() {
        return glossary;
    }

    @Override
    public final void reloadGlossary() {
        if (file != null) {
            openGlossary(file);
        }
    }

    @Override
    public final void updateGlossary(final Glossary glossary) {
        this.glossary = glossary;
        saveGlossary();
    }

    @Override
    public final void openGlossary(final File file) {
        Log.i(TAG, String.format("Open Glossary %s", file));
        this.file = file;

        try {
            glossaryContext = JAXBContext.newInstance(glossary.getClass());
            Unmarshaller unmarshaller = glossaryContext.createUnmarshaller();
            glossary = (Glossary) unmarshaller.unmarshal(file);
            eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
            Log.d(TAG, String.format("Loaded glossary: %s ", glossary.toString()));
        } catch (final JAXBException exception) {
            final ErrorMessage message = new ErrorMessage("Glossary error", String.format("Can not load file %s", file));
            eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR, message);
            Log.wtf(TAG, String.format("Can not load file %s", file), exception);
        }
    }

    @Override
    public final void createGlossary(final File file) {
        Log.i(TAG, String.format("Create Glossary %s", file));
        this.file = file;
        try {
            glossaryContext = JAXBContext.newInstance(glossary.getClass());
            saveGlossary();
            eventBroker.send(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public final void saveGlossary() {
        Log.i(TAG, String.format("Save Glossary %s", glossary.toString()));
        boolean error = false;

        try {
            
            
            final Marshaller marshaller = glossaryContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, FileUtils.ENCODING_TYPE_UTF_16);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            try (OutputStream fout = new FileOutputStream(file.getAbsolutePath()); OutputStream bout = new BufferedOutputStream(fout); OutputStreamWriter osw = new OutputStreamWriter(bout, FileUtils.ENCODING_TYPE_UTF_16)) {
                marshaller.marshal(glossary, osw);
                Log.d(TAG, String.format("Glossary saved: %s ", glossary.toString()));
            } catch (final IOException exceptions) {
                error = true;
                Log.wtf(TAG, "Interrupted I/O operations", exceptions);
            }
            
        } catch (final JAXBException exception) {
            error = true;
            Log.wtf(TAG, "Marshall problem", exception);
        }
        if (error) {
            final ErrorMessage message = new ErrorMessage("Glossary error", String.format("Can not save file %s", file));
            eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR, message);
        }
    }


    @Override
    public final String[] getTranslations() {
        if (glossary != null) {
            return glossary.info.getTranslations();
        } else {
            return new String[0];
        }
    }

    @Override
    public final void removeLocales(final List<String> locales) {
        glossary.info.translations.removeAll(locales);
        saveGlossary();
        eventBroker.send(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
        Log.d(TAG, String.format("Glossary: %s ", glossary.toString()));
    }

    @Override
    public final void addLocales(final Object[] locales) {
        if (glossary != null) {
            Stream.of(locales).forEach(locale -> glossary.info.translations.add(locale.toString()));
            saveGlossary();
            eventBroker.send(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
        }
    }

    @Override
    public boolean containsTerm(final String newTerm) {
        return false;
    }


    @Override
    public final void addTerm(final Term parentTerm, final Term term) {
        glossary.addTerm(parentTerm, term);
        saveGlossary();
        eventBroker.send(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
        Log.d(TAG, String.format("Added Term: %s ", term.toString()));
    }

    @Override
    public final void removeTerm(final Term term) {
        if (glossary != null) {
            glossary.removeTerm(term);
            saveGlossary();
            eventBroker.send(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
            Log.d(TAG, String.format("Removed Term: %s ", term.toString()));
        }
    }

    @Override
    public final void evictGlossary() {
        glossary.terms.forEach(term -> {
            term.translations.clear();
            term.subTerms.clear();
            term.parentTerm = null;
            term.subTerms = null;
            term.translations = null;
        });
        glossary.terms.clear();
        glossary.info = Info.create();
    }
}
