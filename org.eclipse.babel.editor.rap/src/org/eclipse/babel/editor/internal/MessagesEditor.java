package org.eclipse.babel.editor.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.babel.core.message.IMessagesBundle;
import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.message.internal.IMessagesBundleGroupListener;
import org.eclipse.babel.core.message.internal.MessagesBundle;
import org.eclipse.babel.core.message.internal.MessagesBundleGroupAdapter;
import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.core.util.BabelUtils;
import org.eclipse.babel.editor.IMessagesEditor;
import org.eclipse.babel.editor.util.SharedMsgEditorsManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.IPropertiesFileLockListener;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.PFLock;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class MessagesEditor extends AbstractMessagesEditor {

	private ResourceBundle resourceBundle;
	private Display display;
	private Map<PropertiesFile, IPropertiesFileLockListener> pfLockListeners =
			new HashMap<PropertiesFile, IPropertiesFileLockListener>();

	private IPropertiesFileLockListener pfLockListener = new IPropertiesFileLockListener() {
		// return locale from properties file ID
		private Locale idToLocale(long pfID) {
			PropertiesFile propertiesFile = DBUtils.getPropertiesFile(pfID);
			return propertiesFile.getLocale() != null ?
					new Locale(propertiesFile.getLocale()) : null;
		}

		@Override
		public void lockAcquired(final PFLock lock) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					User currentUser = UserUtils.getUser();
					if (currentUser != null && ! currentUser.equals(lock.getOwner())) {
						Locale locale = idToLocale(lock.getPropertiesFileID());

						// disable i18n-Page + text-editor
						setEnabled(false, locale);
						// disable add button
						i18nPage.getSidNavTextBoxComposite().setEnabled(false);
						// disable context menu
						i18nPage.getTreeViewer().getTree().getMenu().setEnabled(false);
					}
				}
			});
		}

		@Override
		public void lockReleased(final PFLock lock) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					User currentUser = UserUtils.getUser();
					if (currentUser != null && ! currentUser.equals(lock.getOwner())) {
						Locale locale = idToLocale(lock.getPropertiesFileID());

						// enable i18n-entry + text-editor
						setEnabled(true, locale);

						if (RBLockManager.INSTANCE.isOwnerOfRBLock(currentUser, resourceBundle)) {
							// enable add button if rb isn't locked
							i18nPage.getSidNavTextBoxComposite().setEnabled(true);
							// enable context menu
							i18nPage.getTreeViewer().getTree().getMenu().setEnabled(true);
						}

						// refresh the text-editor of the locale
						for (int i=0; i < localesIndex.size(); i++) {
							Locale l = localesIndex.get(i);
							if (BabelUtils.equals(l, locale)) {
								textEditorsIndex.get(i).doRevertToSaved();
								break;
							}
						}

						// get messages bundle from locale
						IMessagesBundle msgBundle = messagesBundleGroup.getMessagesBundle(locale);
						// update message bundle with underlying resource
						msgBundle.getResource().deserialize(msgBundle);
						// refresh selected key's entry
						RBManager.getInstance(messagesBundleGroup.getProjectName()).fireResourceChanged(msgBundle);
					}
				}
			});
		}
	};

	@Override
	public void disposeRAP() {
		if (resourceBundle != null && ! pfLockListeners.isEmpty()) {
			for (PropertiesFile propertiesFile : pfLockListeners.keySet()) {
				RBLockManager.INSTANCE.removePFLockListener(propertiesFile.getId(), pfLockListeners.get(propertiesFile));
			}
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
			for (PropertiesFile propertiesFile : resourceBundle.getPropertiesFiles()) {
				RBLockManager.INSTANCE.addPFLockListener(propertiesFile.getId(), pfLockListener);
				// disable editor if resource bundle is already opened by another user
				if (RBLockManager.INSTANCE.isPFLocked(propertiesFile.getId()))
					pfLockListener.lockAcquired(RBLockManager.INSTANCE.getPFLock(propertiesFile.getId()));

				pfLockListeners.put(propertiesFile, pfLockListener);
			}
		}
	}

	@Override
	protected IMessagesBundleGroupListener getMsgBundleGroupListner() {
		return new MessagesBundleGroupAdapter() {
			@Override
			public void messagesBundleAdded(final MessagesBundle messagesBundle) {
				final Locale newLocal = messagesBundle.getLocale();

				display.asyncExec(new Runnable() {
					@Override
                    public void run() {
						// add a new messages bundle to editor
						addMessagesBundle(messagesBundle);
                        // if resource bundle not temporary
                        if (resourceBundle != null) {
                            // get properties file
                            PropertiesFile pf = ((TextEditor) messagesBundle
                                    .getResource().getSource())
                                    .getPropertiesFile();
                            // update resource bundle
                            resourceBundle.getPropertiesFiles().add(pf);
                            // register properties file lock listener
                            RBLockManager.INSTANCE.addPFLockListener(
                                    pf.getId(), pfLockListener);
                        }
					}
				});

				// inform other msg editors which have opened same resource bundle
				// only owner thread of display, should initiate this, otherwise endless loop
				if (Display.getCurrent().equals(display) && resourceBundle != null) {
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
                        TextEditor textEditor = (TextEditor) messagesBundle
                                .getResource().getSource();

                        // if resource bundle not temporary
                        if (resourceBundle != null) {

                            // remove properties file lock listener
                            RBLockManager.INSTANCE.removePFLockListener(
                                    textEditor.getPropertiesFile().getId(),
                                    pfLockListener);
                            // update resource bundle
                            resourceBundle.getPropertiesFiles().remove(
                                    textEditor.getPropertiesFile());
                        }

						// remove msg bundle from i18n page + remove text editor
                        removeMessagesBundle(messagesBundle);

                        // update input file if it was deleted
                        IFileEditorInput editorInput = (IFileEditorInput) textEditor.getEditorInput();
                        if (editorInput.getFile().equals(file)) {
                            if (!messagesBundleGroup.getMessagesBundles()
                                    .isEmpty()) {
                                IMessagesBundle nextMsgBundle = messagesBundleGroup
                                        .getMessagesBundles().iterator().next();
                                TextEditor nextTextEditor = (TextEditor) nextMsgBundle
                                        .getResource().getSource();

                                file = ((IFileEditorInput) nextTextEditor
                                        .getEditorInput()).getFile();
                                setInput(new FileEditorInput(file));
                            }
                        }
					}
				});

				// inform other msg editors which have opened same resource bundle
				// only owner thread of display, should initiate this, otherwise endless loop
				if (resourceBundle != null && Display.getCurrent().equals(display)) {
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

	public void setEnabled(boolean enabled, Locale locale) {
		i18nPage.setEnabled(enabled, locale);

		for (int i=0; i < localesIndex.size(); i++) {
			Locale l = localesIndex.get(i);
			if (BabelUtils.equals(l, locale)) {
				((TextEditor)textEditorsIndex.get(i)).setEnabled(enabled);
				break;
			}
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
