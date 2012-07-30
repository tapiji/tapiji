package org.eclipselabs.tapiji.translator.rap.dialogs;

import java.util.Locale;

import org.eclipse.babel.core.message.internal.MessagesBundleGroup;
import org.eclipse.babel.editor.widgets.LocaleSelector;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class NewLocaleDialog extends Dialog {

	private LocaleSelector selector;
	private Locale selectedLocale;
	
	public NewLocaleDialog(Shell parentShell) {
		super(parentShell);
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add new local");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		selector = new LocaleSelector(comp);
		return comp;
	}
	
	public Locale getSelectedLocal() {
		return selectedLocale;
	}
	
	@Override
	protected void okPressed() {
		selectedLocale = selector.getSelectedLocale();
		super.okPressed();
	}
}
