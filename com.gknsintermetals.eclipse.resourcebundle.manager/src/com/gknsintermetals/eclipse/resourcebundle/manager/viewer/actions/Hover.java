package com.gknsintermetals.eclipse.resourcebundle.manager.viewer.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;


public class Hover {
	private Shell hoverShell;
	private Point hoverPosition;
	private Label infoLabelTitel, infoLabelText;
	private HoverInformant informant;
	
	
	public Hover(Shell parent, HoverInformant informant){
		this.informant = informant;
		hoverShell = new Shell(parent, SWT.ON_TOP | SWT.TOOL);
		Display display = hoverShell.getDisplay();
		
		GridLayout gridLayout = new GridLayout();
		hoverShell.setLayout(gridLayout);
		
		hoverShell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		infoLabelTitel = new Label(hoverShell, SWT.NONE);
		infoLabelText = new Label(hoverShell, SWT.NONE);
		
		infoLabelTitel.setForeground(display
				.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		infoLabelTitel.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		infoLabelText.setForeground(display
				.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		infoLabelText.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	}
	
	public void init(String titel, String text){
		infoLabelTitel.setText(titel);
		infoLabelText.setText(text);
	}
	
	private void setHoverLocation(Shell shell, Point position) {
		Rectangle displayBounds = shell.getDisplay().getBounds();
		Rectangle shellBounds = shell.getBounds();
		shellBounds.x = Math.max(
				Math.min(position.x, displayBounds.width - shellBounds.width), 0);
		shellBounds.y = Math.max(
				Math.min(position.y + 16, displayBounds.height - shellBounds.height), 0);
		shell.setBounds(shellBounds);
	}

	
	public void activateHoverHelp(final Control control) {

		control.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (hoverShell != null && hoverShell.isVisible()){
					hoverShell.setVisible(false);
				}
			}
		});

		control.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseExit(MouseEvent e) {
				if (hoverShell != null && hoverShell.isVisible())
					hoverShell.setVisible(false);
			}

			public void mouseHover(MouseEvent event) {				
				Point pt = new Point(event.x, event.y);
				Widget widget = event.widget;
				if (widget instanceof ToolBar) {
					ToolBar w = (ToolBar) widget;
					widget = w.getItem(pt);
				}
				if (widget instanceof Table) {
					Table w = (Table) widget;
					widget = w.getItem(pt);
				}
				if (widget instanceof Tree) {
					Tree w = (Tree) widget;
					widget = w.getItem(pt);
				}
				if (widget == null) {
					hoverShell.setVisible(false);
					return;
				}
				
				hoverPosition = control.toDisplay(pt);
				setHoverLocation(hoverShell, hoverPosition);
				
				Object data =  widget.getData();
				String titel = informant.getTitel(data);
				String text = informant.getText(data);
				
				init(titel, text);
				
				hoverShell.pack();
				if (informant.show()) hoverShell.setVisible(true);
				else hoverShell.setVisible(false);
			}
		});
	}
}
