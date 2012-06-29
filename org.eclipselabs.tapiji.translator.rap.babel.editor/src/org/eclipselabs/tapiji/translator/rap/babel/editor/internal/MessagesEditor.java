/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 *    Alexej Strelzow - TapJI integration, bug fixes & enhancements
 *    				  - issue 35, 36, 48, 73
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.rap.babel.editor.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.IMessagesBundle;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.internal.MessageException;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.internal.MessagesBundle;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.internal.MessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.manager.RBManager;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.resource.IMessagesResource;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.tree.internal.AbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rap.babel.editor.IMessagesEditor;
import org.eclipselabs.tapiji.translator.rap.babel.editor.IMessagesEditorChangeListener;
import org.eclipselabs.tapiji.translator.rap.babel.editor.builder.ToggleNatureAction;
import org.eclipselabs.tapiji.translator.rap.babel.editor.bundle.MessagesBundleGroupFactory;
import org.eclipselabs.tapiji.translator.rap.babel.editor.i18n.I18NPage;
import org.eclipselabs.tapiji.translator.rap.babel.editor.plugin.MessagesEditorPlugin;
import org.eclipselabs.tapiji.translator.rap.babel.editor.preferences.MsgEditorPreferences;
import org.eclipselabs.tapiji.translator.rap.babel.editor.resource.EclipsePropertiesEditorResource;
import org.eclipselabs.tapiji.translator.rap.babel.editor.util.UIUtils;
import org.eclipselabs.tapiji.translator.rap.babel.editor.views.MessagesBundleGroupOutline;
import org.eclipselabs.tapiji.translator.rap.extResources.TextEditor;

/**
 * Multi-page editor for editing resource bundles.
 */
