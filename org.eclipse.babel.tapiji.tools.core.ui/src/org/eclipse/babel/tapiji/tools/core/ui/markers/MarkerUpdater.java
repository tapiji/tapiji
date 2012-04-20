/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.markers;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;

public class MarkerUpdater  implements IMarkerUpdater {
	
	@Override
	public String getMarkerType() {
		return "org.eclipse.core.resources.problemmarker";
	}

	@Override
	public String[] getAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateMarker(IMarker marker, IDocument document,
			Position position) {
		try {
				int start = position.getOffset();
				int end = position.getOffset() + position.getLength();
				marker.setAttribute(IMarker.CHAR_START, start);
				marker.setAttribute(IMarker.CHAR_END, end);
				return true;
			} catch (CoreException e) {
				Logger.logError(e);
				return false;
			}
	}
}
