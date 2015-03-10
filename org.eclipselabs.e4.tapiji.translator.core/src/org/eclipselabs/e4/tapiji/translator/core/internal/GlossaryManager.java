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
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.utils.FileUtils;


public final class GlossaryManager implements IGlossaryService {

    private static final String TAG = GlossaryManager.class.getSimpleName();

    private Glossary glossary;

    @Inject
    private IEventBroker eventBroker;

    @Override
    public Glossary getGlossary() {
        return glossary;
    }

    @Override
    public void loadGlossary(final File file) {
        JAXBContext context;
        try {
            final Glossary glossary = new Glossary();
            context = JAXBContext.newInstance(glossary.getClass());
            this.glossary = (Glossary) context.createUnmarshaller().unmarshal(file);
            Log.d(TAG, String.format("Loaded glossary: %s ", glossary.toString()));
        } catch (final JAXBException exception) {
            Log.wtf(TAG, String.format("Can not load file %s", file), exception);
        }
    }

    @Override
    public void saveGlossary(final File file) {
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
                eventBroker.post(GlossaryServiceConstants.TOPIC_GLOSSARY_NEW, "DATA");
                Log.d(TAG, String.format("Glossary saved: %s ", glossary.toString()));

            } catch (final IOException exceptions) {
                Log.wtf(TAG, "Interrupted I/O operations", exceptions);
            }
        } catch (final JAXBException exception) {
            Log.wtf(TAG, "Marshall problem", exception);
        }
    }
}
