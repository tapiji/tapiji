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

import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.refactoring.IRefactoringService;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.KeyRefactoringDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.KeyRefactoringDialog.DialogConfiguration;
import org.eclipse.babel.tapiji.tools.java.ui.util.ASTutilsUI;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.babel.tapiji.tools.java.visitor.ResourceAuditVisitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;

/**
 * Service class, which can be used to execute key refactorings.
 * 
 * @author Alexej Strelzow
 */
public class RefactoringService implements IRefactoringService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void refactorKey(String projectName, String resourceBundleId,
            String selectedLocale, String oldKey, String newKey, String enumName) {
        ASTutilsUI.refactorKey(projectName, resourceBundleId, selectedLocale,
                oldKey, newKey, enumName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openRefactorDialog(String projectName, String resourceBundleId,
            String oldKey, String enumName) {

        KeyRefactoringDialog dialog = new KeyRefactoringDialog(Display
                .getDefault().getActiveShell());

        DialogConfiguration config = dialog.new DialogConfiguration();
        config.setPreselectedKey(oldKey);
        config.setPreselectedBundle(resourceBundleId);
        config.setProjectName(projectName);

        dialog.setDialogConfiguration(config);

        if (dialog.open() != InputDialog.OK) {
            return;
        }

        refactorKey(projectName, resourceBundleId, config.getSelectedLocale(),
                oldKey, config.getNewKey(), enumName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openRefactorDialog(IFile file, int selectionOffset) {

        String projectName = file.getProject().getName();
        CompilationUnit cu = ASTutilsUI.getCompilationUnit(file);

        StringLiteral literal = ASTutils.getStringLiteralAtPos(cu,
                selectionOffset);

        if (literal == null) { // check for Cal10n
            String[] metaData = ASTutils.getCal10nEnumLiteralDataAtPos(
                    projectName, cu, selectionOffset);
            if (metaData != null) {
                openRefactorDialog(projectName, metaData[0], metaData[1],
                        metaData[2]);
            }
        } else { // it's a String (not Cal10n)
            ResourceBundleManager manager = ResourceBundleManager
                    .getManager(projectName);
            ResourceAuditVisitor visitor = new ResourceAuditVisitor(file,
                    projectName);
            cu.accept(visitor);

            String oldKey = literal.getLiteralValue();
            IRegion region = visitor.getKeyAt(new Long(selectionOffset));
            String bundleName = visitor.getBundleReference(region);
            if (bundleName != null) {
                IMessagesBundleGroup resourceBundle = manager
                        .getResourceBundle(bundleName);
                if (resourceBundle.containsKey(oldKey)) {
                    String resourceBundleId = resourceBundle
                            .getResourceBundleId();

                    openRefactorDialog(projectName, resourceBundleId, oldKey,
                            null);
                }
            }
        }
    }
}
