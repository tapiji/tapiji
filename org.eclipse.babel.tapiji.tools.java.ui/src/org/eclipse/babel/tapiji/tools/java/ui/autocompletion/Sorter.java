/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.jdt.ui.text.java.AbstractProposalSorter;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class Sorter extends AbstractProposalSorter {

    public Sorter() {
    }

    @Override
    public void beginSorting(ContentAssistInvocationContext context) {
        // TODO Auto-generated method stub
        super.beginSorting(context);
    }

    @Override
    public int compare(ICompletionProposal prop1, ICompletionProposal prop2) {
        return getIndex(prop1) - getIndex(prop2);
    }

    protected int getIndex(ICompletionProposal prop) {
        if (prop instanceof NoActionProposal) {
            return 1;
        } else if (prop instanceof MessageCompletionProposal) {
            return 2;
        } else if (prop instanceof InsertResourceBundleReferenceProposal) {
            return 3;
        } else if (prop instanceof NewResourceBundleEntryProposal) {
            return 4;
        } else {
            return 0;
        }
    }

}
