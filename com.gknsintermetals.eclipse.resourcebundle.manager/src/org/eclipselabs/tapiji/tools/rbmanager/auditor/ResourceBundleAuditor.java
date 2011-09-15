package org.eclipselabs.tapiji.tools.rbmanager.auditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipselabs.tapiji.tools.core.extensions.I18nRBAuditor;
import org.eclipselabs.tapiji.tools.core.extensions.ILocation;
import org.eclipselabs.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.model.preferences.TapiJIPreferences;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;
import org.eclipselabs.tapiji.tools.rbmanager.auditor.quickfix.MissingLanguageResolution;
import org.eclipselabs.tapiji.translator.rbe.model.bundle.IBundleEntry;
import org.eclipselabs.tapiji.translator.rbe.model.bundle.IBundleGroup;

/**
 * 
 */
public class ResourceBundleAuditor extends I18nRBAuditor {
	private static final String LANGUAGE_ATTRIBUTE = "key";

	private List<ILocation> unspecifiedKeys = new LinkedList<ILocation>();
	private Map<ILocation, ILocation> sameValues = new HashMap<ILocation, ILocation>();
	private List<ILocation> missingLanguages = new LinkedList<ILocation>();
	private List<String> seenRBs = new LinkedList<String>();


	@Override
	public String[] getFileEndings() {
		return new String[] { "properties" };
	}

	@Override
	public String getContextId() {
		return "resourcebundle";
	}

	@Override
	public List<ILocation> getUnspecifiedKeyReferences() {
		return unspecifiedKeys;
	}

	@Override
	public Map<ILocation, ILocation> getSameValuesReferences() {
		return sameValues;
	}

	@Override
	public List<ILocation> getMissingLanguageReferences() {
		return missingLanguages;
	}

	/*
	 * Forgets all save rbIds in seenRB and reset all problem-lists
	 */
	@Override
	public void resetProblems() {
		unspecifiedKeys = new LinkedList<ILocation>();
		sameValues = new HashMap<ILocation, ILocation>();
		missingLanguages = new LinkedList<ILocation>();
		seenRBs = new LinkedList<String>();
	}

	/*
	 * Finds the corresponding ResouceBundle for the resource. Checks if the
	 * ResourceBundle is already audit and it is not already executed audit the
	 * hole ResourceBundle and save the rbId in seenRB
	 */
	@Override
	public void audit(IResource resource) {
		if (!RBFileUtils.isResourceBundleFile(resource))
			return;

		IFile file = (IFile) resource;
		String rbId = RBFileUtils.getCorrespondingResourceBundleId(file);

		if (!seenRBs.contains(rbId)) {
			ResourceBundleManager rbmanager = ResourceBundleManager
					.getManager(file.getProject());
			audit(rbId, rbmanager);
			seenRBs.add(rbId);
		} else
			return;
	}

	/*
	 * audit all files of a resourcebundle
	 */
	public void audit(String rbId, ResourceBundleManager rbmanager) {
		IBundleGroup bundlegroup = rbmanager.getResourceBundle(rbId);
		Collection<IResource> bundlefile = rbmanager.getResourceBundles(rbId);
		Set<String> keys = bundlegroup.getKeys();

		
		for (IResource r : bundlefile) {
			IFile f1 = (IFile) r;
			
			for (String key : keys) {
				// check if all keys have a value
				
				if (auditUnspecifiedKey(f1, key, bundlegroup)){
					/* do nothing - all just done*/
				}else {
					
					// check if a key has the same value like a key of an other properties-file
					if (TapiJIPreferences.getAuditSameValue() && bundlefile.size() > 1)
						for (IResource r2 : bundlefile) {
							IFile f2 = (IFile) r2;
							auditSameValues(f1, f2, key, bundlegroup);
						}
				}
			}
		}
		
		if (TapiJIPreferences.getAuditMissingLanguage()){
			// check if the resourcebundle supports all project-languages
			Set<Locale> rbLocales = rbmanager.getProvidedLocales(rbId);
			Set<Locale> projectLocales = rbmanager.getProjectProvidedLocales();
			
			auditMissingLanguage(rbLocales, projectLocales, rbmanager, rbId);
		}
	}

	/*
	 * Audit the file if the key is not specified. If the value is null report a
	 * problem.
	 */
	private boolean auditUnspecifiedKey(IFile f1, String key,
			IBundleGroup bundlegroup) {
		if (bundlegroup.getBundleEntry(RBFileUtils.getLocale(f1), key) == null) {
			int pos = calculateKeyLine(key, f1);
			unspecifiedKeys.add(new RBLocation(f1, pos, pos + 1, key));
			return true;
		} else
			return false;
	}

	/*
	 * Compare a key in different files and report a problem if the value is the
	 * same.
	 */
	private void auditSameValues(IFile f1, IFile f2, String key,
			IBundleGroup bundlegroup) {
		Locale l1 = RBFileUtils.getLocale(f1);
		Locale l2 = RBFileUtils.getLocale(f2);

		// TODO don't audit the Default-file
		if (!l2.equals(l1)) {
			IBundleEntry bundleentry = bundlegroup.getBundleEntry(l2, key);

			if (bundleentry != null)
				if (bundlegroup.getBundleEntry(l1, key).getValue()
						.equals(bundleentry.getValue())) {
					int pos1 = calculateKeyLine(key, f1);
					int pos2 = calculateKeyLine(key, f2);
					sameValues.put(new RBLocation(f1, pos1, pos1 + 1, key),
							new RBLocation(f2, pos2, pos2 + 1, key));
				}
		}
	}

	/*
	 * check if the resourcebundle supports all project-languages and report
	 * missing languages.
	 */
	private void auditMissingLanguage(Set<Locale> rbLocales,
			Set<Locale> projectLocales, ResourceBundleManager rbmanager,
			String rbId) {
		for (Locale pLocale : projectLocales) {
			if (!rbLocales.contains(pLocale)) {
				String language;
				if (!pLocale.toString().equals(""))
					language = pLocale.toString();
				else
					language = "[Default]";

				// Add Warning to default-file or a random chosen file
				IResource representative = rbmanager.getResourceBundleFile(
						rbId, new Locale(""));
				if (representative == null)
					representative = rbmanager.getRandomFile(rbId);
				missingLanguages.add(new RBLocation((IFile) representative, 1,
						2, language));
			}
		}
	}

	/*
	 * Find a position the key is located or missing
	 */
	private int calculateKeyLine(String key, IFile file) {
		int linenumber = 1;
		try {
			InputStream is = file.getContents();
			BufferedReader bf = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = bf.readLine()) != null) {
				if ((!line.isEmpty()) && (!line.startsWith("#"))
						&& (line.compareTo(key) > 0))
					return linenumber;
				linenumber++;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return linenumber;
	}

	@Override
	public List<IMarkerResolution> getMarkerResolutions(IMarker marker) {
		List<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

		switch (marker.getAttribute("cause", -1)) {
		case IMarkerConstants.CAUSE_MISSING_LANGUAGE:
			Locale l = new Locale(marker.getAttribute(LANGUAGE_ATTRIBUTE, "")); // TODO
																				// change
																				// Name
			resolutions.add(new MissingLanguageResolution(l));
			break;
		}

		return resolutions;
	}

}
