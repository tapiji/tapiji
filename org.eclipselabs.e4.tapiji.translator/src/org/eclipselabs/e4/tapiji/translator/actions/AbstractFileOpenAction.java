package org.eclipselabs.e4.tapiji.translator.actions;
///*******************************************************************************
// * Copyright (c) 2012 TapiJI.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// * 
// * Contributors:
// *     Martin Reiterer - initial API and implementation
// ******************************************************************************/
//package org.eclipselabs.tapiji.translator.actions;
//
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.IAction;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.IWorkbenchWindowActionDelegate;
//
//public abstract class AbstractFileOpenAction extends Action implements
//        IWorkbenchWindowActionDelegate {
//
//	/** Editor ids **/
//	public static final String RESOURCE_BUNDLE_EDITOR = "com.essiembre.rbe.eclipse.editor.ResourceBundleEditor";
//
//	public static final String INSTANCE_CLASS = "org.eclipselabs.tapiji.translator.actions.FileOpenAction";
//	
//	protected IWorkbenchWindow window;
//
//	@Override
//	public abstract void run(IAction action);
//
//	@Override
//	public void selectionChanged(IAction action, ISelection selection) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void dispose() {
//		window = null;
//	}
//
//	@Override
//	public void init(IWorkbenchWindow window) {
//		this.window = window;
//	}
//
//}
