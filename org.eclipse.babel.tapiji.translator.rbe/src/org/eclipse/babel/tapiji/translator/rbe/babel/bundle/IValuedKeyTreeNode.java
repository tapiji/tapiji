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
package org.eclipse.babel.tapiji.translator.rbe.babel.bundle;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public interface IValuedKeyTreeNode extends IKeyTreeNode {

	public void initValues(Map<Locale, String> values);

	public void addValue(Locale locale, String value);

	public void setValue(Locale locale, String newValue);

	public String getValue(Locale locale);

	public Collection<String> getValues();

	public void setInfo(Object info);

	public Object getInfo();

	public Collection<Locale> getLocales();

}
