package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.jdt.ui.text.java.AbstractProposalSorter;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class Sorter extends AbstractProposalSorter {

    private boolean loaded = false;

    public Sorter() {
	// i18n
	loaded = true;
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
	if (prop instanceof NoActionProposal)
	    return 1;
	else if (prop instanceof MessageCompletionProposal)
	    return 2;
	else if (prop instanceof InsertResourceBundleReferenceProposal)
	    return 3;
	else if (prop instanceof NewResourceBundleEntryProposal)
	    return 4;
	else
	    return 0;
    }

}
