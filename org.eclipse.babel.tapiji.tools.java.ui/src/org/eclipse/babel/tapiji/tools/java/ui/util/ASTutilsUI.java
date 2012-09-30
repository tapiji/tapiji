/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer, Alexej Strelzow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Alexej Strelzow - seperation of ui/non-ui (methods moved from ASTUtils)
 ******************************************************************************/

package org.eclipse.babel.tapiji.tools.java.ui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.babel.core.message.IMessagesBundle;
import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.KeyRefactoringDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.KeyRefactoringDialog.DialogConfiguration;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.KeyRefactoringSummaryDialog;
import org.eclipse.babel.tapiji.tools.core.ui.utils.LocaleUtils;
import org.eclipse.babel.tapiji.tools.java.ui.refactoring.Cal10nEnumRefactoringVisitor;
import org.eclipse.babel.tapiji.tools.java.ui.refactoring.Cal10nRefactoringVisitor;
import org.eclipse.babel.tapiji.tools.java.ui.refactoring.PrimitiveRefactoringVisitor;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;

public class ASTutilsUI {

    public static CompilationUnit getCompilationUnit(IResource resource) {
	IJavaElement je = JavaCore.create(resource,
		JavaCore.create(resource.getProject()));
	// get the type of the currently loaded resource
	ITypeRoot typeRoot = ((ICompilationUnit) je);

	if (typeRoot == null) {
	    return null;
	}

	return getCompilationUnit(typeRoot);
    }

    public static CompilationUnit getCompilationUnit(ITypeRoot typeRoot) {
	// get a reference to the shared AST of the loaded CompilationUnit
	CompilationUnit cu = SharedASTProvider.getAST(typeRoot,
	// do not wait for AST creation
		SharedASTProvider.WAIT_YES, null);

	return cu;
    }

    public static String insertNewBundleRef(IDocument document,
	    IResource resource, int startPos, int endPos,
	    String resourceBundleId, String key) {
	String newName = null;
	String reference = "";

	CompilationUnit cu = getCompilationUnit(resource);

	if (cu == null) {
	    return null;
	}

	String variableName = ASTutils.resolveRBReferenceVar(document,
		resource, startPos, resourceBundleId, cu);
	if (variableName == null) {
	    newName = ASTutils.getNonExistingRBRefName(resourceBundleId,
		    document, cu);
	}

	try {
	    reference = ASTutils.createResourceReference(resourceBundleId, key,
		    null, resource, startPos, variableName == null ? newName
			    : variableName, document, cu);

	    if (startPos > 0 && document.get().charAt(startPos - 1) == '\"') {
		startPos--;
		endPos++;
	    }

	    if ((startPos + endPos) < document.getLength()
		    && document.get().charAt(startPos + endPos) == '\"') {
		endPos++;
	    }

	    if ((startPos + endPos) < document.getLength()
		    && document.get().charAt(startPos + endPos - 1) == ';') {
		endPos--;
	    }

	    document.replace(startPos, endPos, reference);

	    // create non-internationalisation-comment
	    ASTutils.createReplaceNonInternationalisationComment(cu, document,
		    startPos);
	} catch (BadLocationException e) {
	    e.printStackTrace();
	}

	if (variableName == null) {
	    // refresh reference to the shared AST of the loaded CompilationUnit
	    cu = getCompilationUnit(resource);

	    ASTutils.createResourceBundleReference(resource, startPos,
		    document, resourceBundleId, null, true, newName, cu);
	    // createReplaceNonInternationalisationComment(cu, document, pos);
	}

	return reference;
    }

    public static String insertExistingBundleRef(IDocument document,
	    IResource resource, int offset, int length,
	    String resourceBundleId, String key, Locale locale) {
	String reference = "";
	String newName = null;

	CompilationUnit cu = getCompilationUnit(resource);

	String variableName = ASTutils.resolveRBReferenceVar(document,
		resource, offset, resourceBundleId, cu);
	if (variableName == null) {
	    newName = ASTutils.getNonExistingRBRefName(resourceBundleId,
		    document, cu);
	}

	try {
	    reference = ASTutils.createResourceReference(resourceBundleId, key,
		    locale, resource, offset, variableName == null ? newName
			    : variableName, document, cu);

	    document.replace(offset, length, reference);

	    // create non-internationalisation-comment
	    ASTutils.createReplaceNonInternationalisationComment(cu, document,
		    offset);
	} catch (BadLocationException e) {
	    e.printStackTrace();
	}

	// TODO retrieve cu in the same way as in createResourceReference
	// the current version does not parse method bodies

	if (variableName == null) {
	    ASTutils.createResourceBundleReference(resource, offset, document,
		    resourceBundleId, locale, true, newName, cu);
	    // createReplaceNonInternationalisationComment(cu, document, pos);
	}
	return reference;
    }

