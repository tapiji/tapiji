package org.eclipselabs.e4.tapiji.translator.model;


/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Christian Behon
 ******************************************************************************/


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
@Singleton
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Glossary implements Serializable {

    private static final long serialVersionUID = 2070750758712154134L;

    public Info info;

    @XmlElementWrapper(name = "terms")
    @XmlElement(name = "term")
    public List<Term> terms;

    public Glossary() {
        this.terms = new ArrayList<Term>();
        this.info = Info.create();
    }

    public synchronized Term[] getAllTerms() {
        return terms.toArray(new Term[terms.size()]);
    }

    public int getIndexOfLocale(final String referenceLocale) {
        int i = 0;

        for (final String locale : info.translations) {
            if (locale.equalsIgnoreCase(referenceLocale)) {
                return i;
            }
            i++;
        }

        return 0;
    }

    public void removeTerm(final Term elem) {
        synchronized (terms) {
            for (final Term term : terms) {
                if (term == elem) {
                    terms.remove(term);
                    break;
                }

                if (term.removeTerm(elem)) {
                    break;
                }
            }
        }
    }

    public void addTerm(final Term parentTerm, final Term newTerm) {
        synchronized (terms) {
            if (parentTerm == null) {
                this.terms.add(newTerm);
                return;
            }

            for (final Term term : terms) {
                if (term == parentTerm) {
                    term.subTerms.add(newTerm);
                    break;
                }

                if (term.addTerm(parentTerm, newTerm)) {
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Glossary [info=" + info + ", terms=" + terms + "]";
    }
}
