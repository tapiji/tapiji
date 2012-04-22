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
package org.eclipse.babel.tapiji.tools.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Locale;

import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class LanguageUtils {
	private static final String INITIALISATION_STRING = PropertiesSerializer.GENERATED_BY;

	private static IFile createFile(IContainer container, String fileName,
	        IProgressMonitor monitor) throws CoreException, IOException {
		if (!container.exists()) {
			if (container instanceof IFolder) {
				((IFolder) container).create(false, false, monitor);
			}
		}

		IFile file = container.getFile(new Path(fileName));
		if (!file.exists()) {
			InputStream s = new StringBufferInputStream(INITIALISATION_STRING);
			file.create(s, true, monitor);
			s.close();
		}

		return file;
	}

	/**
	 * Checks if ResourceBundle provides a given locale. If the locale is not
	 * provided, creates a new properties-file with the ResourceBundle-basename
	 * and the index of the given locale.
	 * 
	 * @param project
	 * @param rbId
	 * @param locale
	 */
	public static void addLanguageToResourceBundle(IProject project,
	        final String rbId, final Locale locale) {
		ResourceBundleManager rbManager = ResourceBundleManager
		        .getManager(project);

		if (rbManager.getProvidedLocales(rbId).contains(locale))
			return;

		final IResource file = rbManager.getRandomFile(rbId);
		final IContainer c = ResourceUtils.getCorrespondingFolders(
		        file.getParent(), project);

		new Job("create new propertfile") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					String newFilename = ResourceBundleManager
					        .getResourceBundleName(file);
					if (locale.getLanguage() != null
					        && !locale.getLanguage().equalsIgnoreCase(
					                ResourceBundleManager.defaultLocaleTag)
					        && !locale.getLanguage().equals(""))
						newFilename += "_" + locale.getLanguage();
					if (locale.getCountry() != null
					        && !locale.getCountry().equals(""))
						newFilename += "_" + locale.getCountry();
					if (locale.getVariant() != null
					        && !locale.getCountry().equals(""))
						newFilename += "_" + locale.getVariant();
					newFilename += ".properties";

					createFile(c, newFilename, monitor);
				} catch (CoreException e) {
					Logger.logError(
					        "File for locale "
					                + locale
					                + " could not be created in ResourceBundle "
					                + rbId, e);
				} catch (IOException e) {
					Logger.logError(
					        "File for locale "
					                + locale
					                + " could not be created in ResourceBundle "
					                + rbId, e);
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * Adds new properties-files for a given locale to all ResourceBundles of a
	 * project. If a ResourceBundle already contains the language, happens
	 * nothing.
	 * 
	 * @param project
	 * @param locale
	 */
	public static void addLanguageToProject(IProject project, Locale locale) {
		ResourceBundleManager rbManager = ResourceBundleManager
		        .getManager(project);

		// Audit if all resourecbundles provide this locale. if not - add new
		// file
		for (String rbId : rbManager.getResourceBundleIdentifiers()) {
			addLanguageToResourceBundle(project, rbId, locale);
		}
	}

	private static void deleteFile(IFile file, boolean force,
	        IProgressMonitor monitor) throws CoreException {
		EditorUtils.deleteAuditMarkersForResource(file);
		file.delete(force, monitor);
	}

	/**
	 * Removes the properties-file of a given locale from a ResourceBundle, if
	 * the ResourceBundle provides the locale.
	 * 
	 * @param project
	 * @param rbId
	 * @param locale
	 */
	public static void removeFileFromResourceBundle(IProject project,
	        String rbId, Locale locale) {
		ResourceBundleManager rbManager = ResourceBundleManager
		        .getManager(project);

		if (!rbManager.getProvidedLocales(rbId).contains(locale))
			return;

		final IFile file = rbManager.getResourceBundleFile(rbId, locale);
		final String filename = file.getName();

		new Job("remove properties-file") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					deleteFile(file, true, monitor);
				} catch (CoreException e) {
					// MessageDialog.openError(Display.getCurrent().getActiveShell(),
					// "Confirm", "File could not be deleted");
					Logger.logError("File could not be deleted", e);
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * Removes all properties-files of a given locale from all ResourceBundles
	 * of a project.
	 * 
	 * @param rbManager
	 * @param locale
	 * @return
	 */
	public static void removeLanguageFromProject(IProject project, Locale locale) {
		ResourceBundleManager rbManager = ResourceBundleManager
		        .getManager(project);

		for (String rbId : rbManager.getResourceBundleIdentifiers()) {
			removeFileFromResourceBundle(project, rbId, locale);
		}

	}

}
