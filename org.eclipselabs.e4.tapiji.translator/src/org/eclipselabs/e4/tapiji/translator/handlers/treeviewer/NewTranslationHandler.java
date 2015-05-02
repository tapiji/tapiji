package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.inject.Named;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.providers.LocaleContentProvider;
import org.eclipselabs.e4.tapiji.translator.views.providers.LocaleLabelProvider;
import org.eclipselabs.e4.tapiji.utils.LocaleUtils;


public final class NewTranslationHandler {

    @Execute
    public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) final Term term,
                    @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService) {
        final CheckedTreeSelectionDialog localeDialog = new CheckedTreeSelectionDialog(shell,
                        new LocaleLabelProvider(), new LocaleContentProvider());
        localeDialog.setInput(generateLocales(glossaryService.getTranslations()));
        localeDialog.setTitle("Translation Selection");

        if (localeDialog.open() == Window.OK) {
            addTranslationAsync(glossaryService, localeDialog.getResult());
        }
    }

    private List<Locale> generateLocales(final String[] translations) {
        final List<Locale> allLocales = new ArrayList<Locale>();
        final List<Locale> locales = new ArrayList<Locale>();

        for (final String languageCode : translations) {
            if (languageCode.equalsIgnoreCase("default")) {
                continue;
            }
            locales.add(LocaleUtils.getLocaleFromLanguageCode(languageCode));
        }

        for (final Locale locale : Locale.getAvailableLocales()) {
            if (!locales.contains(locale)) {
                allLocales.add(locale);
            }
        }

        Collections.sort(allLocales, new Comparator<Locale>() {

            @Override
            public int compare(final Locale o1, final Locale o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });
        return allLocales;
    }

    private void addTranslationAsync(final IGlossaryService glossaryService, final Object[] translations) {
        final Job job = new Job("Add New Translation") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                glossaryService.addLocales(translations);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @CanExecute
    public boolean canExecute(final IGlossaryService glossaryService) {
        if (glossaryService.getGlossary() == null) {
            return false;
        }
        return true;
    }

}