public class MessagesEditor extends MultiPageEditorPart implements IGotoMarker,
        IMessagesEditor {

	/** Editor ID, as defined in plugin.xml. */
	public static final String EDITOR_ID = "org.eclipse.babel.editor.MessagesEditor"; //$NON-NLS-1$

	private String selectedKey;
	private List<IMessagesEditorChangeListener> changeListeners = new ArrayList<IMessagesEditorChangeListener>(
	        2);

	/** MessagesBundle group. */
	private MessagesBundleGroup messagesBundleGroup;

	/** Page with key tree and text fields for all locales. */
	private I18NPage i18nPage;
	private final List<Locale> localesIndex = new ArrayList<Locale>();
	private final List<TextEditor> textEditorsIndex = new ArrayList<TextEditor>();
	
	private MessagesBundleGroupOutline outline;

	private MessagesEditorMarkers markers;

	private AbstractKeyTreeModel keyTreeModel;

	private IFile file; // init

	private boolean updateSelectedKey;

	/**
	 * Creates a multi-page editor example.
	 */
	public MessagesEditor() {
		super();
		outline = new MessagesBundleGroupOutline(this);
	}

	public MessagesEditorMarkers getMarkers() {
		return markers;
	}

	private IPropertyChangeListener preferenceListener;

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput)
	        throws PartInitException {

		if (editorInput instanceof IFileEditorInput) {
			file = ((IFileEditorInput) editorInput).getFile();
			if (MsgEditorPreferences.getInstance()
			        .isBuilderSetupAutomatically()) {
				IProject p = file.getProject();
				if (p != null && p.isAccessible()) {
					ToggleNatureAction
					        .addOrRemoveNatureOnProject(p, true, true);
				}
			}
			try {
				messagesBundleGroup = MessagesBundleGroupFactory
				        .createBundleGroup(site, file);
			} catch (MessageException e) {
				throw new PartInitException("Cannot create bundle group.", e); //$NON-NLS-1$
			}
			markers = new MessagesEditorMarkers(messagesBundleGroup);
			setPartName(messagesBundleGroup.getName());
			setTitleImage(UIUtils.getImage(UIUtils.IMAGE_RESOURCE_BUNDLE));
			closeIfAreadyOpen(site, file);
			super.init(site, editorInput);
			// TODO figure out model to use based on preferences
			keyTreeModel = new AbstractKeyTreeModel(messagesBundleGroup);
			// markerManager = new RBEMarkerManager(this);
		} else {
			throw new PartInitException(
			        "Invalid Input: Must be IFileEditorInput"); //$NON-NLS-1$
		}
	}

	// public RBEMarkerManager getMarkerManager() {
	// return markerManager;
	// }

	/**
	 * Creates the pages of the multi-page editor.
	 */
	@Override
	protected void createPages() {
		// Create I18N page
		i18nPage = new I18NPage(getContainer(), SWT.NONE, this);
		int index = addPage(i18nPage);
		setPageText(index, MessagesEditorPlugin.getString("editor.properties")); //$NON-NLS-1$
		setPageImage(index, UIUtils.getImage(UIUtils.IMAGE_RESOURCE_BUNDLE));

		// Create text editor pages for each locales
		try {
			Locale[] locales = messagesBundleGroup.getLocales();
			// first: sort the locales.
			UIUtils.sortLocales(locales);
			// second: filter+sort them according to the filter preferences.
			locales = UIUtils.filterLocales(locales);
			for (int i = 0; i < locales.length; i++) {
				Locale locale = locales[i];
				MessagesBundle messagesBundle = (MessagesBundle) messagesBundleGroup
				        .getMessagesBundle(locale);
				IMessagesResource resource = messagesBundle.getResource();
				TextEditor textEditor = (TextEditor) resource.getSource();
				index = addPage(textEditor, textEditor.getEditorInput());
				setPageText(index,
				        UIUtils.getDisplayName(messagesBundle.getLocale()));
				setPageImage(index,
				        UIUtils.getImage(UIUtils.IMAGE_PROPERTIES_FILE));
				localesIndex.add(locale);
				textEditorsIndex.add(textEditor);
			}
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
			        "Error creating text editor page.", //$NON-NLS-1$
			        null, e.getStatus());
		}
	}

	/**
	 * Called when the editor's pages need to be reloaded. For example when the
	 * filters of locale is changed.
	 * <p>
	 * Currently this only reloads the index page. TODO: remove and add the new
	 * locales? it actually looks quite hard to do.
	 * </p>
	 */
	public void reloadDisplayedContents() {
		super.removePage(0);
		int currentlyActivePage = super.getActivePage();
		i18nPage.dispose();
		i18nPage = new I18NPage(getContainer(), SWT.NONE, this);
		super.addPage(0, i18nPage);
		if (currentlyActivePage == 0) {
			super.setActivePage(currentlyActivePage);
		}
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		for (TextEditor textEditor : textEditorsIndex) {
			textEditor.doSave(monitor);
		}

		try { // [alst] remove in near future
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateSelectedKey = true;

		RBManager instance = RBManager.getInstance(messagesBundleGroup
		        .getProjectName());

		refreshKeyTreeModel(); // keeps editor and I18NPage in sync

		instance.fireEditorSaved();

		// // maybe new init?
	}

	private void refreshKeyTreeModel() {
		String selectedKey = getSelectedKey(); // memorize

		if (messagesBundleGroup == null) {
			messagesBundleGroup = MessagesBundleGroupFactory.createBundleGroup(
			        (IEditorSite) getSite(), file);
		}

		AbstractKeyTreeModel oldModel = this.keyTreeModel;
		this.keyTreeModel = new AbstractKeyTreeModel(messagesBundleGroup);

		for (IMessagesEditorChangeListener listener : changeListeners) {
			listener.keyTreeModelChanged(oldModel, this.keyTreeModel);
		}

		i18nPage.getTreeViewer().expandAll();

		if (selectedKey != null) {
			setSelectedKey(selectedKey);
		}
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// Save As not allowed.
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Change current page based on locale. If there is no editors associated
	 * with current locale, do nothing.
	 * 
	 * @param locale
	 *            locale used to identify the page to change to
	 */
	public void setActivePage(Locale locale) {
		int index = localesIndex.indexOf(locale);
		if (index > -1) {
			setActivePage(index + 1);
		}
	}

	/**
	 * @see org.eclipse.ui.ide.IGotoMarker#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker marker) {
		// String key = marker.getAttribute(RBEMarker.KEY, "");
		// if (key != null && key.length() > 0) {
		// setActivePage(0);
		// setSelectedKey(key);
		// getI18NPage().selectLocale(BabelUtils.parseLocale(
		// marker.getAttribute(RBEMarker.LOCALE, "")));
		// } else {
		IResource resource = marker.getResource();
		Locale[] locales = messagesBundleGroup.getLocales();
		for (int i = 0; i < locales.length; i++) {
			IMessagesResource messagesResource = ((MessagesBundle) messagesBundleGroup
			        .getMessagesBundle(locales[i])).getResource();
			if (messagesResource instanceof EclipsePropertiesEditorResource) {
				EclipsePropertiesEditorResource propFile = (EclipsePropertiesEditorResource) messagesResource;
				if (resource.equals(propFile.getResource())) {
					// ok we got the locale.
					// try to open the master i18n page and select the
					// corresponding key.
					try {
						String key = (String) marker
						        .getAttribute(IMarker.LOCATION);
						if (key != null && key.length() > 0) {
							getI18NPage().selectLocale(locales[i]);
							setActivePage(0);
							setSelectedKey(key);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();// something better.s
					}
					// it did not work... fall back to the text editor.
					setActivePage(locales[i]);
					IDE.gotoMarker((IEditorPart) propFile.getSource(), marker);
					break;
				}
			}
		}
		// }
	}

	/**
	 * Calculates the contents of page GUI page when it is activated.
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex != 0) { // if we just want the default page -> == 1
			setSelection(newPageIndex);
		} else if (newPageIndex == 0 && updateSelectedKey) {
			// TODO: find better way
			for (IMessagesBundle bundle : messagesBundleGroup
			        .getMessagesBundles()) {
				RBManager.getInstance(messagesBundleGroup.getProjectName())
				        .fireResourceChanged(bundle);
			}
			updateSelectedKey = false;
		}

		// if (newPageIndex == 0) {
		// resourceMediator.reloadProperties();
		// i18nPage.refreshTextBoxes();
		// }
	}

	private void setSelection(int newPageIndex) {
		TextEditor editor = textEditorsIndex.get(--newPageIndex);
		String selectedKey = getSelectedKey();
		if (selectedKey != null) {
			if (editor.getEditorInput() instanceof FileEditorInput) {
				FileEditorInput input = (FileEditorInput) editor
				        .getEditorInput();
				try {
					BufferedReader reader = new BufferedReader(
					        new InputStreamReader(input.getFile().getContents()));
					String line = "";
					int selectionIndex = 0;
					boolean found = false;

					while ((line = reader.readLine()) != null) {
						int index = line.indexOf('=');
						if (index != -1) {
							if (selectedKey.equals(line.substring(0, index)
							        .trim())) {
								found = true;
								break;
							}
						}
						selectionIndex += line.length() + 2; // + \r\n
					}

					if (found) {
						// TODO [RAP] editor.selectAndReveal(selectionIndex, 0);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Is the given file a member of this resource bundle.
	 * 
	 * @param file
	 *            file to test
	 * @return <code>true</code> if file is part of bundle
	 */
	public boolean isBundleMember(IFile file) {
		// return resourceMediator.isResource(file);
		return false;
	}

	private void closeIfAreadyOpen(IEditorSite site, IFile file) {
		IWorkbenchPage[] pages = site.getWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPage page = pages[i];
			IEditorReference[] editors = page.getEditorReferences();
			for (int j = 0; j < editors.length; j++) {
				IEditorPart editor = editors[j].getEditor(false);
				if (editor instanceof MessagesEditor) {
					MessagesEditor rbe = (MessagesEditor) editor;
					if (rbe.isBundleMember(file)) {
						page.closeEditor(editor, true);
					}
				}
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		for (IMessagesEditorChangeListener listener : changeListeners) {
			listener.editorDisposed();
		}
		if (i18nPage != null)
			i18nPage.dispose();
		for (TextEditor textEditor : textEditorsIndex) {
			textEditor.dispose();
		}
	}

	/**
	 * @return Returns the selectedKey.
	 */
	public String getSelectedKey() {
		return selectedKey;
	}

	/**
	 * @param selectedKey
	 *            The selectedKey to set.
	 */
	public void setSelectedKey(String activeKey) {
		if ((selectedKey == null && activeKey != null)
		        || (selectedKey != null && activeKey == null)
		        || (selectedKey != null && !selectedKey.equals(activeKey))) {
			String oldKey = this.selectedKey;
			this.selectedKey = activeKey;
			for (IMessagesEditorChangeListener listener : changeListeners) {
				listener.selectedKeyChanged(oldKey, activeKey);
			}
		}
	}

	public void addChangeListener(IMessagesEditorChangeListener listener) {
		changeListeners.add(0, listener);
	}

	public void removeChangeListener(IMessagesEditorChangeListener listener) {
		changeListeners.remove(listener);
	}

	public Collection<IMessagesEditorChangeListener> getChangeListeners() {
		return changeListeners;
	}

	/**
	 * @return Returns the messagesBundleGroup.
	 */
	public MessagesBundleGroup getBundleGroup() {
		return messagesBundleGroup;
	}

	/**
	 * @return Returns the keyTreeModel.
	 */
	public AbstractKeyTreeModel getKeyTreeModel() {
		return keyTreeModel;
	}

	/**
	 * @param keyTreeModel
	 *            The keyTreeModel to set.
	 */
	public void setKeyTreeModel(AbstractKeyTreeModel newKeyTreeModel) {
		if ((this.keyTreeModel == null && newKeyTreeModel != null)
		        || (keyTreeModel != null && newKeyTreeModel == null)
		        || (!keyTreeModel.equals(newKeyTreeModel))) {
			AbstractKeyTreeModel oldModel = this.keyTreeModel;
			this.keyTreeModel = newKeyTreeModel;
			for (IMessagesEditorChangeListener listener : changeListeners) {
				listener.keyTreeModelChanged(oldModel, newKeyTreeModel);
			}
		}
	}

	public I18NPage getI18NPage() {
		return i18nPage;
	}

	/**
	 * one of the SHOW_* constants defined in the
	 * {@link IMessagesEditorChangeListener}
	 */
	private int showOnlyMissingAndUnusedKeys = IMessagesEditorChangeListener.SHOW_ALL;

	/**
	 * @return true when only unused and missing keys should be displayed. flase
	 *         by default.
	 */
	public int isShowOnlyUnusedAndMissingKeys() {
		return showOnlyMissingAndUnusedKeys;
	}

	public void setShowOnlyUnusedMissingKeys(int showFlag) {
		showOnlyMissingAndUnusedKeys = showFlag;
		for (IMessagesEditorChangeListener listener : getChangeListeners()) {
			listener.showOnlyUnusedAndMissingChanged(showFlag);
		}
	}

	@Override
	public Object getAdapter(Class adapter) {
		Object obj = super.getAdapter(adapter);
		if (obj == null) {
			if (IContentOutlinePage.class.equals(adapter)) {
				return (outline);
			}
		}
		return (obj);
	}

	public TextEditor getTextEditor(Locale locale) {
		int index = localesIndex.indexOf(locale);
		return textEditorsIndex.get(index);
	}
}
