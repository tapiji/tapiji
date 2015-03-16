package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;


public class AddTermHandler {

    private static final String TAG = AddTermHandler.class.getSimpleName();

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService) {

        final InputDialog dialog = new InputDialog(shell, "New Term", "Please, define the new term:", "", null);
        if (dialog.open() == Window.OK) {
            if (dialog.getValue() != null && dialog.getValue().trim().length() > 0) {
                final Glossary glossary = glossaryService.getGlossary();
                Term term = Term.newInstance();


                Log.d(TAG, String.format("Added new Term %s ", dialog.getValue()));
            }
        }
    }

}
