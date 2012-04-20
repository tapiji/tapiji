/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType (XmlAccessType.FIELD)
public class Term implements Serializable {
	 
	private static final long serialVersionUID = 7004998590181568026L;

	@XmlElementWrapper (name = "translations")
	@XmlElement (name = "translation")
	public List<Translation> translations;
	
	@XmlElementWrapper (name = "terms")
	@XmlElement (name = "term")
	public List<Term> subTerms;
	
	public Term parentTerm;
	
	@XmlTransient
	private Object info;
	
	public Term () {
		translations = new ArrayList<Translation> ();
		subTerms = new ArrayList<Term> ();
		parentTerm = null;
		info = null;
	}
	
	public void setInfo(Object info) {
		this.info = info;
	}
	
	public Object getInfo() {
		return info;
	}
	
	public Term[] getAllSubTerms () {
		return subTerms.toArray(new Term[subTerms.size()]);
	}
	
	public Term getParentTerm() {
		return parentTerm;
	}
	
	public boolean hasChildTerms () {
		return subTerms != null && subTerms.size() > 0;
	}
	
	public Translation[] getAllTranslations() {
		return translations.toArray(new Translation [translations.size()]);
	}
	
	public Translation getTranslation (String language) {
		for (Translation translation : translations) {
			if (translation.id.equalsIgnoreCase(language))
				return translation;
		}
		
		Translation newTranslation = new Translation ();
		newTranslation.id = language;
		translations.add(newTranslation);
		
		return newTranslation;
	}

	public boolean removeTerm(Term elem) {
		boolean hasFound = false;
		for (Term subTerm : subTerms) {
			if (subTerm == elem) {
				subTerms.remove(elem);
				hasFound = true;
				break;
			} else {
				hasFound = subTerm.removeTerm(elem);
				if (hasFound)
					break;
			}
		}
		return hasFound;
	}

	public boolean addTerm(Term parentTerm, Term newTerm) {
		boolean hasFound = false;
		for (Term subTerm : subTerms) {
			if (subTerm == parentTerm) {
				subTerms.add(newTerm);
				hasFound = true;
				break;
			} else {
				hasFound = subTerm.addTerm(parentTerm, newTerm);
				if (hasFound)
					break;
			}
		}
		return hasFound;
	}
}
