package org.eclipse.babel.editor.internal;
import org.eclipse.babel.core.message.internal.IMessagesBundleGroupListener;
import org.eclipse.babel.core.message.internal.MessagesBundle;
import org.eclipse.babel.core.message.internal.MessagesBundleGroupAdapter;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;


public class MessagesEditor extends AbstractMessagesEditor {

	@Override
	protected IMessagesBundleGroupListener getMsgBundleGroupListner() {		
		return new MessagesBundleGroupAdapter() {
			@Override
			public void messagesBundleAdded(MessagesBundle messagesBundle) {
				addMessagesBundle(messagesBundle, messagesBundle.getLocale());
				// refresh i18n page
				i18nPage.addI18NEntry(MessagesEditor.this, messagesBundle.getLocale());
			}
		};
	}

	@Override
	protected void initRAP() {
		// nothing to do
	}

	@Override
	protected void disposeRAP() {
		// nothing to do
	}

	@Override
	public void setEnabled(boolean enabled) {
		i18nPage.setEnabled(enabled);
		for (ITextEditor textEditor : textEditorsIndex) {
			// TODO disable editors
		}
	}

}
