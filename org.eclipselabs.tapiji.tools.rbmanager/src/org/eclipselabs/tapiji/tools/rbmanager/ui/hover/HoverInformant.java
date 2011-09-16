package org.eclipselabs.tapiji.tools.rbmanager.ui.hover;

import org.eclipse.swt.widgets.Composite;

public interface HoverInformant {
	
	public Composite getInfoComposite(Object data, Composite parent);
	
	public boolean show();
}
