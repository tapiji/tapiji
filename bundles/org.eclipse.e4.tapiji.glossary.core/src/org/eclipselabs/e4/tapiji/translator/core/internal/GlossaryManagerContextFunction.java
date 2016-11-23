package org.eclipselabs.e4.tapiji.translator.core.internal;


/*******************************************************************************
 * Copyright (c) 2015 Christian Behon
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Behon
 ******************************************************************************/

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;


public final class GlossaryManagerContextFunction extends ContextFunction {

    @Override
    public Object compute(final IEclipseContext context, final String contextKey) {
        final IGlossaryService glossaryManager = ContextInjectionFactory.make(GlossaryManager.class, context);
        final MApplication application = context.get(MApplication.class);
        final IEclipseContext applicationContext = application.getContext();
        applicationContext.set(IGlossaryService.class, glossaryManager);
        return glossaryManager;
    }
}
