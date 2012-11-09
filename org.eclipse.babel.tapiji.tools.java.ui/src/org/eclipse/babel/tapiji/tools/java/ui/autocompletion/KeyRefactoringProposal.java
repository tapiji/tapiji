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
package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Proposal for the key refactoring. The gets triggerd if you press Ctrl + Shift
 * + Space on an externalized (!) {@link String} or on an externalized (!) enum!
 * The key must be registered in the system!
 * 
 * @author Alexej Strelzow
 */
public class KeyRefactoringProposal implements IJavaCompletionProposal {

    private int startPos;
    private String value;
    private String projectName;
    private String bundleName;
    private String reference;
    private String enumPath;

    /**
     * Constructor for non Cal10n refactoring.
     * 
     * @param startPos
     *            The starting position of the Ctrl + Shift + Space command.
     * @param value
     *            The value of the key to refactor.
     * @param projectName
     *            The project the resource bundle is in.
     * @param bundleName
     *            The resource bundle, which contains the key to be refactored.
     */
    public KeyRefactoringProposal(int startPos, String value,
            String projectName, String bundleName) {

        this.startPos = startPos;
        this.value = value;
        this.projectName = projectName;
        this.bundleName = bundleName;
    }

    /**
     * Constructor for Cal10n refactoring.
     * 
     * @param startPos
     *            The starting position of the Ctrl + Shift + Space command.
     * @param value
     *            The value of the key to refactor.
     * @param projectName
     *            The project the resource bundle is in.
     * @param bundleName
     *            The resource bundle, which contains the key to be refactored.
     * @param enumPath
     *            The {@link IPath#toPortableString()} of the enum to change
     */
    public KeyRefactoringProposal(int startPos, String value,
            String projectName, String bundleName, String enumPath) {
        this(startPos, value, projectName, bundleName);
        this.enumPath = enumPath; // relative path (to the project)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(IDocument document) {
        RBManager.getRefactorService().openRefactorDialog(projectName,
                bundleName, value, enumPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getSelection(IDocument document) {
        int refLength = reference == null ? 0 : reference.length() - 1;
        return new Point(startPos + refLength, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdditionalProposalInfo() {

        if (enumPath != null) {
            return "Replace this enum key with a new one! \r\n"
                    + "This operation will automatically replace all references to the selected key "
                    + "with the new one. Also the enum value will be changed!";
        } else {
            return "Replace this key with a new one! \r\n"
                    + "This operation will automatically replace all references to the selected key "
                    + "with the new one.";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayString() {
        return "Refactor this key...";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage() {
        return UIUtils.getImageDescriptor(UIUtils.IMAGE_REFACTORING)
                .createImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelevance() {
        return 100;
    }

}
