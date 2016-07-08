package org.eclipselabs.e4.tapiji.translator.model;


/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon
 ******************************************************************************/


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;


@XmlAccessorType(XmlAccessType.FIELD)
public class Term implements Serializable {

    private static final long serialVersionUID = 7004998590181568026L;

    @XmlElementWrapper(name = "translations")
    @XmlElement(name = "translation")
    public List<Translation> translations;

    @XmlElementWrapper(name = "terms")
    @XmlElement(name = "term")
    public List<Term> subTerms;

    public Term parentTerm;

    @XmlTransient
    private Object info;

    private Term() {
        this(new ArrayList<Translation>(), new ArrayList<Term>(), null, null);
    }
    
    public Term(final List<Translation> translations, final List<Term> subTerms, final Term parentTerm, final Info info) {
        this.translations = translations;
        this.subTerms = subTerms;
        this.parentTerm = parentTerm;
        this.info = info;
    }

    public void setInfo(final Object info) {
        this.info = info;
    }

    public Object getInfo() {
        return info;
    }

    public Term[] getAllSubTerms() {
        return subTerms.toArray(new Term[subTerms.size()]);
    }

    public Term getParentTerm() {
        return parentTerm;
    }

    public boolean hasChildTerms() {
        return (subTerms != null) && (subTerms.size() > 0);
    }

    public Translation[] getTranslations() {
        return translations.toArray(new Translation[translations.size()]);
    }

    public Translation getTranslation(final String language) {
        for (final Translation translation : translations) {
            if (translation.id.equalsIgnoreCase(language)) {
                return translation;
            }
        }

        final Translation newTranslation = Translation.create();
        newTranslation.id = language;
        translations.add(newTranslation);

        return newTranslation;
    }

    public boolean removeTerm(final Term elem) {
        boolean hasFound = false;
        for (final Term subTerm : subTerms) {
            if (subTerm == elem) {
                subTerms.remove(elem);
                hasFound = true;
                break;
            } else {
                hasFound = subTerm.removeTerm(elem);
                if (hasFound) {
                    break;
                }
            }
        }
        return hasFound;
    }

    public boolean addTerm(final Term parentTerm, final Term newTerm) {
        boolean hasFound = false;
        for (final Term subTerm : subTerms) {
            if (subTerm == parentTerm) {
                subTerms.add(newTerm);
                hasFound = true;
                break;
            } else {
                hasFound = subTerm.addTerm(parentTerm, newTerm);
                if (hasFound) {
                    break;
                }
            }
        }
        return hasFound;
    }

    private void addTranslation(Translation translation) {
        this.translations.add(translation);
    }
    
    @Override
    public String toString() {
        return "Term [translations=" + translations + ", subTerms=" + subTerms + ", parentTerm=" + parentTerm + ", info=" + info + "]";
    }

    public static Term newInstance(Translation translation) {
        Term term = newInstance();
        term.addTranslation(translation);
        return term;
    }

    public static Term newInstance() {
        return new Term();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((info == null) ? 0 : info.hashCode());
        result = prime * result + ((parentTerm == null) ? 0 : parentTerm.hashCode());
        result = prime * result + ((subTerms == null) ? 0 : subTerms.hashCode());
        result = prime * result + ((translations == null) ? 0 : translations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Term other = (Term) obj;
        if (info == null) {
            if (other.info != null) return false;
        } else if (!info.equals(other.info)) return false;
        if (parentTerm == null) {
            if (other.parentTerm != null) return false;
        } else if (!parentTerm.equals(other.parentTerm)) return false;
        if (subTerms == null) {
            if (other.subTerms != null) return false;
        } else if (!subTerms.equals(other.subTerms)) return false;
        if (translations == null) {
            if (other.translations != null) return false;
        } else if (!translations.equals(other.translations)) return false;
        return true;
    }
    
    
}
