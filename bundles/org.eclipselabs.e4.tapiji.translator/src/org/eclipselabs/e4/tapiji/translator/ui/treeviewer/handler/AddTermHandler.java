package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;


public class AddTermHandler implements IInputValidator {

    private static final String TAG = AddTermHandler.class.getSimpleName();
    
    @Inject
    private IGlossaryService glossaryService;
    
    @Inject
    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell shell;
    
    @Inject
    @Optional
    @Named(IServiceConstants.ACTIVE_SELECTION)
    private Term selectedTerm;
    
    @Execute
    public void execute(final StoreInstanceState storeInstanceState) {
        final InputDialog dialog = new InputDialog(shell, "New Term", "Please, define the new term:", "", this);
        if (dialog.open() == Window.OK) {
            if ((dialog.getValue() != null) && (dialog.getValue().trim().length() > 0)) {
                addTermAsync(glossaryService, dialog.getValue(), storeInstanceState.getReferenceLanguage(), selectedTerm);
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

    @Override
    public String isValid(String newText) {
        Log.d(TAG, "INPUT: " + newText);
        return null;
    }
}
