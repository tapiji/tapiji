package org.eclipse.babel.tapiji.tools.java.ui.util;

import java.util.Locale;

import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

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
			ASTutils.createReplaceNonInternationalisationComment(cu, document, startPos);
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
			ASTutils.createReplaceNonInternationalisationComment(cu, document, offset);
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
	
}
