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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Translation")
public class Translation implements Serializable {

    private static final long serialVersionUID = 2033276999496196690L;

    public String id;

    public String value;

    private Translation() {
        this.id = "";
        this.value = "";
    }

    private Translation(final String id, final String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Translation [id=" + id + ", value=" + value + "]";
    }

    public static Translation newInstance() {
        return new Translation();
    }

    public static Translation newInstance(final String id, final String value) {
        return new Translation(id, value);
    }
}