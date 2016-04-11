package org.eclipselabs.e4.tapiji.translator.ui.handler.treeviewer;


import javax.inject.Named;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;


public class AddTermHandler {

    private static final String TAG = AddTermHandler.class.getSimpleName();

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) final Term parentTerm, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell,
                    final IGlossaryService glossaryService, final StoreInstanceState storeInstanceState) {
        final InputDialog dialog = new InputDialog(shell, "New Term", "Please, define the new term:", "", null);// TODO Translate
        if (dialog.open() == Window.OK) {
            if ((dialog.getValue() != null) && (dialog.getValue().trim().length() > 0)) {
                addTermAsync(glossaryService, dialog.getValue(), storeInstanceState.getReferenceLanguage(), parentTerm);
            }
        }
    }

    private void addTermAsync(final IGlossaryService glossaryService, final String value, final String referenceLocale, final Term parentTerm) {
        final Job job = new Job("removing") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                final Term term = Term.newInstance();
                final Translation translation = Translation.newInstance(referenceLocale, value);
                term.parentTerm = null;
                term.translations.add(translation);
                glossaryService.addTerm(parentTerm, term);
                Log.d(TAG, String.format("Term %s added with id %s", value, referenceLocale));
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
