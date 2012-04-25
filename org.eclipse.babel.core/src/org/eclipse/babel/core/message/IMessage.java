/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.core.message;

import java.util.Locale;

public interface IMessage {

	String getKey();

	String getValue();

	Locale getLocale();

	String getComment();

	boolean isActive();

	String toString();

	void setActive(boolean active);

	void setComment(String comment);

	void setComment(String comment, boolean silent);

	void setText(String test);

	void setText(String test, boolean silent);

}
