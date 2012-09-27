package org.eclipse.babel.editor.internal;

import java.util.List;
import java.util.Locale;

import org.eclipse.babel.core.message.IMessagesBundle;
import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.message.internal.IMessagesBundleGroupListener;
import org.eclipse.babel.core.message.internal.MessageException;
import org.eclipse.babel.core.message.internal.MessagesBundle;
import org.eclipse.babel.core.message.internal.MessagesBundleGroup;
import org.eclipse.babel.core.message.internal.MessagesBundleGroupAdapter;
import org.eclipse.babel.core.message.tree.internal.AbstractKeyTreeModel;
import org.eclipse.babel.editor.IMessagesEditor;
import org.eclipse.babel.editor.IMessagesEditorChangeListener;
import org.eclipse.babel.editor.builder.ToggleNatureAction;
import org.eclipse.babel.editor.bundle.MessagesBundleGroupFactory;
import org.eclipse.babel.editor.preferences.MsgEditorPreferences;
import org.eclipse.babel.editor.util.SharedMsgEditorsManager;
import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.IResourceBundleLockListener;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLock;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class MessagesEditor extends AbstractMessagesEditor {
	
	private ResourceBundle resourceBundle;
	private Display display;
	private IResourceBundleLockListener rbLockListener;
		
	@Override
	public void disposeRAP() {		
		
		if (resourceBundle != null) {
			RBLockManager.INSTANCE.removeRBLockListener(resourceBundle.getId(), rbLockListener);
			SharedMsgEditorsManager.INSTANCE.removeMessagesEditor(resourceBundle.getId(), this);
		}
	}	
	
	private ResourceBundle getRBFromFile(IFile ifile) {		
		PropertiesFile propsFile = DBUtils.getPropertiesFile(file.getLocation().toOSString());
		ResourceBundle rb = propsFile != null ? propsFile.getResourceBundle() : null;
		return rb;
	}
	
	@Override
	protected void initRAP() {
		display = Display.getCurrent();			
		resourceBundle = getRBFromFile(file);			
		// add locking mechanism only for stored resource bundles
		if (resourceBundle != null) {				
			SharedMsgEditorsManager.INSTANCE.addMessagesEditor(resourceBundle.getId(), this);
			
			rbLockListener = new IResourceBundleLockListener() {		
				IStatusLineManager statusLineManager;
				
				private void initStatusLine() {
					if (statusLineManager == null && getEditorSite() != null)
						statusLineManager = getEditorSite().getActionBars().getStatusLineManager();
				}
				@Override
				public void lockAcquired(final RBLock lock) {
					display.asyncExec(new Runnable() {									
						@Override
						public void run() {
							User currentUser = UserUtils.getUser();
							if (currentUser != null && ! currentUser.equals(lock.getOwner())) {						
								initStatusLine();
								// disable editor								
								setEnabled(false);
								statusLineManager.setErrorMessage("Resource bundle is currently locked by \""+
										lock.getOwner().getUsername()+"\"!");
							}
						}
					});		
				}	
				
				@Override
				public void lockReleased(final RBLock lock) {
					display.asyncExec(new Runnable() {									
						@Override
						public void run() {
							User currentUser = UserUtils.getUser();
							if (currentUser != null && ! currentUser.equals(lock.getOwner())) {
								initStatusLine();
								setEnabled(true);
								// refresh text-editors
								for (ITextEditor textEditor : textEditorsIndex)
									textEditor.doRevertToSaved();
								// update message bundles with underlying resource
								for (IMessagesBundle bundle : messagesBundleGroup.getMessagesBundles())
									bundle.getResource().deserialize(bundle);
								// refresh tree model
								refreshKeyTreeModel();
								// refresh selected key's entries
								for (IMessagesEditorChangeListener listener : changeListeners) {
									listener.selectedKeyChanged(getSelectedKey(), getSelectedKey());
								}
								
								// clear status bar
								statusLineManager.setErrorMessage(null);											
							}
						}
					});
				}
			};				
			
			RBLockManager.INSTANCE.addRBLockListener(resourceBundle.getId(), rbLockListener);
			// disable editor if resource bundle is already opened by another user
			if (RBLockManager.INSTANCE.isLocked(resourceBundle.getId()))
				rbLockListener.lockAcquired(RBLockManager.INSTANCE.getRBLock(resourceBundle.getId()));
		}
	}

	@Override
	protected IMessagesBundleGroupListener getMsgBundleGroupListner() {		
		return new MessagesBundleGroupAdapter() {
			@Override
			public void messagesBundleAdded(final MessagesBundle messagesBundle) {
				final Locale newLocal = messagesBundle.getLocale();
				
				display.asyncExec(new Runnable() {
					public void run() {
						addMessagesBundle(messagesBundle, newLocal);
						// add entry to i18n page
						i18nPage.addI18NEntry(MessagesEditor.this, newLocal);
					}
				});
				
				// inform other msg editors which have opened same resource bundle
				// only owner thread of display, should initiate this, otherwise endless loop
				if (Display.getCurrent().equals(display)) {
					List<IMessagesEditor> sharedEditors = SharedMsgEditorsManager.INSTANCE.
							getSharedMessagesEditors(resourceBundle.getId());
					for (IMessagesEditor sharedEditor : sharedEditors) {
						if (sharedEditor != MessagesEditor.this)
							// add locale to others msg bundle group -> fires messagesBundleAdded event
							// add locale to create new msg bundle, can't use the same msg bundle with same text editor resource
							sharedEditor.getBundleGroup().addMessagesBundle(newLocal);
					}
				}
			}
			@Override
			public void messagesBundleRemoved(final MessagesBundle messagesBundle) {
				final Locale locale = messagesBundle.getLocale();
				
				display.asyncExec(new Runnable() {
					@Override
					public void run() {											
						removeMessagesBundle(messagesBundle, locale);							
					}
				});					
				
				// inform other msg editors which have opened same resource bundle
				// only owner thread of display, should initiate this, otherwise endless loop
				if (Display.getCurrent().equals(display)) {
					List<IMessagesEditor> sharedEditors = SharedMsgEditorsManager.INSTANCE.
							getSharedMessagesEditors(resourceBundle.getId());
					for (IMessagesEditor sharedEditor : sharedEditors) {
						if (sharedEditor != MessagesEditor.this) {
							// remove msg bundle from others msg bundle group -> fires messagesBundleRemoved event
							IMessagesBundleGroup otherGroup = sharedEditor.getBundleGroup();
							IMessagesBundle otherBundle = otherGroup.getMessagesBundle(locale);
							otherGroup.removeMessagesBundle(otherBundle);
						}
					}
				}
			}
		};
	}

	@Override
	public void setEnabled(boolean enabled) {
		i18nPage.setEnabled(enabled);
		for (ITextEditor textEditor : textEditorsIndex) {
			((TextEditor) textEditor).setEnabled(enabled);
		}
	}
}
