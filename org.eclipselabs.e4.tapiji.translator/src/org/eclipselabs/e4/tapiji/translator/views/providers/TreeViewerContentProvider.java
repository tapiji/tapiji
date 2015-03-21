package org.eclipselabs.e4.tapiji.translator.views.providers;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;


public class TreeViewerContentProvider implements ITreeContentProvider {

    private final boolean grouped = false;

    private Glossary glossary;


    public TreeViewerContentProvider() {
        super();
        this.glossary = new Glossary();
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (newInput instanceof Glossary) {
            this.glossary = (Glossary) newInput;
        }
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        if (!grouped) {
            return getAllElements(glossary.terms).toArray(new Term[glossary.terms.size()]);
        }

        if (glossary != null) {
            return glossary.getAllTerms();
        }

        return null;
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (grouped) {
            if ((parentElement instanceof Term)) {
                final Term t = (Term) parentElement;
                return t.getAllSubTerms();
            }
        }
        return null;
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof Term) {
            return ((Term) element).getParentTerm();
        }
        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        if (grouped) {
            if (element instanceof Term) {
                return ((Term) element).hasChildTerms();
            }
        }
        return false;
    }

    public List<Term> getAllElements(final List<Term> terms) {
        final List<Term> allTerms = new ArrayList<Term>();
        if (terms != null) {
            for (final Term term : terms) {
                allTerms.add(term);
                allTerms.addAll(getAllElements(term.subTerms));
            }
        }
        return allTerms;
    }

    @Override
    public void dispose() {
        this.glossary = null;
    }

    public static TreeViewerContentProvider newInstance() {
        return new TreeViewerContentProvider();
    }
}
