package org.eclipse.e4.tapiji.mylyn.core.internal;


import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.ui.model.application.MApplication;


public class MylynServiceContextFactory extends ContextFunction {

    @Override
    public Object compute(final IEclipseContext context, final String contextKey) {
        final MylynService glossaryManager = ContextInjectionFactory.make(MylynService.class, context);
        final MApplication application = context.get(MApplication.class);
        final IEclipseContext applicationContext = application.getContext();
        applicationContext.set(IMylynService.class, glossaryManager);
        return glossaryManager;
    }
}
