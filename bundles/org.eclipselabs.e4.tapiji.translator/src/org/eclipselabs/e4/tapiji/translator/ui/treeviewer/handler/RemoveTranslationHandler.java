package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import java.util.ArrayList;
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
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler.provider.LocaleContentProvider;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler.provider.LocaleLabelProvider;
import org.eclipselabs.e4.tapiji.utils.LocaleUtils;


public final class RemoveTranslationHandler {

    private final ArrayList<Locale> languageLocales;
    private final ArrayList<String> languageCodes;

    public RemoveTranslationHandler() {
        languageLocales = new ArrayList<Locale>();
        languageCodes = new ArrayList<String>();
    }

    @Execute
    public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) final Term term, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService, final StoreInstanceState storeInstanceState) {

        final CheckedTreeSelectionDialog localeDialog = new CheckedTreeSelectionDialog(shell, new LocaleLabelProvider(), new LocaleContentProvider());

        localeDialog.setInput(generateLocales(glossaryService.getTranslations(), storeInstanceState.getReferenceLanguage()));
        localeDialog.setTitle("Translation Selection");

        if (localeDialog.open() == Window.OK) {
            removeTranslationAsync(glossaryService, localeDialog.getResult());
        }
    }

    private void removeTranslationAsync(final IGlossaryService glossaryService, final Object[] translations) {
        final Job job = new Job("Remove Locales") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                final List<String> toRemove = new ArrayList<String>();
                for (final Object delLoc : translations) {
                    toRemove.add(languageCodes.get(languageLocales.indexOf(delLoc)));
                }
                glossaryService.removeLocales(toRemove);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private ArrayList<Locale> generateLocales(final String[] translations, final String referenceLanguage) {

        final ArrayList<Locale> locales = new ArrayList<Locale>();
        languageLocales.clear();
        for (final String languageCode : translations) {
            if (languageCode.equalsIgnoreCase(referenceLanguage)) {
                continue;
            }
            locales.add(LocaleUtils.getLocaleFromLanguageCode(languageCode));
            languageLocales.add(LocaleUtils.getLocaleFromLanguageCode(languageCode));
            languageCodes.add(languageCode);
        }
        return locales;
    }

    @CanExecute
    public boolean canExecute(final IGlossaryService glossaryService) {
        if ((null == glossaryService.getGlossary()) || (glossaryService.getTranslations().length == 1) || (glossaryService.getTranslations().length == 0)) {
            return false;
        }
        return true;
    }

}
