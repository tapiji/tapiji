/*******************************************************************************
 * Copyright (c) 2012 Michael Gasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.auditor;

import java.io.Serializable;

import org.eclipse.babel.tapiji.tools.core.extensions.ILocation;
import org.eclipse.core.resources.IFile;

public class RBLocation implements ILocation {
    private IFile file;
    private int startPos, endPos;
    private String language;
    private Serializable data;
    private ILocation sameValuePartner;

    public RBLocation(IFile file, int startPos, int endPos, String language) {
	this.file = file;
	this.startPos = startPos;
	this.endPos = endPos;
	this.language = language;
    }

    public RBLocation(IFile file, int startPos, int endPos, String language,
	    ILocation sameValuePartner) {
	this(file, startPos, endPos, language);
	this.sameValuePartner = sameValuePartner;
    }

    @Override
    public IFile getFile() {
	return file;
    }

    @Override
    public int getStartPos() {
	return startPos;
    }

    @Override
    public int getEndPos() {
	return endPos;
    }

    @Override
    public String getLiteral() {
	return language;
    }

    @Override
    public Serializable getData() {
	return data;
    }

    public void setData(Serializable data) {
	this.data = data;
    }

    public ILocation getSameValuePartner() {
	return sameValuePartner;
    }
}
