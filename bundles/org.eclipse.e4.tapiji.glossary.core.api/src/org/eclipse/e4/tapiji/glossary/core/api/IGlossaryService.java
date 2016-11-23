package org.eclipse.e4.tapiji.glossary.core.api;


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

import org.eclipse.e4.tapiji.glossary.model.Glossary;
import org.eclipse.e4.tapiji.glossary.model.Term;


public interface IGlossaryService {

    void openGlossary(final File file);

    Glossary getGlossary();

    void removeTerm(final Term term);

    void evictGlossary();

    void updateGlossary(Glossary glossary);

    void addTerm(final Term parentTerm, final Term term);
    
    boolean containsTerm(String term);

    void addLocales(final Object[] locales);

    String[] getTranslations();

    void saveGlossary();

    void createGlossary(final File file);

    void reloadGlossary();

    void removeLocales(List<String> locales);
}