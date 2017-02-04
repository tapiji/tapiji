package org.eclipse.e4.tapiji.git.core.internal;


import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.ui.model.application.MApplication;


public class GitServiceContextFactory extends ContextFunction {

    @Override
    public Object compute(final IEclipseContext context, final String contextKey) {
        final IGitService glossaryManager = ContextInjectionFactory.make(GitService.class, context);
        final MApplication application = context.get(MApplication.class);
        final IEclipseContext applicationContext = application.getContext();
        applicationContext.set(IGitService.class, glossaryManager);
        return glossaryManager;
    }
}
