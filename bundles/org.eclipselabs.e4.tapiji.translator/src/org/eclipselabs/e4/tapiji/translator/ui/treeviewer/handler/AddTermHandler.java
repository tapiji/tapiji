package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;
import org.eclipselabs.e4.tapiji.translator.ui.glossary.GlossaryContract;


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
    public void execute(final StoreInstanceState storeInstanceState, final MPart part) {
        if (part.getObject() instanceof GlossaryContract.View) {
            GlossaryContract.View glossaryView = (GlossaryContract.View) part.getObject();
            final InputDialog dialog = new InputDialog(shell, "New Term", "Please, define the new term:", "", this);
            if (dialog.open() == Window.OK) {
                if ((dialog.getValue() != null) && (dialog.getValue().trim().length() > 0)) {
                    final Term term = Term.newInstance(Translation.create(storeInstanceState.getReferenceLanguage(), dialog.getValue()));
                    glossaryService.addTerm(selectedTerm, term);
                    glossaryView.getTreeViewerView().addSelection(term);
                    Log.d(TAG, String.format("Term %s added with id %s", dialog.getValue(), storeInstanceState.getReferenceLanguage()));
                }
            }
        }

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
