/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexej Strelzow - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

/**
 * Changes the enum class, which is referenced by the Cal10n framework.
 * It replaces the old key with the new one.
 * 
 * @author Alexej Strelzow
 */
public class Cal10nEnumRefactoringVisitor extends ASTVisitor {

	private List<String> changeSet = new ArrayList<String>();
	
	private String oldKey;
	private String newKey;
	private CompilationUnit enumCu;
	
	/**
	 * Constructor.
	 * @param oldKey The old key name
	 * @param newKey The new key name, which should overwrite the old one
	 * @param enumCu The {@link CompilationUnit} to modify
	 * @param changeSet The set of changes (protocol)
	 */
	public Cal10nEnumRefactoringVisitor(CompilationUnit enumCu, 
			String oldKey, String newKey, List<String> changeSet) {
		this.oldKey = oldKey;
		this.newKey = newKey;
		this.enumCu = enumCu;
		this.changeSet = changeSet;
	}
	
	/**
	 * Modifies the enum file. It replaces the old key with the new one.
	 */
	public boolean visit(EnumConstantDeclaration node) {
		
		if (node.resolveVariable().getName().equals(oldKey)) {
			
			// ASTRewrite
			AST ast = enumCu.getAST();
			ASTRewrite rewriter = ASTRewrite.create(ast);
            
			EnumConstantDeclaration newDeclaration = ast.newEnumConstantDeclaration();
			
            SimpleName newSimpleName = ast.newSimpleName(newKey);
            newDeclaration.setName(newSimpleName);
            
            rewriter.replace(node, newDeclaration, null);
            
            try {
                TextEdit textEdit = rewriter.rewriteAST();
                if (textEdit.hasChildren()) { // if the compilation unit has been
                                              // changed
                    ICompilationUnit icu = (ICompilationUnit) enumCu.getJavaElement();
                    icu.applyTextEdit(textEdit, null);
                    icu.getBuffer().save(null, true);
                    
                    // protocol
                    int startPos = node.getStartPosition();
                    changeSet.add(icu.getPath().toPortableString() + ": line " + enumCu.getLineNumber(startPos));
                }
            } catch (Exception e) {
            	Logger.logError(e);
            }
		}
		
		return false;
	};
}
