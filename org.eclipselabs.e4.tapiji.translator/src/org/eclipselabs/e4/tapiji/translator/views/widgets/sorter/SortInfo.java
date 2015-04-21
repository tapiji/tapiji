/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.widgets.sorter;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public final class SortInfo {

    private int columnIndex;
    private boolean isDescending;
    private List<Locale> visibleLocales;

    public SortInfo() {
        this.columnIndex = 0;
        this.isDescending = false;
        this.visibleLocales = new ArrayList<>();
    }

    public void setDescending(final boolean isDescending) {
        this.isDescending = isDescending;
    }

    public boolean isDescending() {
        return this.isDescending;
    }

    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public void setVisibleLocales(final List<Locale> visibleLocales) {
        this.visibleLocales = visibleLocales;
    }

    public List<Locale> getVisibleLocales() {
        return this.visibleLocales;
    }

    @Override
    public String toString() {
        return "SortInfo [columnIndex=" + columnIndex + ", isDescending=" + isDescending + ", visibleLocales="
                        + visibleLocales + "]";
    }
}
