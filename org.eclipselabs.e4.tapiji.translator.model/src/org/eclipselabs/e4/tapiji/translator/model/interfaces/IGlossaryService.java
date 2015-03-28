package org.eclipselabs.e4.tapiji.translator.model.interfaces;


/*******************************************************************************
 * Copyright (c) 2012 Christian Behon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Behon
 ******************************************************************************/

import java.io.File;
import java.util.List;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;


public interface IGlossaryService {

    void loadGlossary(final File file);

    Glossary getGlossary();

    void removeTerm(final Term term);

    void evictGlossary();

    void updateGlossary(Glossary glossary);

    void addTerm(final Term parentTerm, final Term term);

    void addLocales(final Object[] locales);

    String[] getTranslations();

    void saveGlossary();

    void createGlossary(final File file);

    void reloadGlossary();

    void removeLocales(List<String> locales);
}