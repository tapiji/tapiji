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
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.messages.ErrorMessage;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Info;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.utils.FileUtils;


public final class GlossaryManager implements IGlossaryService {

    private static final String TAG = GlossaryManager.class.getSimpleName();

    private Glossary glossary;

    @Inject
    private IEventBroker eventBroker;

    private File file;

    @Override
    public Glossary getGlossary() {
        return glossary;
    }

    @Override
    public void setGlossary(final Glossary glossary) {
        this.glossary = glossary;
    }

    @Override
    public void loadGlossary(final File file) {
        Log.i(TAG, String.format("Open Glossary %s", file));
        JAXBContext context;
        try {
            glossary = new Glossary();
            context = JAXBContext.newInstance(glossary.getClass());
            this.glossary = (Glossary) context.createUnmarshaller().unmarshal(file);
            Log.d(TAG, String.format("Loaded glossary: %s ", glossary.toString()));
            eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
        } catch (final JAXBException exception) {
            final ErrorMessage message = new ErrorMessage("Glossary error", String.format("Can not load file %s", file));
            eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR, message);
            Log.wtf(TAG, String.format("Can not load file %s", file), exception);
        }
    }

    @Override
    public void saveGlossary(final File file) {
        this.file = file;
        boolean error = false;
        this.glossary = new Glossary();
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(glossary.getClass());
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, FileUtils.ENCODING_TYPE_UTF_16);
            try (OutputStream fout = new FileOutputStream(file.getAbsolutePath());
                            OutputStream bout = new BufferedOutputStream(fout);
                            OutputStreamWriter osw = new OutputStreamWriter(bout, FileUtils.ENCODING_TYPE_UTF_16)) {

                marshaller.marshal(glossary, osw);
                // eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, glossary);
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
    public void addTerm(final Term parentTerm, final Term term) {

    }

    @Override
    public String[] getTranslations() {
        return glossary.info.getTranslations();
    }

    @Override
    public void removeLocales(final Object[] locales) {
        for (final Object localeToRemove : locales) {

        }
    }

    @Override
    public void addLocales(final Object[] locales) {
        for (final Object localeToAdd : locales) {
            glossary.info.translations.add(((Locale) localeToAdd).toString());
            Log.d(TAG, String.format("Locale %s added", ((Locale) localeToAdd).toString()));
        }
    }

    @Override
    public void removeTerm(final Term term) {
        glossary.removeTerm(term);
    }

    @Override
    public void evictGlossary() {
        for (final Term term : glossary.terms) {
            term.translations.clear();
            term.subTerms.clear();
            term.parentTerm = null;
            term.subTerms = null;
            term.translations = null;
        }
        glossary.terms.clear();
        glossary.info = Info.newInstance();
    }

    @Override
    public void saveGlossary() {
        if (glossary != null) {
            saveGlossary(file);
        }
    }
}
