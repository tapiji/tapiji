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
package org.eclipselabs.e4.tapiji.translator.model;


import java.io.File;


public final class LoadGlossaryEvent {

  private boolean isNewGlossary;

  private File glossaryFile;

  public LoadGlossaryEvent(final File glossaryFile) {
    this.glossaryFile = glossaryFile;
  }

  public LoadGlossaryEvent(final File glossaryFile, final boolean isNewGlossary) {
    this.glossaryFile = glossaryFile;
    this.isNewGlossary = isNewGlossary;
  }

  public File getGlossaryFile() {
    return glossaryFile;
  }

  public boolean isNewGlossary() {
    return isNewGlossary;
  }

  public void setGlossaryFile(final File glossaryFile) {
    this.glossaryFile = glossaryFile;
  }

  @Override
  public String toString() {
    return "LoadGlossaryEvent [isNewGlossary=" + isNewGlossary + ", glossaryFile=" + glossaryFile + "]";
  }
}
