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
import java.util.ListIterator;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

/**
 * Executes the refactoring operation. That is, it changes the
 * {@link CompilationUnit} and saves the changes via {@link ASTRewrite}.
 * 
 * @author Alexej Strelzow
 */
public class PrimitiveRefactoringVisitor extends ASTVisitor {
	
	private String varNameOfBundle; // name of the variable, which references the resource bundle
	private List<String> changeSet = new ArrayList<String>();
	
	private String oldKey;
	private String newKey;
	private String resourceBundleId;
	
	private CompilationUnit cu;
	private AST ast;
	private ASTRewrite rewriter;
	
	/**
	 * Constructor.
	 * @param cu The {@link CompilationUnit} to modify
	 * @param resourceBundleId The Id of the resource bundle to change
	 * @param oldKey The old key name
	 * @param newKey The new key name, which should overwrite the old one
	 * @param changeSet The set of changes (protocol)
	 */
	public PrimitiveRefactoringVisitor(CompilationUnit cu, String resourceBundleId, 
			String oldKey, String newKey, List<String> changeSet) {
		this.oldKey = oldKey;
		this.newKey = newKey;
		this.resourceBundleId = resourceBundleId;
		this.cu = cu;
		this.changeSet = changeSet;
		this.ast = cu.getAST();
		this.rewriter = ASTRewrite.create(ast);
	}
	
	/**
	 * GLOBAL VARS:<br>
	 * <br>
	 * Find the variable name of the bundle (if exists!)
	 * E.g.:
	 * <pre>
	 * 	static ResourceBundle applicationresourcesref = ResourceBundle
	 *  		.getBundle("dev.ApplicationResources");
	 * </pre>
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(FieldDeclaration node) {
		for (Object obj : node.fragments()) {
			String val = getVarNameOfBundle((VariableDeclarationFragment) obj, resourceBundleId);

			if (val != null) {
				varNameOfBundle = val;
			}
		}
		return false;
	}
	
	/**
	 * LOCAL VARS: <br>
	 * <br>
	 * Find the variable name of the bundle (if exists!)
	 * E.g.:
	 * <pre>
	 * 	ResourceBundle applicationresourcesref = ResourceBundle
	 *  		.getBundle("dev.ApplicationResources");
	 * </pre>
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		for (Object obj : node.fragments()) {
			String val = getVarNameOfBundle((VariableDeclarationFragment) obj, resourceBundleId);
			
			if (val != null) {
				varNameOfBundle = val;
			}
			
			// [alst] because visit(MethodInvocation node) does not work when the MethodInvocation is embedded
			// in an Initializer (I don't know why...)
			if (!node.fragments().isEmpty()) {
				List fragments = node.fragments();
				ListIterator listIterator = fragments.listIterator();
				while (listIterator.hasNext()) {
					Object element = listIterator.next();
					if (element instanceof VariableDeclarationFragment) {
						VariableDeclarationFragment fragment = (VariableDeclarationFragment) element;
						Expression initializer = fragment.getInitializer();
						if (initializer != null && initializer instanceof MethodInvocation) {
							visit((MethodInvocation)initializer);
						}
					}
				}
			}
			
		}
		return false;
	}
	
	@Override
	public boolean visit(StringLiteral node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	/**
	 * Searches for the {@link MethodInvocation}, which needs to be modified.
	 * It gets only executed, if the variable name of the resource bundle has been
	 * found. E.g. applicationresourcesref:
	 * <pre>
	 * ResourceBundle applicationresourcesref = ResourceBundle
	 *  		.getBundle("dev.ApplicationResources");
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodInvocation node) {
		if (varNameOfBundle == null) {
			return false;
		}
		
		boolean isResourceBundle = "java.util.ResourceBundle".equals(
				node.resolveMethodBinding().getDeclaringClass().getQualifiedName());
		
		if (!isResourceBundle) {
			if (node.arguments().size() == 0) {
				return false;
			} else {
				return true; // because the call (we are searching for) may be an argument!
			}
		} else {
			// check the name of the caller, is it our resource bundle?
			if (node.getExpression() instanceof SimpleName
					&& varNameOfBundle.equals(
							node.getExpression().toString())) {
	
				// is the parameter the old key?
				StringLiteral literal = (StringLiteral) node.arguments().get(0);
	
				if (oldKey.equals(literal.getLiteralValue())) {
					
					// ASTRewrite
	                Expression exp = (Expression) rewriter.createCopyTarget(node.getExpression());
	                
	                MethodInvocation newMethod = ast.newMethodInvocation();
	                newMethod.setExpression(exp);
	                newMethod.setName(ast.newSimpleName(node.getName().getIdentifier()));
	                
	                StringLiteral newStringLiteral = ast.newStringLiteral();
	                newStringLiteral.setLiteralValue(newKey);
	                newMethod.arguments().add(newStringLiteral);
	                rewriter.replace(node, newMethod, null);
	                
	                // protocol
	                int startPos = node.getStartPosition();
	                ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
	                changeSet.add(icu.getPath().toPortableString() + ": line " + cu.getLineNumber(startPos));
				}
			}
		}
		return false;
	}
	
	/**
	 * Saves the changes, which were made in {@link #visit(MethodInvocation)}
	 */
	public void saveChanges() {
		try {
            TextEdit textEdit = rewriter.rewriteAST();
            if (textEdit.hasChildren()) { // if the compilation unit has been
                                          // changed
                ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
                icu.applyTextEdit(textEdit, null);
                icu.getBuffer().save(null, true);
            }
        } catch (Exception e) {
        	Logger.logError(e);
        }
	}
	
	private static String getVarNameOfBundle(VariableDeclarationFragment vdf, String resourceBundleId) {
		
		// Is the referenced bundle in there?
		if (vdf.getInitializer() != null && vdf.getInitializer() instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)vdf.getInitializer();
			
			// what's the name of the variable of the target res. bundle?
			for (Object arg : method.arguments()) {
				if (arg instanceof StringLiteral 
						&& resourceBundleId.equals(((StringLiteral)arg).getLiteralValue())) {
					return vdf.getName().toString();
				}
			}
		}
		
		return null;
	}
}
