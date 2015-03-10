package org.eclipselabs.e4.tapiji.translator.model.interfaces;


/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Behon
 ******************************************************************************/

import java.io.File;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;


public interface IGlossaryService {

    void saveGlossary(File file);

    void loadGlossary(File file);

    Glossary getGlossary();
}
