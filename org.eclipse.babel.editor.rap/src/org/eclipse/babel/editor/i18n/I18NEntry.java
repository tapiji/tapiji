package org.eclipse.babel.editor.i18n;

import java.util.Locale;

import org.eclipse.babel.core.message.IMessage;
import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.util.BabelUtils;
import org.eclipse.babel.editor.internal.AbstractMessagesEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;

public class I18NEntry extends AbstractI18NEntry {

	public I18NEntry(Composite parent, AbstractMessagesEditor editor,
			Locale locale) {
		super(parent, editor, locale);
	}

	@Override
	void updateKey(String key) {		
		IMessagesBundleGroup messagesBundleGroup = editor.getBundleGroup();
		boolean isKey = key != null && messagesBundleGroup.isMessageKey(key);
	
		PropertiesFile propsFile = ((TextEditor) editor.getTextEditor(locale)).getPropertiesFile();
		if (propsFile == null || ! RBLockManager.INSTANCE.isPFLocked(propsFile.getId()))
			textBox.setEnabled(isKey);
		
		if (isKey) {
			IMessage entry = messagesBundleGroup.getMessage(key, locale);
			if (entry == null || entry.getValue() == null) {
				textBox.setText(null);				
			} else {				
				textBox.setText(entry.getValue());
			}
		} else {
			textBox.setText(null);
		}
		
	}

	@Override
	KeyListener getKeyListener() {
		return new KeyAdapter() {
			 public void keyReleased(KeyEvent event) {
	            // Text field has changed: make underlying text editor dirty if not already
	            if (!BabelUtils.equals(focusGainedText, textBox.getText())) {
	                // update model if underlying text editor isn't dirty yet
	                ITextEditor textEditor = (ITextEditor) editor.getBundleGroup().getMessagesBundle(locale).
	                		getResource().getSource();
	            	if ( !textEditor.isDirty()) {
	                    updateModel();
	                }
	            }
	        }
		};
	}
}
