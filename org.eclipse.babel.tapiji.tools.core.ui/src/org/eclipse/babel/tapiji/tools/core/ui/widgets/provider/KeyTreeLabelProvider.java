/*******************************************************************************
 * Copyright (c) 2012 Alexej Strelzow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexej Strelzow - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.widgets.provider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for key tree viewer.
 * 
 * @author Alexej Strelzow
 */
public abstract class KeyTreeLabelProvider extends StyledCellLabelProvider
        implements IFontProvider, IColorProvider {

    public KeyTreeLabelProvider() {
        setOwnerDrawEnabled(true);
    }

    /**
     * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
     */
    public Font getFont(Object element) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
     */
    public Color getForeground(Object element) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
     */
    public Color getBackground(Object element) {
        return null;
    }

    public abstract String getColumnText(Object element, int columnIndex);

    public abstract Image getColumnImage(Object element, int columnIndex);

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ViewerCell cell) {
        cell.setText(getColumnText(cell.getElement(), cell.getColumnIndex()));
        cell.setImage(getColumnImage(cell.getElement(), cell.getColumnIndex()));
        super.update(cell);
    }

}
