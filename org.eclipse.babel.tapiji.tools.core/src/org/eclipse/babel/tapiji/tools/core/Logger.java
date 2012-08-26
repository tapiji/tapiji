/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Logger {

    public static void logInfo(String message) {
	log(IStatus.INFO, IStatus.OK, message, null);
    }

    public static void logError(Throwable exception) {
	logError("Unexpected Exception", exception);
    }

    public static void logError(String message, Throwable exception) {
	log(IStatus.ERROR, IStatus.OK, message, exception);
    }

    public static void log(int severity, int code, String message,
	    Throwable exception) {
	log(createStatus(severity, code, message, exception));
    }

    public static IStatus createStatus(int severity, int code, String message,
	    Throwable exception) {
	return new Status(severity, Activator.PLUGIN_ID, code, message,
		exception);
    }

    public static void log(IStatus status) {
	Activator.getDefault().getLog().log(status);
    }
}