    /**
	 * Performs the refactoring of messages key. The key can be a {@link String} or an
	 * Enumeration! If it is an enumeration, then the enumPath needs to be provided!
	 * 
	 * @param projectName The name of the project, where the resource bundle file is in
	 * @param resourceBundleId The Id of the resource bundle, which contains the old key
	 * @param selectedLocale The {@link Locale} to change
	 * @param oldKey The name of the key to change
	 * @param newKey The name of the key, which replaces the old one
	 * @param enumPath The path of the enum file (needs: {@link IPath#toPortableString()})
	 */
	public static void refactorKey(final String projectName, final String resourceBundleId, 
			final String selectedLocale, final String oldKey, final String newKey, final String enumPath) {
		
		// contains file and line
		final List<String> changeSet = new ArrayList<String>();
		
		ResourceBundleManager manager = ResourceBundleManager.getManager(projectName);
		IProject project = manager.getProject();
		
		try {
			project.accept(new IResourceVisitor() {
				
				/**
				 * First step of filtering. Only classes, which import java.util.ResourceBundle
				 * or ch.qos.cal10n.MessageConveyor will be changed. An exception is the
				 * enum file, which gets referenced by the Cal10n framework.
				 * 
				 * {@inheritDoc}
				 */
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (!(resource instanceof IFile) || !resource.getFileExtension().equals("java")) {
						return true;
					}
					
					final CompilationUnit cu = getCompilationUnit(resource);
					
					// step 1: import filter
					for (Object obj : cu.imports()) {
						ImportDeclaration imp = (ImportDeclaration) obj;
						String importName = imp.getName().toString();
						if ("java.util.ResourceBundle".equals(importName)) {
							PrimitiveRefactoringVisitor prv = new PrimitiveRefactoringVisitor(cu,
									resourceBundleId, oldKey, newKey, changeSet);
							cu.accept(prv);
							prv.saveChanges();
							break;
						} else if ("ch.qos.cal10n.MessageConveyor".equals(importName)) { // Cal10n
							Cal10nRefactoringVisitor crv = new Cal10nRefactoringVisitor(cu,
									oldKey, newKey, enumPath, changeSet);
							cu.accept(crv);
							crv.saveChanges();
							break;
						}
					}
					
					return false;
				}
			});
		} catch (CoreException e) {
			Logger.logError(e);
		}
		
		if (enumPath != null) { // Cal10n support, change the enum file
			IFile file = project.getFile(enumPath.substring(project.getName().length() + 1));
			final CompilationUnit enumCu = getCompilationUnit(file);
			
			Cal10nEnumRefactoringVisitor enumVisitor = new Cal10nEnumRefactoringVisitor(enumCu, oldKey, newKey, changeSet);
			enumCu.accept(enumVisitor);
		}
		
		// change backend
		RBManager rbManager = RBManager.getInstance(projectName);
		IMessagesBundleGroup messagesBundleGroup = rbManager.getMessagesBundleGroup(resourceBundleId);
		
		if (KeyRefactoringDialog.ALL_LOCALES.equals(selectedLocale)) {
			messagesBundleGroup.renameMessageKeys(oldKey, newKey);
		} else {
			IMessagesBundle messagesBundle = messagesBundleGroup.getMessagesBundle(LocaleUtils
					.getLocaleByDisplayName(manager.getProvidedLocales(resourceBundleId), selectedLocale));
			messagesBundle.renameMessageKey(oldKey, newKey);
//			rbManager.fireResourceChanged(messagesBundle); ??
		}
		rbManager.fireEditorChanged(); // notify Resource Bundle View
		
		// show the summary dialog
		KeyRefactoringSummaryDialog summaryDialog = new KeyRefactoringSummaryDialog(Display.getDefault().getActiveShell());
		
		DialogConfiguration config = summaryDialog.new DialogConfiguration();
		config.setPreselectedKey(oldKey);
		config.setNewKey(newKey);
		config.setPreselectedBundle(resourceBundleId);
		config.setProjectName(projectName);
		
		summaryDialog.setDialogConfiguration(config);
		summaryDialog.setChangeSet(changeSet);
		
		summaryDialog.open();
	}
    
}
