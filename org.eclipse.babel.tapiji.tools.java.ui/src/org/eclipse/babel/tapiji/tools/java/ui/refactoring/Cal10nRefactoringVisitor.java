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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEdit;

/**
 * Refactors java files, which are using the old enumeration key that gets
 * refactored.
 * 
 * @author Alexej Strelzow
 */
public class Cal10nRefactoringVisitor extends ASTVisitor {

    private List<String> changeSet = new ArrayList<String>();

    private String oldKey;
    private String newKey;
    private String enumPath;

    private CompilationUnit cu;
    private AST ast;
    private ASTRewrite rewriter;

    /**
     * Constructor.
     * 
     * @param oldKey
     *            The old key name
     * @param newKey
     *            The new key name, which should overwrite the old one
     * @param enumPath
     *            The path of the enum file to change
     * @param cu
     *            The {@link CompilationUnit} to modify
     * @param changeSet
     *            The set of changes (protocol)
     */
    public Cal10nRefactoringVisitor(CompilationUnit cu, String oldKey,
            String newKey, String enumPath, List<String> changeSet) {
        this.oldKey = oldKey;
        this.newKey = newKey;
        this.enumPath = enumPath;
        this.cu = cu;
        this.changeSet = changeSet;
        this.ast = cu.getAST();
        this.rewriter = ASTRewrite.create(ast);
    }

    /**
     * Changes the argument of the IMessageConveyor#getMessage(...) call. The
     * new enum key replaces the old one.
     */
    @SuppressWarnings("unchecked")
    public boolean visit(MethodInvocation node) {

        boolean isCal10nCall = "ch.qos.cal10n.IMessageConveyor".equals(node
                .resolveMethodBinding().getDeclaringClass().getQualifiedName());

        if (!isCal10nCall) {
            if (node.arguments().size() == 0) {
                return false;
            } else {
                return true; // because the Cal10n call may be an argument!
            }
        } else {
            QualifiedName qName = (QualifiedName) node.arguments().get(0);
            String fullPath = qName.resolveTypeBinding().getJavaElement()
                    .getResource().getFullPath().toPortableString();
            if (fullPath.equals(enumPath)
                    && qName.getName().toString().equals(oldKey)) {

                // ASTRewrite
                Expression exp = (Expression) rewriter.createCopyTarget(node
                        .getExpression());

                MethodInvocation newMethod = ast.newMethodInvocation();
                newMethod.setExpression(exp);
                newMethod.setName(ast.newSimpleName(node.getName()
                        .getIdentifier()));

                SimpleName newSimpleName = ast.newSimpleName(newKey);
                Name newName = ast.newName(qName.getQualifier()
                        .getFullyQualifiedName());
                QualifiedName newQualifiedName = ast.newQualifiedName(newName,
                        newSimpleName);

                newMethod.arguments().add(newQualifiedName);
                rewriter.replace(node, newMethod, null);

                // protocol
                int startPos = node.getStartPosition();
                ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
                changeSet.add(icu.getPath().toPortableString() + ": line "
                        + cu.getLineNumber(startPos));
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
}
