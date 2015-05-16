package org.eclipselabs.e4.tapiji.editor.ui.handler.toolbar;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.editor.ui.widget.LocaleSelector;


public final class NewLocaleHandler {

    private static final String TAG = NewLocaleHandler.class.getSimpleName();

    @Execute
    public void execute(final Shell shell) {
        final Dialog localeDialog = new Dialog(shell) {

            LocaleSelector selector;

            @Override
            protected void configureShell(final Shell newShell) {
                super.configureShell(newShell);
                newShell.setText("Add new local");
            }

            @Override
            protected Control createDialogArea(final Composite parent) {
                final Composite comp = (Composite) super.createDialogArea(parent);
                selector = new LocaleSelector(comp);
                return comp;
            }

            @Override
            protected void okPressed() {
                // add local to bundleGroup
                /*
                 * MessagesBundleGroup bundleGroup = editor.getBundleGroup();
                 * Locale newLocal = selector.getSelectedLocale();
                 * // exists local already?
                 * boolean existsLocal = false;
                 * Locale[] locales = bundleGroup.getLocales();
                 * for (Locale locale : locales) {
                 * if (locale == null) {
                 * if (newLocal == null) {
                 * existsLocal = true;
                 * break;
                 * }
                 * } else if (locale.equals(newLocal)) {
                 * existsLocal = true;
                 * break;
                 * }
                 * }
                 * if (!existsLocal) bundleGroup.addMessagesBundle(newLocal);
                 */

                super.okPressed();
            }
        };
        // open dialog
        localeDialog.open();
    }

    @CanExecute
    public boolean canExecute() {
        return true;
    }
}
