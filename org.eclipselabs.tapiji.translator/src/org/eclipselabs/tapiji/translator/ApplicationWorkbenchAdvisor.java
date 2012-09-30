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
package org.eclipselabs.tapiji.translator;

import java.lang.reflect.Constructor;
import java.util.List;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipselabs.tapiji.translator.views.widgets.provider.AbstractGlossaryLabelProvider;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.eclipselabs.tapiji.translator.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
	        IWorkbenchWindowConfigurer configurer) {
		
		AbstractWorkbenchWindowAdvisor window = null;		
		try {
			Class<?> clazz = Class.forName(AbstractWorkbenchWindowAdvisor.INSTANCE_CLASS);
			Constructor<?> constr = clazz.getConstructor(IWorkbenchWindowConfigurer.class);
			window = (AbstractWorkbenchWindowAdvisor) constr.newInstance(configurer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return window;
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(false);
	}

}
