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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


@XmlAccessorType(XmlAccessType.FIELD)
public class Info implements Serializable {

  private static final long serialVersionUID = 8607746669906026928L;

  @XmlElementWrapper(name = "locales")
  @XmlElement(name = "locale")
  public List<String> translations;

  public Info() {
    this.translations = new ArrayList<String>();
    this.translations.add("Default");
  }

  public String[] getTranslations() {
    return translations.toArray(new String[translations.size()]);
  }

  @Override
  public String toString() {
    return "Info [translations=" + translations + "]";
  }
}
