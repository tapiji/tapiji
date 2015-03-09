/*******************************************************************************
 * Copyright (c) 2012 TapiJI. All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.widgets.provider;


import java.util.List;
import org.eclipse.babel.core.message.IMessage;
import org.eclipse.babel.core.message.tree.IKeyTreeNode;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
/*
 * import org.eclipse.ui.ISelectionListener; import org.eclipse.ui.IWorkbenchPage; import org.eclipse.ui.IWorkbenchPart;
 */
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.views.widgets.filter.FilterInfo;
import org.eclipselabs.e4.tapiji.utils.FontUtils;


// import org.eclipse.babel.editor.api.EditorUtil;

public abstract class AbstractGlossaryLabelProvider extends StyledCellLabelProvider implements ISelectionListener,
        ISelectionChangedListener {

  private static final long serialVersionUID = -1833407818565507359L;
  public static final String INSTANCE_CLASS = "org.eclipselabs.tapiji.translator.views.widgets.provider.GlossaryLabelProvider";

  protected boolean searchEnabled = false;
  protected int referenceColumn = 0;
  protected List<String> translations;
  protected IKeyTreeNode selectedItem;

  /*** COLORS ***/
  protected Color gray = FontUtils.getSystemColor(SWT.COLOR_GRAY);
  protected Color black = FontUtils.getSystemColor(SWT.COLOR_BLACK);
  protected Color info_color = FontUtils.getSystemColor(SWT.COLOR_YELLOW);
  protected Color info_crossref = FontUtils.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
  protected Color info_crossref_foreground = FontUtils.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
  protected Color transparent = FontUtils.getSystemColor(SWT.COLOR_WHITE);

  /*** FONTS ***/
  protected Font bold = FontUtils.createFont(SWT.BOLD);
  protected Font bold_italic = FontUtils.createFont(SWT.ITALIC);

  public void setSearchEnabled(boolean b) {
    this.searchEnabled = b;
  }

  public AbstractGlossaryLabelProvider(int referenceColumn, List<String> translations
  // , IWorkbenchPage page
  ) {
    this.referenceColumn = referenceColumn;
    this.translations = translations;
    /*
     * if (page.getActiveEditor() != null) { selectedItem = EditorUtil.getSelectedKeyTreeNode(page); }
     */
  }

  public String getColumnText(Object element, int columnIndex) {
    try {
      Term term = (Term) element;
      if (term != null) {
        Translation transl = term.getTranslation(this.translations.get(columnIndex));
        return transl != null ? transl.value : "";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public boolean isSearchEnabled() {
    return this.searchEnabled;
  }

  protected boolean isMatchingToPattern(Object element, int columnIndex) {
    boolean matching = false;

    if (element instanceof Term) {
      Term term = (Term) element;

      if (term.getInfo() == null)
        return false;

      FilterInfo filterInfo = (FilterInfo) term.getInfo();

      matching = filterInfo.hasFoundInTranslation(translations.get(columnIndex));
    }

    return matching;
  }

  protected boolean isCrossRefRegion(String cellText) {
    if (selectedItem != null) {
      for (IMessage entry : selectedItem.getMessagesBundleGroup().getMessages(selectedItem.getMessageKey())) {
        String value = entry.getValue();
        String[] subValues = value.split("[\\s\\p{Punct}]+");
        for (String v : subValues) {
          if (v.trim().equalsIgnoreCase(cellText.trim()))
            return true;
        }
      }
    }

    return false;
  }

  protected Font getColumnFont(Object element, int columnIndex) {
    if (columnIndex == 0) {
      return bold_italic;
    }
    return null;
  }

  /*
   * @Override public void selectionChanged(IWorkbenchPart part, ISelection selection) { try { if (selection.isEmpty())
   * return; if (!(selection instanceof IStructuredSelection)) return; IStructuredSelection sel = (IStructuredSelection)
   * selection; selectedItem = (IKeyTreeNode) sel.iterator().next(); this.getViewer().refresh(); } catch (Exception e) {
   * // silent catch } }
   */

  @Override
  public void selectionChanged(SelectionChangedEvent event) {
    event.getSelection();
  }

}
